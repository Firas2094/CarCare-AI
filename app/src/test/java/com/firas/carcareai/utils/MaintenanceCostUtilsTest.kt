package com.firas.carcareai.utils

import com.firas.carcareai.core.model.Maintenance
import com.firas.carcareai.core.model.MaintenanceType
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Test


/**
 * Tests unitaires des informations financières
 * et du garage associées à un entretien.
 */
class MaintenanceExpensesTest {

    @Test
    fun `maintenance conserve le cout le garage et les notes`() {
        val maintenance = Maintenance(
            id = 1L,
            vehicleId = 10L,
            type = MaintenanceType.OIL_CHANGE,
            date = 1730000000000L,
            mileage = 125000,
            costCents = 12550L,
            garageName = "Norauto",
            notes = "Vidange et filtre à huile"
        )

        assertEquals(12550L, maintenance.costCents)
        assertEquals("Norauto", maintenance.garageName)
        assertEquals("Vidange et filtre à huile", maintenance.notes)
    }

    @Test
    fun `maintenance accepte les informations facultatives absentes`() {
        val maintenance = Maintenance(
            vehicleId = 10L,
            type = MaintenanceType.OIL_CHANGE,
            date = 1730000000000L,
            mileage = 125000
        )

        assertNull(maintenance.costCents)
        assertNull(maintenance.garageName)
        assertNull(maintenance.notes)
    }

    @Test
    fun `maintenance accepte un cout nul en euros`() {
        val maintenance = Maintenance(
            vehicleId = 10L,
            type = MaintenanceType.BRAKES,
            date = 1730000000000L,
            mileage = 125000,
            costCents = 0L
        )

        assertEquals(0L, maintenance.costCents)
    }
}