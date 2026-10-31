package com.firas.carcareai.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Représente la configuration personnalisée
 * d'un intervalle d'entretien pour un véhicule.
 *
 * Un véhicule ne peut avoir qu'une seule configuration
 * par type d'entretien.
 */
@Entity(
    tableName = "maintenance_intervals",
    primaryKeys = ["vehicleId", "type"],
    foreignKeys = [
        ForeignKey(
            entity = VehicleEntity::class,
            parentColumns = ["id"],
            childColumns = ["vehicleId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["vehicleId"])
    ]
)

data class MaintenanceIntervalEntity(
    val vehicleId: Long,
    val type: String,
    val intervalKm: Int?,
    val intervalMonths: Int?
)