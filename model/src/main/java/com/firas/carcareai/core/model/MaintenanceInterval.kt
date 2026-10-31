package com.firas.carcareai.core.model

data class MaintenanceInterval(
    val vehicleId: Long,
    val type: MaintenanceType,
    val intervalKm: Int? = null,
    val intervalMonths: Int? = null
){
    init {
        require(intervalKm == null || intervalKm > 0) {
            "Mileage interval must be positive"
        }

        require(intervalMonths == null || intervalMonths > 0) {
            "Month interval must be positive"
        }

        require(intervalKm != null || intervalMonths != null) {
            "At least one maintenance interval must be defined"
        }
    }
}