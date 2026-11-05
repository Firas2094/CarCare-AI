package com.firas.carcareai.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firas.carcareai.core.model.MaintenanceStatistics
import com.firas.carcareai.domain.usecase.maintenance.GetMaintenanceStatisticsUseCase
import com.firas.carcareai.domain.usecase.maintenance.GetMaintenancesByVehicleUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel du tableau de bord des dépenses.
 *
 * Observe les entretiens d'un véhicule et recalcule
 * automatiquement les statistiques à chaque changement.
 */
class DashboardViewModel(
    private val getMaintenancesByVehicleUseCase: GetMaintenancesByVehicleUseCase,
    private val getMaintenanceStatisticsUseCase: GetMaintenanceStatisticsUseCase
) : ViewModel() {

    private val _statistics = MutableStateFlow<MaintenanceStatistics?>(null)
    val statistics: StateFlow<MaintenanceStatistics?> = _statistics

    // Évite de conserver plusieurs collecteurs actifs.
    private var statisticsJob: Job? = null


    /**
     * Observe les entretiens du véhicule sélectionné.
     */
    fun loadStatistics(vehicleId: Long) {

        // Annule l'observation précédente.
        statisticsJob?.cancel()

        // Réinitialise les données lors d'un changement de véhicule.
        _statistics.value = null

        statisticsJob = viewModelScope.launch {

            getMaintenancesByVehicleUseCase(vehicleId)
                .collect { maintenances ->

                    // Recalcule les statistiques à chaque émission du Flow.
                    _statistics.value = getMaintenanceStatisticsUseCase(maintenances)
                }
        }
    }
}