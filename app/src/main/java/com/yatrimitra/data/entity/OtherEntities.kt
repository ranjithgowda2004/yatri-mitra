package com.yatrimitra.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// ── VehicleEntity ─────────────────────────────────────────────────────────────

@Entity(
    tableName = "vehicles",
    foreignKeys = [ForeignKey(
        entity = RouteEntity::class,
        parentColumns = ["routeId"],
        childColumns = ["routeId"],
        onDelete = ForeignKey.SET_NULL
    )],
    indices = [Index("routeId")]
)
data class VehicleEntity(
    @PrimaryKey val vehicleId: String,
    val routeId: String? = null,
    val driverName: String = "Unknown",
    val capacity: Int = 8,
    val registeredAt: Long = System.currentTimeMillis()
)

// ── VehiclePositionEntity ─────────────────────────────────────────────────────

@Entity(
    tableName = "vehicle_positions",
    foreignKeys = [ForeignKey(
        entity = VehicleEntity::class,
        parentColumns = ["vehicleId"],
        childColumns = ["vehicleId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("vehicleId")]
)
data class VehiclePositionEntity(
    @PrimaryKey val vehicleId: String,
    val routeId: String,
    val position: Float,
    val speedKmh: Float,
    val updatedAt: Long = System.currentTimeMillis()
)

// ── RiderEntity ───────────────────────────────────────────────────────────────

@Entity(
    tableName = "riders",
    foreignKeys = [ForeignKey(
        entity = StopEntity::class,
        parentColumns = ["stopId"],
        childColumns = ["stopId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("stopId")]
)
data class RiderEntity(
    @PrimaryKey val riderId: String,
    val name: String,
    val stopId: String,
    val stopName: String,
    val registeredAt: Long = System.currentTimeMillis()
)

// ── ArrivalEntity ─────────────────────────────────────────────────────────────

@Entity(tableName = "arrivals")
data class ArrivalEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val vehicleId: String,
    val stopId: String,
    val stopName: String,
    val arrivedAt: Long = System.currentTimeMillis()
)
