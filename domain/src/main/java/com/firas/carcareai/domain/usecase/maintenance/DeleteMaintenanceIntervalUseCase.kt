package com.firas.carcareai.domain.usecase.maintenance

import com.firas.carcareai.core.model.MaintenanceType
import com.firas.carcareai.domain.repository.MaintenanceIntervalRepository

/**
 * Supprime l'intervalle personnalisé
 * d'un type d'entretien pour un véhicule.
 */
class DeleteMaintenanceIntervalUseCase(
    private val repository: MaintenanceIntervalRepository
) {
    suspend operator fun invoke(
        vehicleId: Long,
        type: MaintenanceType
    ) {
        repository.delete(vehicleId, type)
    }
}