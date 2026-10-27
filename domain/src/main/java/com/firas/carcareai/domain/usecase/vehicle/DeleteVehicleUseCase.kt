package com.firas.carcareai.domain.usecase.vehicle

import com.firas.carcareai.core.model.Vehicle
import com.firas.carcareai.domain.repository.VehicleRepository

class DeleteVehicleUseCase (
    private val repository: VehicleRepository
){

    suspend operator fun invoke(vehicle: Vehicle) {
        repository.deleteVehicle(vehicle)
    }
}