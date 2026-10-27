package com.firas.carcareai.domain.usecase.maintenance

import com.firas.carcareai.core.model.Maintenance
import com.firas.carcareai.domain.repository.MaintenanceRepository
import kotlinx.coroutines.flow.Flow

class GetMaintenancesByVehicleUseCase(
    private val repository: MaintenanceRepository
) {
    operator fun invoke(
        vehicleId: Long
    ): Flow<List<Maintenance>> {
        return repository.getMaintenancesByVehicle(vehicleId)
    }
}