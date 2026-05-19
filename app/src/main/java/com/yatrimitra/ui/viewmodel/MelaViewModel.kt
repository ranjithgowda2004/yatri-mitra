package com.yatrimitra.ui.viewmodel

import androidx.lifecycle.*
import com.yatrimitra.data.entity.*
import com.yatrimitra.data.repository.*
import com.yatrimitra.util.SimulationEngine
import com.yatrimitra.util.VehicleState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}

data class SimStats(
    val routeCount: Int = 0,
    val vehicleCount: Int = 0,
    val arrivalCount: Int = 0
)

class MelaViewModel(
    private val routeRepo: RouteRepository,
    private val stopRepo: StopRepository,
    private val vehicleRepo: VehicleRepository,
    private val riderRepo: RiderRepository,
    private val arrivalRepo: ArrivalRepository
) : ViewModel() {

    // ── Routes ────────────────────────────────────────────────────────────────

    val routes: StateFlow<List<RouteEntity>> = routeRepo.activeRoutes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _selectedRouteId = MutableStateFlow<String?>(null)
    val selectedRouteId: StateFlow<String?> = _selectedRouteId.asStateFlow()

    val selectedRoute: StateFlow<RouteEntity?> = _selectedRouteId
        .flatMapLatest { id ->
            if (id != null) routeRepo.observeById(id) else flowOf(null)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun selectRoute(routeId: String) {
        if (_selectedRouteId.value == routeId) return
        _selectedRouteId.value = routeId
        _selectedStop.value = null
        initEngineForRoute(routeId)
    }

    // ── Stops ─────────────────────────────────────────────────────────────────

    val stops: StateFlow<List<StopEntity>> = _selectedRouteId
        .flatMapLatest { id ->
            if (id != null) stopRepo.observeByRoute(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _selectedStop = MutableStateFlow<StopEntity?>(null)
    val selectedStop: StateFlow<StopEntity?> = _selectedStop.asStateFlow()

    fun selectStop(stop: StopEntity) {
        _selectedStop.value = stop
        // Tell all vehicles to compute ETA for this stop
        _vehicles.values.forEach { vehicleId ->
            _engine?.setWatchedStop(vehicleId, stop.stopId)
        }
    }

    // Keeps track of vehicle IDs currently in engine
    private val _vehicles = mutableMapOf<String, String>() // vehicleId -> vehicleId

    // ── Vehicle positions (Room-backed) ───────────────────────────────────────

    val vehiclePositions: StateFlow<List<VehiclePositionEntity>> = _selectedRouteId
        .flatMapLatest { id ->
            if (id != null) vehicleRepo.observePositions(id) else flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    // ── Engine state ──────────────────────────────────────────────────────────

    private var _engine: SimulationEngine? = null
    private var engineJob: Job? = null

    private val _vehicleStates = MutableStateFlow<List<VehicleState>>(emptyList())
    val vehicleStates: StateFlow<List<VehicleState>> = _vehicleStates.asStateFlow()

    val nearestEtaSeconds: StateFlow<Float?> = combine(
        _vehicleStates, _selectedStop
    ) { states, stop ->
        if (stop == null) return@combine null
        states
            .mapNotNull { it.etaSeconds }
            .filter { it >= 0f }
            .minOrNull()
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    // ── Arrivals ──────────────────────────────────────────────────────────────

    val totalArrivals: StateFlow<Int> = arrivalRepo.totalArrivalCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val recentArrivals: StateFlow<List<ArrivalEntity>> = arrivalRepo.observeRecent()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _arrivalEvents = MutableSharedFlow<Pair<String, StopEntity>>(extraBufferCapacity = 16)
    val arrivalEvents: SharedFlow<Pair<String, StopEntity>> = _arrivalEvents.asSharedFlow()

    // ── Stats ─────────────────────────────────────────────────────────────────

    val stats: StateFlow<SimStats> = combine(
        routes, _vehicleStates, totalArrivals
    ) { r, v, a ->
        SimStats(routeCount = r.size, vehicleCount = v.size, arrivalCount = a)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SimStats())

    // ── Simulation control ────────────────────────────────────────────────────

    fun startSimulation()  { _engine?.resume() }
    fun pauseSimulation()  { _engine?.pause() }

    fun setSpeed(speedKmh: Float) {
        _vehicleStates.value.forEach { state ->
            _engine?.setSpeed(state.vehicleId, speedKmh)
        }
    }

    fun addVehicle() {
        val route = selectedRoute.value ?: return
        val stopList = stops.value
        val num = (_vehicleStates.value.size + 1).toString().padStart(3, '0')
        val vehicleId = "AUTO-$num"

        viewModelScope.launch {
            vehicleRepo.insert(VehicleEntity(vehicleId = vehicleId, routeId = route.routeId))
            _vehicles[vehicleId] = vehicleId
            _engine?.addVehicle(
                vehicleId = vehicleId,
                routeId = route.routeId,
                initialPosition = (Math.random() * 0.6).toFloat(),
                speedKmh = 25f + (Math.random() * 15).toFloat(),
                stops = stopList
            )
            // Apply current watched stop
            _selectedStop.value?.let { stop ->
                _engine?.setWatchedStop(vehicleId, stop.stopId)
            }
        }
    }

    fun registerRider(name: String) {
        val stop = _selectedStop.value ?: return
        viewModelScope.launch { riderRepo.register(name, stop) }
    }

    // ── Engine init ───────────────────────────────────────────────────────────

    private fun initEngineForRoute(routeId: String) {
        // Cancel previous engine
        _engine?.stop()
        engineJob?.cancel()
        _vehicles.clear()
        _vehicleStates.value = emptyList()

        engineJob = viewModelScope.launch {
            val route = routeRepo.getById(routeId) ?: return@launch
            val stopList = stopRepo.getByRoute(routeId)

            val engine = SimulationEngine(
                routeLengthKm = route.lengthKm,
                tickIntervalMs = SimulationEngine.TICK_MS,
                scope = viewModelScope
            )
            _engine = engine

            // Collect engine state → update UI + persist to Room
            launch {
                var persistTick = 0
                engine.stateFlow.collect { states ->
                    _vehicleStates.value = states
                    persistTick++
                    // Persist to Room every 5 ticks (500 ms)
                    if (persistTick % 5 == 0) {
                        states.forEach { state ->
                            vehicleRepo.upsertPosition(
                                VehiclePositionEntity(
                                    vehicleId = state.vehicleId,
                                    routeId = state.routeId,
                                    position = state.position,
                                    speedKmh = state.speedKmh
                                )
                            )
                        }
                    }
                }
            }

            // Collect arrivals → log to DB + notify UI
            launch {
                engine.arrivals.collect { event ->
                    arrivalRepo.log(event.vehicleId, event.stop)
                    if (event.stop.stopId == _selectedStop.value?.stopId) {
                        _arrivalEvents.emit(event.vehicleId to event.stop)
                    }
                }
            }

            // Add 2 default vehicles
            repeat(2) { i ->
                val vehicleId = "AUTO-${(i + 1).toString().padStart(3, '0')}"
                vehicleRepo.insert(VehicleEntity(vehicleId = vehicleId, routeId = routeId))
                _vehicles[vehicleId] = vehicleId
                engine.addVehicle(
                    vehicleId = vehicleId,
                    routeId = routeId,
                    initialPosition = i * 0.35f,
                    speedKmh = 28f + i * 6f,
                    stops = stopList
                )
            }

            engine.start()
        }
    }

    override fun onCleared() {
        super.onCleared()
        _engine?.stop()
        engineJob?.cancel()
    }
}
