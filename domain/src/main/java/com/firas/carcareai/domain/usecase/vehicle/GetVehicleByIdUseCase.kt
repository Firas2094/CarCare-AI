package com.firas.carcareai.domain.usecase.vehicle

import com.firas.carcareai.core.model.Vehicle
import com.firas.carcareai.domain.repository.VehicleRepository

class GetVehicleByIdUseCase(
    private val repository: VehicleRepository
) {

    suspend operator fun invoke(id: Long?): Vehicle? {
        return repository.getVehicleById(id)
    }
}