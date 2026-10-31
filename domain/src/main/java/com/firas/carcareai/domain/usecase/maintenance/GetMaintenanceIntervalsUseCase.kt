package com.firas.carcareai.domain.usecase.maintenance

import com.firas.carcareai.core.model.MaintenanceInterval
import com.firas.carcareai.domain.repository.MaintenanceIntervalRepository
import kotlinx.coroutines.flow.Flow

/**
 * Observe les intervalles d'entretien personnalisés
 * d'un véhicule.
 */
class GetMaintenanceIntervalsUseCase(
    private val repository: MaintenanceIntervalRepository
) {
    operator fun invoke(
        vehicleId: Long
    ): Flow<List<MaintenanceInterval>> {
        return repository.observeByVehicle(vehicleId)
    }
}