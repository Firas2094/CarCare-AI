package com.firas.carcareai.core.model

/**
 * Représente un rappel d'entretien associé à un véhicule.
 *
 * Regroupe les informations du véhicule et son échéance
 * de maintenance calculée.
 */
data class MaintenanceReminder(
    val vehicle: Vehicle,
    val schedule: MaintenanceSchedule
)
