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

                // Récupère les intervalles personnalisés enregistrés dans Room.
                val customIntervals = appContainer.getMaintenanceIntervalsUseCase(
                    vehicle.id
                ).first()

                val schedules = appContainer.calculateMaintenanceScheduleUseCase(
                    vehicle = vehicle,
                    maintenances = maintenances,
                    customIntervals = customIntervals
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


                        val remainingKm = schedule.remainingKm
                        val remainingDays = schedule.remainingDays
                        val message = when {

                            // Entretien déjà dépassé en kilomètres.
                            remainingKm != null && remainingKm < 0 -> {
                                "Votre ${vehicle.brand} ${vehicle.model} " +
                                        "a dépassé l'échéance de ${-remainingKm} km."
                            }

                            // Entretien déjà dépassé en jours.
                            remainingDays != null && remainingDays < 0 -> {
                                "Votre ${vehicle.brand} ${vehicle.model} : " +
                                        "échéance dépassée de ${-remainingDays} jours."
                            }

                            // Entretien à prévoir prochainement en kilomètres.
                            remainingKm != null && remainingKm <= 2000 -> {
                                "Votre ${vehicle.brand} ${vehicle.model} : " +
                                        "encore $remainingKm km avant l'entretien."
                            }

                            // Entretien à prévoir prochainement en jours.
                            remainingDays != null && remainingDays <= 60 -> {
                                "Votre ${vehicle.brand} ${vehicle.model} : " +
                                        "échéance dans $remainingDays jours."
                            }

                            // Message de secours.
                            else -> {
                                "Un entretien de votre ${vehicle.brand} " +
                                        "${vehicle.model} nécessite votre attention."
                            }
                        }

                        // Identifiant stable pour un véhicule et un type d'entretien
                        val notificationId =
                            "${vehicle.id}_${schedule.type.name}".hashCode()

                        val reminderSignature = buildString {
                            append(schedule.nextMaintenanceMileage ?: "no_km")
                            append("_")
                            append(schedule.nextMaintenanceDate ?: "no_date")
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

                                Log.d(
                                    "CarCareWorker",
                                    "Nouveau rappel enregistré : ${schedule.type.name}"
                                )

                            } else {

                                Log.d(
                                    "CarCareWorker",
                                    "Notification non envoyée : ${schedule.type.name}"
                                )
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