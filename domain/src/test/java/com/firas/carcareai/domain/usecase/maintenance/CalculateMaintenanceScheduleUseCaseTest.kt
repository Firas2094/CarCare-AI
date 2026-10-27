package com.firas.carcareai.domain.usecase.maintenance

import com.firas.carcareai.core.model.FuelType
import com.firas.carcareai.core.model.Maintenance
import com.firas.carcareai.core.model.MaintenanceStatus
import com.firas.carcareai.core.model.MaintenanceType
import com.firas.carcareai.core.model.Vehicle
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class CalculateMaintenanceScheduleUseCaseTest {

    private val useCase = CalculateMaintenanceScheduleUseCase()

    private fun createVehicle(
        mileage: Int = 100_000
    ): Vehicle {
        return Vehicle(
            id = 1L,
            brand = "Renault",
            model = "Clio",
            year = 2006,
            mileage = mileage,
            fuelType = FuelType.DIESEL
        )
    }

    private fun createMaintenance(
        type: MaintenanceType,
        mileage: Int = 90_000,
        date: Long = System.currentTimeMillis()
    ): Maintenance {
        return Maintenance(
            id = 1L,
            vehicleId = 1L,
            type = type,
            date = date,
            mileage = mileage
        )
    }

    private fun dateToTimestamp(date: LocalDate): Long {
        return date
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    @Test
    fun `should return UNKNOWN when no maintenance exists`() {

        // Given
        val vehicle = createVehicle()

        // When
        val schedules = useCase(vehicle, emptyList())

        // Then
        val oilChange = schedules.first {
            it.type == MaintenanceType.OIL_CHANGE
        }

        assertEquals(MaintenanceStatus.UNKNOWN, oilChange.status)
    }

    @Test
    fun `should return OK when oil change is not due soon`() {

        // Given
        val vehicle = createVehicle(mileage = 100_000)

        val maintenance = createMaintenance(
            type = MaintenanceType.OIL_CHANGE,
            mileage = 95_000
        )

        // When
        val schedules = useCase(vehicle, listOf(maintenance))

        // Then
        val oilChange = schedules.first {
            it.type == MaintenanceType.OIL_CHANGE
        }

        assertEquals(MaintenanceStatus.OK, oilChange.status)
        assertEquals(105_000, oilChange.nextMaintenanceMileage)
        assertEquals(5_000, oilChange.remainingKm)
    }

    @Test
    fun `should return SOON when oil change is approaching`() {

        // Given
        val vehicle = createVehicle(mileage = 100_000)

        val maintenance = createMaintenance(
            type = MaintenanceType.OIL_CHANGE,
            mileage = 91_000
        )

        // When
        val schedules = useCase(vehicle, listOf(maintenance))

        // Then
        val oilChange = schedules.first {
            it.type == MaintenanceType.OIL_CHANGE
        }

        assertEquals(MaintenanceStatus.SOON, oilChange.status)
        assertEquals(1_000, oilChange.remainingKm)
    }

    @Test
    fun `should return OVERDUE when oil change is overdue`() {

        // Given
        val vehicle = createVehicle(mileage = 100_000)

        val maintenance = createMaintenance(
            type = MaintenanceType.OIL_CHANGE,
            mileage = 89_500
        )

        // When
        val schedules = useCase(vehicle, listOf(maintenance))

        // Then
        val oilChange = schedules.first {
            it.type == MaintenanceType.OIL_CHANGE
        }

        assertEquals(MaintenanceStatus.OVERDUE, oilChange.status)
        assertEquals(-500, oilChange.remainingKm)
    }

    @Test
    fun `should use latest maintenance mileage`() {

        // Given
        val vehicle = createVehicle(mileage = 100_000)

        val oldMaintenance = createMaintenance(
            type = MaintenanceType.OIL_CHANGE,
            mileage = 80_000
        )

        val latestMaintenance = createMaintenance(
            type = MaintenanceType.OIL_CHANGE,
            mileage = 95_000
        )

        // When
        val schedules = useCase(
            vehicle,
            listOf(oldMaintenance, latestMaintenance)
        )

        // Then
        val oilChange = schedules.first {
            it.type == MaintenanceType.OIL_CHANGE
        }

        assertEquals(95_000, oilChange.lastMaintenanceMileage)
        assertEquals(105_000, oilChange.nextMaintenanceMileage)
        assertEquals(MaintenanceStatus.OK, oilChange.status)
    }

    @Test
    fun `should return SOON when technical inspection is due in 30 days`() {

        // Given
        val vehicle = createVehicle()

        val lastInspectionDate = LocalDate.now()
            .minusYears(2)
            .plusDays(30)

        val maintenance = createMaintenance(
            type = MaintenanceType.TECHNICAL_INSPECTION,
            date = dateToTimestamp(lastInspectionDate)
        )

        // When
        val schedules = useCase(vehicle, listOf(maintenance))

        // Then
        val inspection = schedules.first {
            it.type == MaintenanceType.TECHNICAL_INSPECTION
        }

        assertEquals(MaintenanceStatus.SOON, inspection.status)
        assertEquals(30L, inspection.remainingDays)
    }

    @Test
    fun `should return OVERDUE when technical inspection has expired`() {

        // Given
        val vehicle = createVehicle()

        val lastInspectionDate = LocalDate.now()
            .minusYears(2)
            .minusDays(10)

        val maintenance = createMaintenance(
            type = MaintenanceType.TECHNICAL_INSPECTION,
            date = dateToTimestamp(lastInspectionDate)
        )

        // When
        val schedules = useCase(vehicle, listOf(maintenance))

        // Then
        val inspection = schedules.first {
            it.type == MaintenanceType.TECHNICAL_INSPECTION
        }

        assertEquals(MaintenanceStatus.OVERDUE, inspection.status)
        assertEquals(-10L, inspection.remainingDays)
    }

    @Test
    fun `should return UNKNOWN for OTHER maintenance type`() {

        // Given
        val vehicle = createVehicle()

        val maintenance = createMaintenance(
            type = MaintenanceType.OTHER
        )

        // When
        val schedules = useCase(vehicle, listOf(maintenance))

        // Then
        val other = schedules.first {
            it.type == MaintenanceType.OTHER
        }

        assertEquals(MaintenanceStatus.UNKNOWN, other.status)
    }

    @Test
    fun `should return SOON when exactly 2000 km remain`() {

        // Given
        val vehicle = createVehicle(mileage = 100_000)

        val maintenance = createMaintenance(
            type = MaintenanceType.OIL_CHANGE,
            mileage = 92_000
        )

        // When
        val schedules = useCase(vehicle, listOf(maintenance))

        // Then
        val oilChange = schedules.first {
            it.type == MaintenanceType.OIL_CHANGE
        }

        assertEquals(MaintenanceStatus.SOON, oilChange.status)
        assertEquals(2_000, oilChange.remainingKm)
    }

    @Test
    fun `should return SOON when exactly zero km remain`() {

        // Given
        val vehicle = createVehicle(mileage = 100_000)

        val maintenance = createMaintenance(
            type = MaintenanceType.OIL_CHANGE,
            mileage = 90_000
        )

        // When
        val schedules = useCase(vehicle, listOf(maintenance))

        // Then
        val oilChange = schedules.first {
            it.type == MaintenanceType.OIL_CHANGE
        }

        assertEquals(MaintenanceStatus.SOON, oilChange.status)
        assertEquals(0, oilChange.remainingKm)
    }

    @Test
    fun `should return SOON when technical inspection is due in 60 days`() {

        // Given
        val vehicle = createVehicle()

        val lastInspectionDate = LocalDate.now()
            .minusYears(2)
            .plusDays(60)

        val maintenance = createMaintenance(
            type = MaintenanceType.TECHNICAL_INSPECTION,
            date = dateToTimestamp(lastInspectionDate)
        )

        // When
        val schedules = useCase(vehicle, listOf(maintenance))

        // Then
        val inspection = schedules.first {
            it.type == MaintenanceType.TECHNICAL_INSPECTION
        }

        assertEquals(MaintenanceStatus.SOON, inspection.status)
        assertEquals(60L, inspection.remainingDays)
    }
}