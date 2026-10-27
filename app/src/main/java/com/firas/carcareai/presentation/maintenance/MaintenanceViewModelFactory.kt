package com.firas.carcareai.presentation.maintenance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.firas.carcareai.domain.usecase.maintenance.AddMaintenanceUseCase
import com.firas.carcareai.domain.usecase.maintenance.CalculateMaintenanceScheduleUseCase
import com.firas.carcareai.domain.usecase.maintenance.DeleteMaintenanceUseCase
import com.firas.carcareai.domain.usecase.maintenance.GetMaintenanceByIdUseCase
import com.firas.carcareai.domain.usecase.maintenance.GetMaintenancesByVehicleUseCase
import com.firas.carcareai.domain.usecase.maintenance.UpdateMaintenanceUseCase
import com.firas.carcareai.domain.usecase.vehicle.GetVehicleByIdUseCase

class MaintenanceViewModelFactory(
    private val getMaintenancesByVehicleUseCase: GetMaintenancesByVehicleUseCase,
    private val getMaintenanceByIdUseCase: GetMaintenanceByIdUseCase,
    private val addMaintenanceUseCase: AddMaintenanceUseCase,
    private val updateMaintenanceUseCase: UpdateMaintenanceUseCase,
    private val deleteMaintenanceUseCase: DeleteMaintenanceUseCase,
    private val calculateMaintenanceScheduleUseCase: CalculateMaintenanceScheduleUseCase,
    private val getVehicleByIdUseCase: GetVehicleByIdUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (modelClass.isAssignableFrom(MaintenanceViewModel::class.java)) {
            return MaintenanceViewModel(
                getMaintenancesByVehicleUseCase,
                getMaintenanceByIdUseCase,
                addMaintenanceUseCase,
                updateMaintenanceUseCase,
                deleteMaintenanceUseCase,
                calculateMaintenanceScheduleUseCase,
                getVehicleByIdUseCase
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}