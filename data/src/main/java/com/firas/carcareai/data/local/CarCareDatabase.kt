package com.firas.carcareai.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.firas.carcareai.data.local.dao.MaintenanceDao
import com.firas.carcareai.data.local.dao.VehicleDao
import com.firas.carcareai.data.local.entity.MaintenanceEntity
import com.firas.carcareai.data.local.entity.VehicleEntity

@Database(
    entities = [VehicleEntity::class, MaintenanceEntity::class],
    version = 2,
    exportSchema = false
)
abstract class CarCareDatabase : RoomDatabase() {

    abstract fun vehicleDao(): VehicleDao
    abstract fun maintenanceDao(): MaintenanceDao
}