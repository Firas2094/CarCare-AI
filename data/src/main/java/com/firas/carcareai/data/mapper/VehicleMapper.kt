package com.firas.carcareai.data.mapper

import com.firas.carcareai.core.model.Vehicle
import com.firas.carcareai.data.local.entity.VehicleEntity

fun VehicleEntity.toDomain(): Vehicle {
    return Vehicle(
        id = id,
        brand = brand,
        model = model,
        year = year,
        mileage = mileage,
        fuelType = fuelType,
        licensePlate = licensePlate
    )
}

fun Vehicle.toEntity(): VehicleEntity {
    return VehicleEntity(
        id = id,
        brand = brand,
        model = model,
        year = year,
        mileage = mileage,
        fuelType = fuelType,
        licensePlate = licensePlate
    )
}