package com.firas.carcareai.domain.usecase.vehicle

import com.firas.carcareai.core.model.Vehicle
import com.firas.carcareai.domain.repository.VehicleRepository

class AddVehicleUseCase(
    private val repository: VehicleRepository
) {
    suspend operator fun invoke(vehicle: Vehicle) {
        require(vehicle.brand.isNotBlank()) {
            "Vehicle brand cannot be empty"
        }

        require(vehicle.model.isNotBlank()) {
            "Vehicle model cannot be empty"
        }

        require(vehicle.year > 1900) {
            "Invalid vehicle year"
        }

        require(vehicle.mileage >= 0) {
            "Mileage cannot be negative"
        }

        repository.addVehicle(vehicle)
    }
}