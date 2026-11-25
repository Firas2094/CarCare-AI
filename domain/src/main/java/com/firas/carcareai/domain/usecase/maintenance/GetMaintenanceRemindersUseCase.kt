package com.firas.carcareai.domain.usecase.maintenance

import com.firas.carcareai.core.model.MaintenanceReminder
import com.firas.carcareai.core.model.MaintenanceStatus
import com.firas.carcareai.core.model.Vehicle
import com.firas.carcareai.domain.repository.MaintenanceRepository
import com.firas.carcareai.domain.repository.VehicleRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

/**
 * Observe les rappels d'entretien de tous les véhicules.
 *
 * Recalcule automatiquement les rappels lorsque les véhicules,
 * les entretiens ou les intervalles personnalisés changent.
 */
class GetMaintenanceRemindersUseCase(
    private val vehicleRepository: VehicleRepository,
    private val maintenanceRepository: MaintenanceRepository,
    private val getMaintenanceIntervalsUseCase: GetMaintenanceIntervalsUseCase,
    private val calculateMaintenanceScheduleUseCase: CalculateMaintenanceScheduleUseCase
) {

    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(): Flow<List<MaintenanceReminder>> {
        return vehicleRepository.getVehicles()
            .flatMapLatest { vehicles ->

                if (vehicles.isEmpty()) {
                    flowOf(emptyList<MaintenanceReminder>())
                } else {

                    val reminderFlows = vehicles.map { vehicle ->
                        observeVehicleReminders(vehicle)
                    }

                    combine(reminderFlows) { reminders ->
                        reminders
                            .flatMap { it }
                            .sortedBy { reminder ->
                                when (reminder.schedule.status) {
                                    MaintenanceStatus.OVERDUE -> 0
                                    MaintenanceStatus.SOON -> 1
                                    else -> 2
                                }
                            }
                    }
                }
            }
    }

    /**
     * Observe les rappels d'entretien d'un véhicule.
     */
    private fun observeVehicleReminders(
        vehicle: Vehicle
    ): Flow<List<MaintenanceReminder>> {

        return combine(
            maintenanceRepository.getMaintenancesByVehicle(vehicle.id),
            getMaintenanceIntervalsUseCase(vehicle.id)
        ) { maintenances, customIntervals ->

            calculateMaintenanceScheduleUseCase(
                vehicle = vehicle,
                maintenances = maintenances,
                customIntervals = customIntervals
            )
                .filter { schedule ->
                    schedule.status == MaintenanceStatus.SOON ||
                            schedule.status == MaintenanceStatus.OVERDUE
                }
                .map { schedule ->
                    MaintenanceReminder(
                        vehicle = vehicle,
                        schedule = schedule
                    )
                }
        }
    }
}