package com.firas.carcareai.data.repository

import com.firas.carcareai.core.model.Maintenance
import com.firas.carcareai.data.local.dao.MaintenanceDao
import com.firas.carcareai.data.mapper.toDomain
import com.firas.carcareai.data.mapper.toEntity
import com.firas.carcareai.domain.repository.MaintenanceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MaintenanceRepositoryImpl (
    private val maintenanceDao: MaintenanceDao
): MaintenanceRepository {

    override fun getMaintenancesByVehicle(vehicleId: Long): Flow<List<Maintenance>> {
        return maintenanceDao
            .getMaintenancesByVehicle(vehicleId)
            .map { entities ->
                entities.map { entity ->
                    entity.toDomain()
                }
            }
    }

    override suspend fun getMaintenanceById(id: Long): Maintenance? {
        return maintenanceDao.getMaintenanceById(id)?.toDomain()
    }

    override suspend fun addMaintenance(maintenance: Maintenance) {
        maintenanceDao.insertMaintenance(maintenance.toEntity())
    }

    override suspend fun updateMaintenance(maintenance: Maintenance) {
        maintenanceDao.updateMaintenance(maintenance.toEntity())
    }

    override suspend fun deleteMaintenance(maintenance: Maintenance) {
        maintenanceDao.deleteMaintenance(maintenance.toEntity())
    }
}