package com.firas.carcareai.presentation.maintenanceinterval

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firas.carcareai.core.model.MaintenanceInterval
import com.firas.carcareai.core.model.MaintenanceType
import com.firas.carcareai.domain.usecase.maintenance.DeleteMaintenanceIntervalUseCase
import com.firas.carcareai.domain.usecase.maintenance.GetMaintenanceIntervalsUseCase
import com.firas.carcareai.domain.usecase.maintenance.SaveMaintenanceIntervalUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsable de la gestion
 * des intervalles d'entretien personnalisés.
 */
class MaintenanceIntervalViewModel(
    private val getIntervalsUseCase: GetMaintenanceIntervalsUseCase,
    private val saveIntervalUseCase: SaveMaintenanceIntervalUseCase,
    private val deleteIntervalUseCase: DeleteMaintenanceIntervalUseCase
) : ViewModel() {

    private val _intervals = MutableStateFlow<List<MaintenanceInterval>>(emptyList())

    val intervals: StateFlow<List<MaintenanceInterval>> = _intervals

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private var observationJob: Job? = null

    /**
     * Observe les intervalles personnalisés
     * du véhicule sélectionné.
     */
    fun loadIntervals(vehicleId: Long) {

        // Évite de conserver plusieurs collecteurs actifs.
        observationJob?.cancel()

        _intervals.value = emptyList()
        _error.value = null

        observationJob = viewModelScope.launch {
            try {
                getIntervalsUseCase(vehicleId).collect { result ->
                    _intervals.value = result
                }
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) throw e
                _error.value = e.message ?: "Erreur de chargement"
            }
        }
    }

    /**
     * Enregistre ou modifie un intervalle.
     */
    fun saveInterval(
        vehicleId: Long,
        type: MaintenanceType,
        intervalKm: Int?,
        intervalMonths: Int?
    ) {
        viewModelScope.launch {
            try {
                val interval = MaintenanceInterval(
                    vehicleId = vehicleId,
                    type = type,
                    intervalKm = intervalKm,
                    intervalMonths = intervalMonths
                )

                saveIntervalUseCase(interval)
                _error.value = null

            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) throw e
                _error.value =
                    e.message ?: "Impossible d'enregistrer l'intervalle"
            }
        }
    }

    /**
     * Supprime une configuration personnalisée.
     *
     * Le calcul des échéances pourra alors
     * utiliser les valeurs par défaut.
     */
    fun resetInterval(
        vehicleId: Long,
        type: MaintenanceType
    ) {
        viewModelScope.launch {
            try {
                deleteIntervalUseCase(vehicleId, type)
                _error.value = null

            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) throw e
                _error.value =
                    e.message ?: "Impossible de réinitialiser l'intervalle"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}