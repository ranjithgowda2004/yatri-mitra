package com.yatrimitra

import com.yatrimitra.data.db.AppDatabase
import com.yatrimitra.data.entity.RouteEntity
import com.yatrimitra.data.entity.StopEntity

object DatabaseSeeder {

    suspend fun seedIfEmpty(db: AppDatabase) {
        if (db.routeDao().getCount() > 0) return

        val routes = listOf(
            RouteEntity("R1", "Vijayanagar → Majestic", "Main city corridor", 12.0f, "#FF6B00"),
            RouteEntity("R2", "Village Center → Bus Stand", "Rural feeder route", 8.0f, "#00897B"),
            RouteEntity("R3", "Hebbal → KR Puram", "North-East corridor", 15.0f, "#7C4DFF")
        )
        db.routeDao().insertAll(routes)

        val stops = listOf(
            StopEntity("S1", "R1", "Vijayanagar",  0.00f, 12.9716, 77.5946),
            StopEntity("S2", "R1", "Chord Road",    0.18f, 13.0021, 77.5545),
            StopEntity("S3", "R1", "Rajajinagar",   0.35f, 13.0039, 77.5555),
            StopEntity("S4", "R1", "Yeshwanthpur",  0.52f, 13.0248, 77.5500),
            StopEntity("S5", "R1", "Malleswaram",   0.70f, 13.0068, 77.5700),
            StopEntity("S6", "R1", "Sampige Road",  0.85f, 12.9892, 77.5700),
            StopEntity("S7", "R1", "Majestic",      1.00f, 12.9776, 77.5713),

            StopEntity("T1", "R2", "Village Center",0.00f, 13.1500, 77.6000),
            StopEntity("T2", "R2", "Hanuman Temple",0.20f, 13.1550, 77.6100),
            StopEntity("T3", "R2", "School Gate",   0.40f, 13.1600, 77.6200),
            StopEntity("T4", "R2", "Market Square", 0.60f, 13.1650, 77.6300),
            StopEntity("T5", "R2", "Railway Cross", 0.80f, 13.1700, 77.6400),
            StopEntity("T6", "R2", "Bus Stand",     1.00f, 13.1750, 77.6500),

            StopEntity("H1", "R3", "Hebbal",        0.00f, 13.0450, 77.5960),
            StopEntity("H2", "R3", "Nagavara",      0.17f, 13.0470, 77.6100),
            StopEntity("H3", "R3", "Kalyan Nagar",  0.34f, 13.0440, 77.6390),
            StopEntity("H4", "R3", "HBR Layout",    0.50f, 13.0360, 77.6540),
            StopEntity("H5", "R3", "Banaswadi",     0.67f, 13.0220, 77.6600),
            StopEntity("H6", "R3", "Hoodi",         0.84f, 13.0130, 77.6960),
            StopEntity("H7", "R3", "KR Puram",      1.00f, 13.0000, 77.6960)
        )
        db.stopDao().insertAll(stops)
    }
}
