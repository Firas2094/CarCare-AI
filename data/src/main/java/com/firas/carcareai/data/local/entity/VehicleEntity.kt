package com.firas.carcareai.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.firas.carcareai.core.model.FuelType


@Entity(tableName = "vehicles")
data class VehicleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val brand: String,
    val model: String,
    val year: Int,
    val mileage: Int,
    val fuelType: FuelType,
    val licensePlate: String?
)