package com.firas.carcareai.presentation.vehicle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.firas.carcareai.domain.usecase.vehicle.AddVehicleUseCase
import com.firas.carcareai.domain.usecase.vehicle.DeleteVehicleUseCase
import com.firas.carcareai.domain.usecase.vehicle.GetVehicleByIdUseCase
import com.firas.carcareai.domain.usecase.vehicle.GetVehiclesUseCase
import com.firas.carcareai.domain.usecase.vehicle.UpdateVehicleUseCase

class VehicleViewModelFactory(
    private val getVehiclesUseCase: GetVehiclesUseCase,
    private val addVehicleUseCase: AddVehicleUseCase,
    private val getVehicleByIdUseCase: GetVehicleByIdUseCase,
    private val deleteVehicleUseCase: DeleteVehicleUseCase,
    private val updateVehicleUseCase: UpdateVehicleUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(VehicleViewModel::class.java)) {
            return VehicleViewModel(
                getVehiclesUseCase = getVehiclesUseCase,
                addVehicleUseCase = addVehicleUseCase,
                getVehicleByIdUseCase = getVehicleByIdUseCase,
                deleteVehicleUseCase = deleteVehicleUseCase,
                updateVehicleUseCase = updateVehicleUseCase
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}