package com.yatrimitra.data.repository

import com.yatrimitra.data.dao.*
import com.yatrimitra.data.entity.*
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class RouteRepository(private val routeDao: RouteDao) {
    val activeRoutes: Flow<List<RouteEntity>> = routeDao.observeActiveRoutes()
    fun observeById(routeId: String): Flow<RouteEntity?> = routeDao.observeById(routeId)
    suspend fun getById(routeId: String): RouteEntity? = routeDao.getById(routeId)
    suspend fun insert(route: RouteEntity) = routeDao.insert(route)
}

class StopRepository(private val stopDao: StopDao) {
    fun observeByRoute(routeId: String): Flow<List<StopEntity>> = stopDao.observeByRoute(routeId)
    suspend fun getByRoute(routeId: String): List<StopEntity> = stopDao.getByRoute(routeId)
    suspend fun getById(stopId: String): StopEntity? = stopDao.getById(stopId)
}

class VehicleRepository(
    private val vehicleDao: VehicleDao,
    private val positionDao: VehiclePositionDao
) {
    fun observeByRoute(routeId: String): Flow<List<VehicleEntity>> =
        vehicleDao.observeByRoute(routeId)

    suspend fun insert(vehicle: VehicleEntity) = vehicleDao.insert(vehicle)

    suspend fun deleteById(vehicleId: String) {
        vehicleDao.deleteById(vehicleId)
        positionDao.deleteByVehicle(vehicleId)
    }

    suspend fun upsertPosition(pos: VehiclePositionEntity) = positionDao.upsert(pos)

    fun observePositions(routeId: String): Flow<List<VehiclePositionEntity>> =
        positionDao.observeByRoute(routeId)
}

class RiderRepository(private val riderDao: RiderDao) {
    fun observeAtStop(stopId: String): Flow<List<RiderEntity>> = riderDao.observeAtStop(stopId)

    suspend fun register(name: String, stop: StopEntity): String {
        val riderId = UUID.randomUUID().toString()
        riderDao.insert(
            RiderEntity(
                riderId = riderId,
                name = name.ifBlank { "Rider" },
                stopId = stop.stopId,
                stopName = stop.name
            )
        )
        return riderId
    }
}

class ArrivalRepository(private val arrivalDao: ArrivalDao) {
    fun observeAtStop(stopId: String): Flow<List<ArrivalEntity>> =
        arrivalDao.observeAtStop(stopId)

    fun observeRecent(): Flow<List<ArrivalEntity>> = arrivalDao.observeRecent()

    val totalArrivalCount: Flow<Int> = arrivalDao.observeTotalCount()

    suspend fun log(vehicleId: String, stop: StopEntity) =
        arrivalDao.insert(
            ArrivalEntity(vehicleId = vehicleId, stopId = stop.stopId, stopName = stop.name)
        )
}
