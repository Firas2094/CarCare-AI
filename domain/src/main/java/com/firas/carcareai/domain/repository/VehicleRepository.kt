package com.firas.carcareai.domain.repository

import com.firas.carcareai.core.model.Vehicle
import kotlinx.coroutines.flow.Flow

interface VehicleRepository {

    fun getVehicles(): Flow<List<Vehicle>>

    suspend fun getVehicleById(id: Long?): Vehicle?

    suspend fun addVehicle(vehicle: Vehicle)

    suspend fun updateVehicle(vehicle: Vehicle)

    suspend fun deleteVehicle(vehicle: Vehicle)

}