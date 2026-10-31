package com.firas.carcareai.domain.repository

import com.firas.carcareai.core.model.MaintenanceInterval
import com.firas.carcareai.core.model.MaintenanceType
import kotlinx.coroutines.flow.Flow

/**
 * Contrat permettant de gérer les intervalles
 * d'entretien personnalisés d'un véhicule.
 *
 * Le domaine ne connaît pas Room.
 * Il manipule uniquement les modèles métier.
 */
interface MaintenanceIntervalRepository {

    /**
     * Observe les intervalles personnalisés d'un véhicule.
     *
     * Chaque modification enregistrée en base
     * provoque une nouvelle émission du Flow.
     */
    fun observeByVehicle(vehicleId: Long): Flow<List<MaintenanceInterval>>

    /**
     * Enregistre un nouvel intervalle ou
     * remplace une configuration existante.
     */
    suspend fun upsert(interval: MaintenanceInterval)

    /**
     * Supprime un intervalle personnalisé.
     *
     * Le calcul des échéances pourra ensuite
     * utiliser les valeurs par défaut.
     */
    suspend fun delete(vehicleId: Long, type: MaintenanceType)
}