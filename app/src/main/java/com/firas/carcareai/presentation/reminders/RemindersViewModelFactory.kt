package com.firas.carcareai.presentation.reminders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.firas.carcareai.domain.usecase.maintenance.GetMaintenanceRemindersUseCase

/**
 * Factory responsable de la création du RemindersViewModel
 * et de l'injection de ses dépendances.
 */
class RemindersViewModelFactory(
    private val getMaintenanceRemindersUseCase: GetMaintenanceRemindersUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(RemindersViewModel::class.java)) {
            return RemindersViewModel(
                getMaintenanceRemindersUseCase
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class")
    }
}