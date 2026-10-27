package com.firas.carcareai.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.firas.carcareai.data.local.entity.MaintenanceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MaintenanceDao {

    @Query(
        "SELECT * FROM maintenances " +
                "WHERE vehicleId = :vehicleId " +
                "ORDER BY date DESC"
    )

    fun getMaintenancesByVehicle(
        vehicleId: Long
    ): Flow<List<MaintenanceEntity>>


    @Query("SELECT * FROM maintenances " +
            "WHERE id = :id LIMIT 1")
    suspend fun getMaintenanceById(
        id: Long
    ): MaintenanceEntity?

    @Insert
    suspend fun insertMaintenance(
        maintenance: MaintenanceEntity
    ): Long

    @Update
    suspend fun updateMaintenance(
        maintenance: MaintenanceEntity
    )

    @Delete
    suspend fun deleteMaintenance(
        maintenance: MaintenanceEntity
    )
}