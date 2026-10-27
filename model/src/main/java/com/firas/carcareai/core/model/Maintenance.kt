package com.firas.carcareai.core.model

data class Maintenance(

    val id: Long = 0,
    val vehicleId: Long,
    val type: MaintenanceType,
    val date: Long,
    val mileage: Int,
    val cost: Double? = null,
    val notes: String? = null
)

enum class MaintenanceType {
    OIL_CHANGE,                 //Vidange
    BRAKES,                     //Freins
    TIRES,                      //Pneus
    BATTERY,                    //Batterie
    TIMING_BELT,                //Courroie de distribution
    AIR_FILTER,                 //Filtre à air
    TECHNICAL_INSPECTION,       //Contrôle technique
    OTHER                       //Autre
    ;

}

fun MaintenanceType.toFrenchLabel(): String {
    return when (this) {
        MaintenanceType.OIL_CHANGE -> "Vidange"
        MaintenanceType.BRAKES -> "Freins"
        MaintenanceType.TIRES -> "Pneus"
        MaintenanceType.BATTERY -> "Batterie"
        MaintenanceType.TIMING_BELT -> "Courroie de distribution"
        MaintenanceType.AIR_FILTER -> "Filtre à air"
        MaintenanceType.TECHNICAL_INSPECTION -> "Contrôle technique"
        MaintenanceType.OTHER -> "Autre"
    }
}