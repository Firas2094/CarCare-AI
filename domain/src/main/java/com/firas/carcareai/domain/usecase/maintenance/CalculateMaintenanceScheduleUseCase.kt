package com.firas.carcareai.domain.usecase.maintenance

import com.firas.carcareai.core.model.Maintenance
import com.firas.carcareai.core.model.MaintenanceSchedule
import com.firas.carcareai.core.model.MaintenanceStatus
import com.firas.carcareai.core.model.MaintenanceType
import com.firas.carcareai.core.model.Vehicle
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import com.firas.carcareai.core.model.MaintenanceInterval

class CalculateMaintenanceScheduleUseCase {

    operator fun invoke(
        vehicle: Vehicle,
        maintenances: List<Maintenance>,
        customIntervals: List<MaintenanceInterval> = emptyList()
    ): List<MaintenanceSchedule> {

        return MaintenanceType.entries.map { type ->

            // Recherche un intervalle personnalisé pour ce véhicule et ce type de maintenance.
            val customInterval = customIntervals.find {
                it.vehicleId == vehicle.id && it.type == type
            }

            calculateSchedule(
                type = type,
                currentMileage = vehicle.mileage,
                maintenances = maintenances,
                customInterval = customInterval
            )
        }
    }

    private fun calculateSchedule(
        type: MaintenanceType,
        currentMileage: Int,
        maintenances: List<Maintenance>,
        customInterval: MaintenanceInterval?
    ): MaintenanceSchedule {

        if (type == MaintenanceType.TECHNICAL_INSPECTION) {
            return calculateTechnicalInspectionSchedule(
                maintenances = maintenances
            )
        }

        val intervalKm = if (customInterval != null) {
            customInterval.intervalKm
        } else {
            getIntervalKm(type)
        }

        val intervalMonths = customInterval?.intervalMonths

        // Sans historique, impossible de calculer une échéance.
        val lastMaintenance = maintenances
            .filter { it.type == type }
            .maxByOrNull { it.mileage }

        if (lastMaintenance == null) {
            return MaintenanceSchedule(
                type = type,
                status = MaintenanceStatus.UNKNOWN
            )
        }

        val nextMileage = intervalKm?.let {
            lastMaintenance.mileage + it
        }

        val remainingKm = nextMileage?.let {
            it - currentMileage
        }

        // Calcul de l'échéance temporelle, si disponible.
        val nextDate = intervalMonths?.let { months ->

            val lastDate = Instant
                .ofEpochMilli(lastMaintenance.date)
                .atZone(ZoneId.systemDefault())
                .toLocalDate()

            lastDate.plusMonths(months.toLong())
        }

        val remainingDays = nextDate?.let { date ->
            ChronoUnit.DAYS.between(
                LocalDate.now(),
                date
            )
        }

        val nextDateTimestamp = nextDate?.atStartOfDay(ZoneId.systemDefault())?.toInstant()
            ?.toEpochMilli()

        // Détermination du statut kilométrique.
        val mileageStatus = remainingKm?.let { km ->
            when {
                km < 0 -> MaintenanceStatus.OVERDUE
                km <= 2_000 -> MaintenanceStatus.SOON
                else -> MaintenanceStatus.OK
            }
        }

        // Détermination du statut temporel.
        val timeStatus = remainingDays?.let { days ->
            when {
                days < 0 -> MaintenanceStatus.OVERDUE
                days <= 60 -> MaintenanceStatus.SOON
                else -> MaintenanceStatus.OK
            }
        }

        // Le statut global retient l'échéance la plus urgente.
        val status = when {
            mileageStatus == MaintenanceStatus.OVERDUE ||
                    timeStatus == MaintenanceStatus.OVERDUE ->
                MaintenanceStatus.OVERDUE

            mileageStatus == MaintenanceStatus.SOON ||
                    timeStatus == MaintenanceStatus.SOON ->
                MaintenanceStatus.SOON

            mileageStatus == MaintenanceStatus.OK ||
                    timeStatus == MaintenanceStatus.OK ->
                MaintenanceStatus.OK

            else -> MaintenanceStatus.UNKNOWN
        }

        return MaintenanceSchedule(
            type = type,
            lastMaintenanceMileage = lastMaintenance.mileage,
            lastMaintenanceDate = lastMaintenance.date,
            nextMaintenanceMileage = nextMileage,
            remainingKm = remainingKm,
            nextMaintenanceDate = nextDateTimestamp,
            remainingDays = remainingDays,
            status = status
        )
    }

    private fun getIntervalKm(
        type: MaintenanceType
    ): Int? {

        return when (type) {

            MaintenanceType.OIL_CHANGE -> 10_000
            MaintenanceType.AIR_FILTER -> 20_000
            MaintenanceType.TIRES -> 40_000
            MaintenanceType.BRAKES -> 40_000
            MaintenanceType.BATTERY -> 60_000
            MaintenanceType.TIMING_BELT -> 100_000
            MaintenanceType.TECHNICAL_INSPECTION -> null
            MaintenanceType.OTHER -> null
        }
    }

    private fun calculateTechnicalInspectionSchedule(
        maintenances: List<Maintenance>
    ): MaintenanceSchedule {

        val lastMaintenance = maintenances
            .filter {
                it.type == MaintenanceType.TECHNICAL_INSPECTION
            }
            .maxByOrNull {
                it.date
            }

        if (lastMaintenance == null) {
            return MaintenanceSchedule(
                type = MaintenanceType.TECHNICAL_INSPECTION,
                status = MaintenanceStatus.UNKNOWN
            )
        }

        val lastDate = Instant
            .ofEpochMilli(lastMaintenance.date)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()

        val nextDate = lastDate.plusYears(2)

        val today = LocalDate.now()

        val remainingDays = ChronoUnit.DAYS.between(
            today,
            nextDate
        )

        val status = when {
            remainingDays < 0 ->
                MaintenanceStatus.OVERDUE

            remainingDays <= 60 ->
                MaintenanceStatus.SOON

            else ->
                MaintenanceStatus.OK
        }

        val nextDateTimestamp = nextDate
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        return MaintenanceSchedule(
            type = MaintenanceType.TECHNICAL_INSPECTION,
            lastMaintenanceMileage = lastMaintenance.mileage,
            lastMaintenanceDate = lastMaintenance.date,
            nextMaintenanceDate = nextDateTimestamp,
            remainingDays = remainingDays,
            status = status
        )
    }
}