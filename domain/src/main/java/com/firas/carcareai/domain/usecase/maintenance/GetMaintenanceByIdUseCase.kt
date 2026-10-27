package com.firas.carcareai.domain.usecase.maintenance

import com.firas.carcareai.core.model.Maintenance
import com.firas.carcareai.domain.repository.MaintenanceRepository

class GetMaintenanceByIdUseCase(
    private val repository: MaintenanceRepository
) {
    suspend operator fun invoke(id: Long): Maintenance? {
        return repository.getMaintenanceById(id)
    }
}