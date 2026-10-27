package com.firas.carcareai.data.repository

import com.firas.carcareai.core.model.Vehicle
import com.firas.carcareai.data.local.dao.VehicleDao
import com.firas.carcareai.data.mapper.toDomain
import com.firas.carcareai.data.mapper.toEntity
import com.firas.carcareai.domain.repository.VehicleRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class VehicleRepositoryImpl(
    private val vehicleDao: VehicleDao
) : VehicleRepository {
    override fun getVehicles(): Flow<List<Vehicle>> {
        return vehicleDao.getVehicles().map { entities ->
            entities.map { entity ->
                entity.toDomain()
            }
        }
    }

    override suspend fun getVehicleById(id: Long?): Vehicle? {
        return vehicleDao.getVehicleById(id)?.toDomain()
    }

    override suspend fun addVehicle(vehicle: Vehicle) {
        vehicleDao.insertVehicle(vehicle.toEntity())
    }

    override suspend fun updateVehicle(vehicle: Vehicle) {
        vehicleDao.updateVehicle(vehicle.toEntity())
    }

    override suspend fun deleteVehicle(vehicle: Vehicle) {
        vehicleDao.deleteVehicle(vehicle.toEntity())
    }
}