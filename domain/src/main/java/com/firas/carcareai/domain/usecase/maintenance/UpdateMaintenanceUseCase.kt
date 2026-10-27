package com.firas.carcareai.domain.usecase.maintenance

import com.firas.carcareai.core.model.Maintenance
import com.firas.carcareai.domain.repository.MaintenanceRepository

class UpdateMaintenanceUseCase(
    private val repository: MaintenanceRepository
) {
    suspend operator fun invoke(maintenance: Maintenance) {
        repository.updateMaintenance(maintenance)
    }
}