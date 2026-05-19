package com.yatrimitra.data.dao

import androidx.room.*
import com.yatrimitra.data.entity.*
import kotlinx.coroutines.flow.Flow

// ── RouteDao ──────────────────────────────────────────────────────────────────

@Dao
interface RouteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(route: RouteEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(routes: List<RouteEntity>)

    @Update
    suspend fun update(route: RouteEntity)

    @Delete
    suspend fun delete(route: RouteEntity)

    @Query("SELECT * FROM routes WHERE isActive = 1 ORDER BY name ASC")
    fun observeActiveRoutes(): Flow<List<RouteEntity>>

    @Query("SELECT * FROM routes WHERE isActive = 1 ORDER BY name ASC")
    suspend fun getActiveRoutes(): List<RouteEntity>

    @Query("SELECT * FROM routes WHERE routeId = :routeId LIMIT 1")
    suspend fun getById(routeId: String): RouteEntity?

    @Query("SELECT * FROM routes WHERE routeId = :routeId LIMIT 1")
    fun observeById(routeId: String): Flow<RouteEntity?>

    @Query("SELECT COUNT(*) FROM routes")
    suspend fun getCount(): Int
}

// ── StopDao ───────────────────────────────────────────────────────────────────

@Dao
interface StopDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(stop: StopEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(stops: List<StopEntity>)

    @Update
    suspend fun update(stop: StopEntity)

    @Delete
    suspend fun delete(stop: StopEntity)

    @Query("SELECT * FROM stops WHERE routeId = :routeId ORDER BY position ASC")
    fun observeByRoute(routeId: String): Flow<List<StopEntity>>

    @Query("SELECT * FROM stops WHERE routeId = :routeId ORDER BY position ASC")
    suspend fun getByRoute(routeId: String): List<StopEntity>

    @Query("SELECT * FROM stops WHERE stopId = :stopId LIMIT 1")
    suspend fun getById(stopId: String): StopEntity?
}

// ── VehicleDao ────────────────────────────────────────────────────────────────

@Dao
interface VehicleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(vehicle: VehicleEntity)

    @Delete
    suspend fun delete(vehicle: VehicleEntity)

    @Query("SELECT * FROM vehicles WHERE routeId = :routeId ORDER BY vehicleId ASC")
    fun observeByRoute(routeId: String): Flow<List<VehicleEntity>>

    @Query("SELECT * FROM vehicles WHERE routeId = :routeId")
    suspend fun getByRoute(routeId: String): List<VehicleEntity>

    @Query("DELETE FROM vehicles WHERE vehicleId = :vehicleId")
    suspend fun deleteById(vehicleId: String)
}

// ── VehiclePositionDao ────────────────────────────────────────────────────────

@Dao
interface VehiclePositionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(position: VehiclePositionEntity)

    @Query("SELECT * FROM vehicle_positions WHERE routeId = :routeId")
    fun observeByRoute(routeId: String): Flow<List<VehiclePositionEntity>>

    @Query("SELECT * FROM vehicle_positions WHERE routeId = :routeId")
    suspend fun getByRoute(routeId: String): List<VehiclePositionEntity>

    @Query("DELETE FROM vehicle_positions WHERE vehicleId = :vehicleId")
    suspend fun deleteByVehicle(vehicleId: String)

    @Query("DELETE FROM vehicle_positions WHERE updatedAt < :thresholdMs")
    suspend fun deleteStale(thresholdMs: Long)
}

// ── RiderDao ──────────────────────────────────────────────────────────────────

@Dao
interface RiderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rider: RiderEntity)

    @Delete
    suspend fun delete(rider: RiderEntity)

    @Query("SELECT * FROM riders WHERE stopId = :stopId")
    fun observeAtStop(stopId: String): Flow<List<RiderEntity>>

    @Query("SELECT * FROM riders WHERE stopId = :stopId")
    suspend fun getAtStop(stopId: String): List<RiderEntity>

    @Query("DELETE FROM riders WHERE registeredAt < :cutoff")
    suspend fun deleteExpired(cutoff: Long)
}

// ── ArrivalDao ────────────────────────────────────────────────────────────────

@Dao
interface ArrivalDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(arrival: ArrivalEntity)

    @Query("SELECT * FROM arrivals WHERE stopId = :stopId ORDER BY arrivedAt DESC LIMIT 50")
    fun observeAtStop(stopId: String): Flow<List<ArrivalEntity>>

    @Query("SELECT * FROM arrivals ORDER BY arrivedAt DESC LIMIT 100")
    fun observeRecent(): Flow<List<ArrivalEntity>>

    @Query("SELECT COUNT(*) FROM arrivals")
    fun observeTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM arrivals")
    suspend fun getTotalCount(): Int

    @Query("DELETE FROM arrivals WHERE arrivedAt < :cutoff")
    suspend fun deleteOlderThan(cutoff: Long)
}
