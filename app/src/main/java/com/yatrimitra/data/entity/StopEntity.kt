package com.yatrimitra.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "stops",
    foreignKeys = [ForeignKey(
        entity = RouteEntity::class,
        parentColumns = ["routeId"],
        childColumns = ["routeId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index("routeId")]
)
data class StopEntity(
    @PrimaryKey val stopId: String,
    val routeId: String,
    val name: String,
    val position: Float,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)
