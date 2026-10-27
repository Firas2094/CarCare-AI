package com.firas.carcareai.domain.repository

import com.firas.carcareai.core.model.Maintenance
import kotlinx.coroutines.flow.Flow

interface MaintenanceRepository {

    fun getMaintenancesByVehicle(
        vehicleId: Long
    ): Flow<List<Maintenance>>

    suspend fun getMaintenanceById(
        id: Long
    ): Maintenance?

    suspend fun addMaintenance(
        maintenance: Maintenance
    )

    suspend fun updateMaintenance(
        maintenance: Maintenance
    )

    suspend fun deleteMaintenance(
        maintenance: Maintenance
    )
}