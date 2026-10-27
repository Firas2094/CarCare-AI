package com.firas.carcareai.data.mapper

import android.R.attr.type
import com.firas.carcareai.core.model.Maintenance
import com.firas.carcareai.core.model.MaintenanceType
import com.firas.carcareai.data.local.entity.MaintenanceEntity

fun MaintenanceEntity.toDomain(): Maintenance {
    return Maintenance(
        id = id,
        vehicleId = vehicleId,
        type = MaintenanceType.valueOf(type),
        date = date,
        mileage = mileage,
        cost = cost,
        notes = notes
    )
}

fun Maintenance.toEntity(): MaintenanceEntity {
    return MaintenanceEntity(
        id = id,
        vehicleId = vehicleId,
        type = type.name,
        date = date,
        mileage = mileage,
        cost = cost,
        notes = notes
    )
}