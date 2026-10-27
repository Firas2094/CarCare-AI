package com.firas.carcareai.presentation.vehicle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firas.carcareai.core.model.Vehicle
import com.firas.carcareai.domain.usecase.vehicle.AddVehicleUseCase
import com.firas.carcareai.domain.usecase.vehicle.DeleteVehicleUseCase
import com.firas.carcareai.domain.usecase.vehicle.GetVehicleByIdUseCase
import com.firas.carcareai.domain.usecase.vehicle.GetVehiclesUseCase
import com.firas.carcareai.domain.usecase.vehicle.UpdateVehicleUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VehicleViewModel (
    getVehiclesUseCase: GetVehiclesUseCase,
    private val addVehicleUseCase: AddVehicleUseCase,
    private val getVehicleByIdUseCase: GetVehicleByIdUseCase,
    private val deleteVehicleUseCase: DeleteVehicleUseCase,
    private val updateVehicleUseCase: UpdateVehicleUseCase,
) : ViewModel() {

    private val _selectedVehicle = MutableStateFlow<Vehicle?>(null)
    val selectedVehicle: StateFlow<Vehicle?> = _selectedVehicle

    val vehicles: StateFlow<List<Vehicle>> =
        getVehiclesUseCase()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    fun addVehicle(vehicle: Vehicle){
        viewModelScope.launch {
            addVehicleUseCase(vehicle)
        }
    }

    fun loadVehicle(id: Long?) {
        viewModelScope.launch {
            _selectedVehicle.value = getVehicleByIdUseCase(id)
        }
    }

    fun deleteVehicle(vehicle: Vehicle) {
        viewModelScope.launch {
            deleteVehicleUseCase(vehicle)
        }
    }

    fun updateVehicle(vehicle: Vehicle) {
        viewModelScope.launch {
            updateVehicleUseCase(vehicle)
        }
    }
}