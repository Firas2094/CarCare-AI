package com.firas.carcareai.presentation.maintenanceinterval

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.firas.carcareai.domain.usecase.maintenance.DeleteMaintenanceIntervalUseCase
import com.firas.carcareai.domain.usecase.maintenance.GetMaintenanceIntervalsUseCase
import com.firas.carcareai.domain.usecase.maintenance.SaveMaintenanceIntervalUseCase

/**
 * Factory responsable de la création du
 * MaintenanceIntervalViewModel.
 *
 * Elle injecte les UseCases fournis par AppContainer.
 */
class MaintenanceIntervalViewModelFactory(
    private val getIntervalsUseCase: GetMaintenanceIntervalsUseCase,
    private val saveIntervalUseCase: SaveMaintenanceIntervalUseCase,
    private val deleteIntervalUseCase: DeleteMaintenanceIntervalUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(
                MaintenanceIntervalViewModel::class.java
            )
        ) {
            return MaintenanceIntervalViewModel(
                getIntervalsUseCase = getIntervalsUseCase,
                saveIntervalUseCase = saveIntervalUseCase,
                deleteIntervalUseCase = deleteIntervalUseCase
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }
}