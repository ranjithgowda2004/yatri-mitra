package com.yatrimitra.util

import com.yatrimitra.data.entity.StopEntity
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlin.math.abs

data class VehicleState(
    val vehicleId: String,
    val routeId: String,
    val position: Float,
    val speedKmh: Float,
    val etaSeconds: Float? = null
)

data class ArrivalEvent(
    val vehicleId: String,
    val stop: StopEntity,
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Drives vehicle movement along a route and publishes state via [StateFlow].
 *
 * ETA formula:
 *   remainingKm  = (stopPosition - vehiclePosition) × routeLengthKm
 *   etaSeconds   = (remainingKm / speedKmh) × 3600
 *
 * Smooth render interpolation:
 *   lerp(s, e, t) = s + (e − s) × t
 */
class SimulationEngine(
    private val routeLengthKm: Float,
    private val tickIntervalMs: Long = TICK_MS,
    private val scope: CoroutineScope
) {
    companion object {
        const val TICK_MS = 100L

        fun lerp(start: Float, end: Float, t: Float): Float =
            start + (end - start) * t.coerceIn(0f, 1f)

        fun calculateEtaSeconds(
            stopPosition: Float,
            vehiclePosition: Float,
            routeLengthKm: Float,
            speedKmh: Float
        ): Float {
            if (vehiclePosition >= stopPosition || speedKmh <= 0f) return 0f
            val remainingKm = (stopPosition - vehiclePosition) * routeLengthKm
            return (remainingKm / speedKmh) * 3600f
        }

        fun formatEta(seconds: Float): String {
            if (seconds <= 0f) return "HERE"
            val m = (seconds / 60).toInt()
            val s = (seconds % 60).toInt()
            return "$m:${s.toString().padStart(2, '0')}"
        }

        fun distanceKm(posA: Float, posB: Float, routeLengthKm: Float): Float =
            abs(posA - posB) * routeLengthKm
    }

    private val _vehicles = mutableMapOf<String, MutableVehicleState>()

    private val _stateFlow = MutableStateFlow<List<VehicleState>>(emptyList())
    val stateFlow: StateFlow<List<VehicleState>> = _stateFlow.asStateFlow()

    private val _arrivals = MutableSharedFlow<ArrivalEvent>(extraBufferCapacity = 64)
    val arrivals: SharedFlow<ArrivalEvent> = _arrivals.asSharedFlow()

    var isPaused: Boolean = false
    private var job: Job? = null

    fun start() {
        if (job?.isActive == true) return
        job = scope.launch {
            while (isActive) {
                if (!isPaused) tick()
                delay(tickIntervalMs)
            }
        }
    }

    fun pause()  { isPaused = true }
    fun resume() { isPaused = false }

    fun stop() {
        job?.cancel()
        job = null
    }

    fun addVehicle(
        vehicleId: String,
        routeId: String,
        initialPosition: Float = 0f,
        speedKmh: Float = 30f,
        stops: List<StopEntity>
    ) {
        _vehicles[vehicleId] = MutableVehicleState(
            vehicleId = vehicleId,
            routeId = routeId,
            position = initialPosition,
            speedKmh = speedKmh,
            stops = stops.toMutableList()
        )
    }

    fun removeVehicle(vehicleId: String) { _vehicles.remove(vehicleId) }

    fun setSpeed(vehicleId: String, speedKmh: Float) {
        _vehicles[vehicleId]?.speedKmh = speedKmh
    }

    fun setWatchedStop(vehicleId: String, stopId: String?) {
        _vehicles[vehicleId]?.watchedStopId = stopId
    }

    private fun tick() {
        // 100ms in hours
        val dtHours = tickIntervalMs / 3_600_000f
        val snapshot = mutableListOf<VehicleState>()

        _vehicles.values.forEach { v ->
            // Move: Δpos = speed[km/h] × dt[h] / routeLength[km]  ×50 scale for demo speed
            val delta = (v.speedKmh * dtHours / routeLengthKm) * 50f
            v.position = (v.position + delta).coerceAtMost(1f)

            // Check arrivals
            v.stops.forEach { stop ->
                if (!v.passedStops.contains(stop.stopId) && v.position >= stop.position) {
                    v.passedStops.add(stop.stopId)
                    scope.launch { _arrivals.emit(ArrivalEvent(v.vehicleId, stop)) }
                }
            }

            // Loop back
            if (v.position >= 1f) {
                v.position = 0f
                v.passedStops.clear()
            }

            // ETA to watched stop
            val eta = v.watchedStopId?.let { sid ->
                v.stops.find { it.stopId == sid }?.let { stop ->
                    calculateEtaSeconds(stop.position, v.position, routeLengthKm, v.speedKmh)
                }
            }

            snapshot.add(
                VehicleState(
                    vehicleId = v.vehicleId,
                    routeId = v.routeId,
                    position = v.position,
                    speedKmh = v.speedKmh,
                    etaSeconds = eta
                )
            )
        }
        _stateFlow.value = snapshot
    }

    private data class MutableVehicleState(
        val vehicleId: String,
        val routeId: String,
        var position: Float,
        var speedKmh: Float,
        val stops: MutableList<StopEntity>,
        val passedStops: MutableSet<String> = mutableSetOf(),
        var watchedStopId: String? = null
    )
}
