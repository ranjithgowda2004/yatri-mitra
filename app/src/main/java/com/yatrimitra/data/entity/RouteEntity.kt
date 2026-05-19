package com.yatrimitra.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "routes")
data class RouteEntity(
    @PrimaryKey val routeId: String,
    val name: String,
    val description: String = "",
    val lengthKm: Float,
    val colorHex: String = "#FF6B00",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
