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
import com.firas.carcareai.domain.usecase.maintenance.UpdateMaintenanceUseCase
import com.firas.carcareai.domain.usecase.vehicle.GetVehicleByIdUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MaintenanceViewModel(
    private val getMaintenancesByVehicleUseCase: GetMaintenancesByVehicleUseCase,
    private val getMaintenanceByIdUseCase: GetMaintenanceByIdUseCase,
    private val addMaintenanceUseCase: AddMaintenanceUseCase,
    private val updateMaintenanceUseCase: UpdateMaintenanceUseCase,
    private val deleteMaintenanceUseCase: DeleteMaintenanceUseCase,
    private val calculateMaintenanceScheduleUseCase: CalculateMaintenanceScheduleUseCase,
    private val getVehicleByIdUseCase: GetVehicleByIdUseCase
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



    fun loadMaintenances(vehicleId: Long) {
        viewModelScope.launch {
            val vehicle = getVehicleByIdUseCase(vehicleId)
            if (vehicle == null) {
                _maintenances.value = emptyList()
                _maintenanceSchedules.value = emptyList()
                return@launch
            }

            getMaintenancesByVehicleUseCase(vehicleId)
                .collect{ maintenanceList ->
                    _maintenances.value = maintenanceList

                    _maintenanceSchedules.value =
                        calculateMaintenanceScheduleUseCase(
                            vehicle = vehicle,
                            maintenances = maintenanceList
                        )
                }
        }
    }

    fun loadMaintenance(id: Long) {
        viewModelScope.launch {
            _selectedMaintenance.value =
                getMaintenanceByIdUseCase(id)
        }
    }


    fun addMaintenance(maintenance: Maintenance) {
        viewModelScope.launch {
            addMaintenanceUseCase(maintenance)
        }
    }

    fun updateMaintenance(maintenance: Maintenance) {
        viewModelScope.launch {
            updateMaintenanceUseCase(maintenance)
        }
    }

    fun deleteMaintenance(maintenance: Maintenance) {
        viewModelScope.launch {
            deleteMaintenanceUseCase(maintenance)
        }
    }
}