package com.firas.carcareai.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.firas.carcareai.core.model.MaintenanceStatus
import com.firas.carcareai.core.model.toFrenchLabel
import com.firas.carcareai.di.AppContainer
import com.firas.carcareai.notification.MaintenanceNotificationManager
import com.firas.carcareai.notification.MaintenanceReminderStore
import kotlinx.coroutines.flow.first

class MaintenanceReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return try {

            val appContainer = AppContainer(applicationContext)

            val notificationManager =
                MaintenanceNotificationManager(applicationContext)

            val reminderStore = MaintenanceReminderStore(applicationContext)

            val vehicles = appContainer.vehicleRepository
                .getVehicles()
                .first()

            Log.d("CarCareWorker", "Nombre de véhicules : ${vehicles.size}")

            vehicles.forEach { vehicle ->
                val maintenances = appContainer.maintenanceRepository
                    .getMaintenancesByVehicle(vehicle.id)
                    .first()

                val schedules =
                    appContainer.calculateMaintenanceScheduleUseCase(
                        vehicle = vehicle,
                        maintenances = maintenances
                    )

                schedules
                    .filter { schedule ->
                        schedule.status == MaintenanceStatus.SOON ||
                                schedule.status == MaintenanceStatus.OVERDUE
                    }.forEach { schedule ->
                        Log.d(
                            "CarCareWorker",
                            """
                            Véhicule : ${vehicle.brand} ${vehicle.model}
                            Entretien : ${schedule.type.toFrenchLabel()}
                            Statut : ${schedule.status}
                            Km restants : ${schedule.remainingKm}
                            Jours restants : ${schedule.remainingDays}
                            """.trimIndent()
                        )

                        val maintenanceName = schedule.type.toFrenchLabel()

                        val title = when (schedule.status) {
                            MaintenanceStatus.OVERDUE ->
                                "Entretien en retard : $maintenanceName"

                            else ->
                                "Entretien à prévoir : $maintenanceName"
                        }

                        val message = when {
                            schedule.remainingKm != null -> {
                                val km = schedule.remainingKm ?: 0

                                if (km < 0) {
                                    "Votre ${vehicle.brand} ${vehicle.model} " +
                                            "a dépassé l'échéance de ${-km} km."
                                } else {
                                    "Votre ${vehicle.brand} ${vehicle.model} : " +
                                            "encore $km km avant l'entretien."
                                }
                            }

                            schedule.remainingDays != null -> {
                                val days = schedule.remainingDays ?: 0L

                                if (days < 0) {
                                    "Votre ${vehicle.brand} ${vehicle.model} : " +
                                            "échéance dépassée de ${-days} jours."
                                } else {
                                    "Votre ${vehicle.brand} ${vehicle.model} : " +
                                            "échéance dans $days jours."
                                }
                            }

                            else -> "Un entretien de votre véhicule nécessite votre attention."
                        }

                    // Identifiant stable pour un véhicule et un type d'entretien
                        val notificationId =
                            "${vehicle.id}_${schedule.type.name}".hashCode()

                        val reminderSignature = buildString {
                            append(schedule.nextMaintenanceMileage ?: schedule.nextMaintenanceDate ?: "unknown")
                            append("_")
                            append(schedule.status.name)
                        }

                        val alreadySent = reminderStore.wasAlreadySent(
                            vehicleId = vehicle.id,
                            maintenanceType = schedule.type.name,
                            reminderSignature = reminderSignature
                        )

                        if (!alreadySent) {

                            val notificationSent = notificationManager.showNotification(
                                id = notificationId,
                                vehicleId = vehicle.id,
                                title = title,
                                message = message
                            )

                            if (notificationSent) {

                                reminderStore.markAsSent(
                                    vehicleId = vehicle.id,
                                    maintenanceType = schedule.type.name,
                                    reminderSignature = reminderSignature
                                )

                                Log.d("CarCareWorker", "Nouveau rappel enregistré : ${schedule.type.name}")

                            } else {

                                Log.d("CarCareWorker", "Notification non envoyée : ${schedule.type.name}")
                            }

                        } else {

                            Log.d("CarCareWorker", "Rappel déjà envoyé : ${schedule.type.name}")
                        }
                    }


            }

            Log.d("CarCareWorker", "Vérification terminée")

            Result.success()

        } catch (e: Exception) {

            Log.e("CarCareWorker", "Erreur pendant la vérification", e)
            Result.retry()
        }
    }
}