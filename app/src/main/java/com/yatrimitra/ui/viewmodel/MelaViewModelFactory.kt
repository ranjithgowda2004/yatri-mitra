package com.yatrimitra.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yatrimitra.data.db.AppDatabase
import com.yatrimitra.data.repository.*

class MelaViewModelFactory(private val db: AppDatabase) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MelaViewModel::class.java)) {
            return MelaViewModel(
                routeRepo   = RouteRepository(db.routeDao()),
                stopRepo    = StopRepository(db.stopDao()),
                vehicleRepo = VehicleRepository(db.vehicleDao(), db.vehiclePositionDao()),
                riderRepo   = RiderRepository(db.riderDao()),
                arrivalRepo = ArrivalRepository(db.arrivalDao())
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}
