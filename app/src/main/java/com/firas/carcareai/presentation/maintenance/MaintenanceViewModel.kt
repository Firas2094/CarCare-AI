
package com.firas.carcareai.presentation.maintenance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firas.carcareai.core.model.Maintenance
import com.firas.carcareai.core.model.MaintenanceSchedule
import com.firas.carcareai.domain.usecase.maintenance.AddMaintenanceUseCase
import com.firas.carcareai.domain.usecase.maintenance.CalculateMaintenanceScheduleUseCase
import com.firas.carcareai.domain.usecase.maintenance.DeleteMaintenanceUseCase
import com.firas.carcareai.domain.usecase.maintenance.GetMaintenanceByIdUseCase
import com.firas.carcareai.domain.usecase.maintenance.GetMaintenancesByVehicleUseCase
import com.firas.carcareai.domain.usecase.maintenance.GetMaintenanceIntervalsUseCase
import com.firas.carcareai.domain.usecase.maintenance.UpdateMaintenanceUseCase
import com.firas.carcareai.domain.usecase.vehicle.GetVehicleByIdUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class MaintenanceViewModel(
    private val getMaintenancesByVehicleUseCase: GetMaintenancesByVehicleUseCase,
    private val getMaintenanceByIdUseCase: GetMaintenanceByIdUseCase,
    private val addMaintenanceUseCase: AddMaintenanceUseCase,
    private val updateMaintenanceUseCase: UpdateMaintenanceUseCase,
    private val deleteMaintenanceUseCase: DeleteMaintenanceUseCase,
    private val calculateMaintenanceScheduleUseCase: CalculateMaintenanceScheduleUseCase,
    private val getVehicleByIdUseCase: GetVehicleByIdUseCase,
    private val getMaintenanceIntervalsUseCase: GetMaintenanceIntervalsUseCase
) : ViewModel()  {

    private val _maintenances =
        MutableStateFlow<List<Maintenance>>(emptyList())

    val maintenances: StateFlow<List<Maintenance>> = _maintenances

    private val _selectedMaintenance =
        MutableStateFlow<Maintenance?>(null)

    val selectedMaintenance: StateFlow<Maintenance?> =
        _selectedMaintenance

    private val _maintenanceSchedules =
        MutableStateFlow<List<MaintenanceSchedule>>(emptyList())

    val maintenanceSchedules: StateFlow<List<MaintenanceSchedule>> =
        _maintenanceSchedules

    // Conserve le Job actif pour éviter plusieurs collecteurs
    // lorsque l'utilisateur revient sur l'écran des entretiens.
    private var maintenancesJob: Job? = null

    /**
     * Charge les entretiens et les intervalles personnalisés
     * d'un véhicule, puis recalcule les échéances.
     */
    fun loadMaintenances(vehicleId: Long) {

        // Annule l'observation précédente.
        maintenancesJob?.cancel()

        _maintenances.value = emptyList()
        _maintenanceSchedules.value = emptyList()

        maintenancesJob = viewModelScope.launch {

            val vehicle = getVehicleByIdUseCase(vehicleId) ?: return@launch

            // Flow des entretiens enregistrés.
            val maintenancesFlow = getMaintenancesByVehicleUseCase(vehicleId)

            // Flow des intervalles personnalisés enregistrés.
            val intervalsFlow = getMaintenanceIntervalsUseCase(vehicleId)

            // Recalcule dès que l'une des deux sources change.
            combine(
                maintenancesFlow,
                intervalsFlow
            ) { maintenanceList, customIntervals ->

                val schedules =
                    calculateMaintenanceScheduleUseCase(
                        vehicle = vehicle,
                        maintenances = maintenanceList,
                        customIntervals = customIntervals
                    )

                maintenanceList to schedules

            }.collect { (maintenanceList, schedules) ->

                _maintenances.value = maintenanceList
                _maintenanceSchedules.value = schedules
            }
        }
    }

    /**
     * Charge un entretien par son identifiant.
     */
    fun loadMaintenance(id: Long) {
        viewModelScope.launch {
            _selectedMaintenance.value =
                getMaintenanceByIdUseCase(id)
        }
    }

    /**
     * Ajoute un entretien.
     */
    fun addMaintenance(maintenance: Maintenance) {
        viewModelScope.launch {
            addMaintenanceUseCase(maintenance)
        }
    }

    /**
     * Modifie un entretien existant.
     */
    fun updateMaintenance(maintenance: Maintenance) {
        viewModelScope.launch {
            updateMaintenanceUseCase(maintenance)
        }
    }

    /**
     * Supprime un entretien.
     */
    fun deleteMaintenance(maintenance: Maintenance) {
        viewModelScope.launch {
            deleteMaintenanceUseCase(maintenance)
        }
    }
}