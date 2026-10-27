package com.firas.carcareai.core.model

data class MaintenanceSchedule(
    val type: MaintenanceType,

    val lastMaintenanceMileage: Int? = null,
    val nextMaintenanceMileage: Int? = null,
    val remainingKm: Int? = null,

    val lastMaintenanceDate: Long? = null,
    val nextMaintenanceDate: Long? = null,
    val remainingDays: Long? = null,

    val status: MaintenanceStatus
)

enum class MaintenanceStatus {
    OK,
    SOON,
    OVERDUE,
    UNKNOWN
}