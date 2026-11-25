package com.firas.carcareai.presentation.reminders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.firas.carcareai.core.model.MaintenanceReminder
import com.firas.carcareai.domain.usecase.maintenance.GetMaintenanceRemindersUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel du centre de rappels d'entretien.
 *
 * Observe les rappels de tous les véhicules
 * et expose une liste réactive à l'interface.
 */
class RemindersViewModel(
    getMaintenanceRemindersUseCase: GetMaintenanceRemindersUseCase
) : ViewModel() {

    val reminders: StateFlow<List<MaintenanceReminder>> =
        getMaintenanceRemindersUseCase()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )
}