package com.firas.carcareai.data.repository

import com.firas.carcareai.core.model.MaintenanceInterval
import com.firas.carcareai.core.model.MaintenanceType
import com.firas.carcareai.data.local.dao.MaintenanceIntervalDao
import com.firas.carcareai.data.local.entity.MaintenanceIntervalEntity
import com.firas.carcareai.domain.repository.MaintenanceIntervalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implémentation du repository des intervalles personnalisés.
 *
 * Responsabilités :
 * - Communiquer avec Room via le DAO.
 * - Convertir les entités Room en modèles métier.
 * - Convertir les modèles métier en entités Room.
 */
class MaintenanceIntervalRepositoryImpl(
    private val dao: MaintenanceIntervalDao
) : MaintenanceIntervalRepository {

    /**
     * Récupère les intervalles personnalisés d'un véhicule.
     *
     * Les entités Room sont converties en modèles métier
     * avant d'être transmises à la couche domain.
     */
    override fun observeByVehicle(
        vehicleId: Long
    ): Flow<List<MaintenanceInterval>> {

        return dao.observeByVehicle(vehicleId).map { entities ->

            entities.map { entity ->
                entity.toDomain()
            }
        }
    }

    /**
     * Ajoute ou modifie un intervalle personnalisé.
     *
     * Le modèle métier est converti en entité Room
     * avant son enregistrement.
     */
    override suspend fun upsert(
        interval: MaintenanceInterval
    ) {
        dao.upsert(interval.toEntity())
    }

    /**
     * Supprime la configuration personnalisée
     * d'un type d'entretien pour un véhicule.
     */
    override suspend fun delete(
        vehicleId: Long,
        type: MaintenanceType
    ) {
        dao.delete(
            vehicleId = vehicleId,
            type = type.name
        )
    }

    /**
     * Convertit une entité Room en modèle métier.
     */
    private fun MaintenanceIntervalEntity.toDomain(): MaintenanceInterval {
        return MaintenanceInterval(
            vehicleId = vehicleId,
            type = MaintenanceType.valueOf(type),
            intervalKm = intervalKm,
            intervalMonths = intervalMonths
        )
    }

    /**
     * Convertit un modèle métier en entité Room.
     */
    private fun MaintenanceInterval.toEntity(): MaintenanceIntervalEntity {
        return MaintenanceIntervalEntity(
            vehicleId = vehicleId,
            type = type.name,
            intervalKm = intervalKm,
            intervalMonths = intervalMonths
        )
    }
}