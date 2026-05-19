package com.yatrimitra.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.yatrimitra.data.dao.*
import com.yatrimitra.data.entity.*

@Database(
    entities = [
        RouteEntity::class,
        StopEntity::class,
        VehicleEntity::class,
        VehiclePositionEntity::class,
        RiderEntity::class,
        ArrivalEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun routeDao(): RouteDao
    abstract fun stopDao(): StopDao
    abstract fun vehicleDao(): VehicleDao
    abstract fun vehiclePositionDao(): VehiclePositionDao
    abstract fun riderDao(): RiderDao
    abstract fun arrivalDao(): ArrivalDao

    companion object {
        private const val DB_NAME = "yatri_mitra.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
