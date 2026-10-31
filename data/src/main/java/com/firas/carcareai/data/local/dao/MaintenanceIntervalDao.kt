package com.firas.carcareai.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.firas.carcareai.data.local.entity.MaintenanceIntervalEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO permettant de gérer les intervalles
 * d'entretien personnalisés dans Room.
 */
@Dao
interface MaintenanceIntervalDao {

    /**
     * Observe tous les intervalles personnalisés
     * associés à un véhicule.
     *
     * Flow permet de recevoir automatiquement
     * les modifications effectuées dans la base.
     */
    @Query(
        """
        SELECT * FROM maintenance_intervals
        WHERE vehicleId = :vehicleId
        """
    )
    fun observeByVehicle(
        vehicleId: Long
    ): Flow<List<MaintenanceIntervalEntity>>

    /**
     * Ajoute ou remplace un intervalle personnalisé.
     *
     * Si le véhicule possède déjà un intervalle
     * pour ce type d'entretien, il sera remplacé.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(
        interval: MaintenanceIntervalEntity
    )

    /**
     * Supprime une configuration personnalisée.
     *
     * Après suppression, le calcul des échéances
     * pourra revenir à l'intervalle par défaut.
     */
    @Query(
        """
        DELETE FROM maintenance_intervals
        WHERE vehicleId = :vehicleId
        AND type = :type
        """
    )
    suspend fun delete(
        vehicleId: Long,
        type: String
    )
}