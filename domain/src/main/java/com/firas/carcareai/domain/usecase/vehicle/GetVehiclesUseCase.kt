package com.firas.carcareai.domain.usecase.vehicle

import com.firas.carcareai.core.model.Vehicle
import com.firas.carcareai.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow

class GetVehiclesUseCase(
    private val repository: VehicleRepository
) {
    operator fun invoke(): Flow<List<Vehicle>> {
        return repository.getVehicles()
    }

}