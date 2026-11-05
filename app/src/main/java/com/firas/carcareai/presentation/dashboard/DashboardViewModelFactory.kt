package com.firas.carcareai.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.firas.carcareai.domain.usecase.maintenance.GetMaintenanceStatisticsUseCase
import com.firas.carcareai.domain.usecase.maintenance.GetMaintenancesByVehicleUseCase

/**
 * Factory responsable de la création du DashboardViewModel.
 *
 * Les dépendances sont fournies par AppContainer.
 */
class DashboardViewModelFactory(
    private val getMaintenancesByVehicleUseCase: GetMaintenancesByVehicleUseCase,
    private val getMaintenanceStatisticsUseCase: GetMaintenanceStatisticsUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            return DashboardViewModel(
                getMaintenancesByVehicleUseCase = getMaintenancesByVehicleUseCase,
                getMaintenanceStatisticsUseCase = getMaintenanceStatisticsUseCase
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}