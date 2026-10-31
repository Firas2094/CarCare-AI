package com.firas.carcareai.domain.usecase.maintenance

import com.firas.carcareai.core.model.MaintenanceInterval
import com.firas.carcareai.domain.repository.MaintenanceIntervalRepository

/**
 * Enregistre ou modifie un intervalle
 * d'entretien personnalisé.
 */
class SaveMaintenanceIntervalUseCase(
    private val repository: MaintenanceIntervalRepository
) {
    suspend operator fun invoke(
        interval: MaintenanceInterval
    ) {
        repository.upsert(interval)
    }
}