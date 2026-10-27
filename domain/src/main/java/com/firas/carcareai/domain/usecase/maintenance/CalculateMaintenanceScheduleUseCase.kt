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

class CalculateMaintenanceScheduleUseCase {

    operator fun invoke(
        vehicle: Vehicle,
        maintenances: List<Maintenance>
    ): List<MaintenanceSchedule> {

        return MaintenanceType.entries.map { type ->
            calculateSchedule(
                type = type,
                currentMileage = vehicle.mileage,
                maintenances = maintenances
            )
        }
    }

    private fun calculateSchedule(
        type: MaintenanceType,
        currentMileage: Int,
        maintenances: List<Maintenance>
    ): MaintenanceSchedule {

        if (type == MaintenanceType.TECHNICAL_INSPECTION) {
            return calculateTechnicalInspectionSchedule(
                maintenances = maintenances
            )
        }

        val intervalKm = getIntervalKm(type)

        if (intervalKm == null) {
            return MaintenanceSchedule(
                type = type,
                status = MaintenanceStatus.UNKNOWN
            )
        }

        val lastMaintenance = maintenances
            .filter { it.type == type }
            .maxByOrNull { it.mileage }

        if (lastMaintenance == null) {
            return MaintenanceSchedule(
                type = type,
                status = MaintenanceStatus.UNKNOWN
            )
        }

        val nextMileage = lastMaintenance.mileage + intervalKm
        val remainingKm = nextMileage - currentMileage

        val status = when {
            remainingKm < 0 -> MaintenanceStatus.OVERDUE
            remainingKm <= 2000 -> MaintenanceStatus.SOON

            else -> MaintenanceStatus.OK
        }

        return MaintenanceSchedule(
            type = type,
            lastMaintenanceMileage = lastMaintenance.mileage,
            nextMaintenanceMileage = nextMileage,
            remainingKm = remainingKm,
            lastMaintenanceDate = lastMaintenance.date,
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