package com.firas.carcareai.core.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class MaintenanceIntervalTest {

    /**
     * Vérifie qu'un intervalle de maintenance peut être défini
     * uniquement en kilomètres, sans intervalle temporel.
     *
     * Exemple : vidange tous les 10 000 km.
     */
    @Test
    fun `should create maintenance interval with mileage only`() {

        // Given
        val vehicleId = 1L
        val intervalKm = 10_000

        // When
        val interval = MaintenanceInterval(
            vehicleId = vehicleId,
            type = MaintenanceType.OIL_CHANGE,
            intervalKm = intervalKm
        )

        // Then
        assertEquals(vehicleId, interval.vehicleId)
        assertEquals(MaintenanceType.OIL_CHANGE, interval.type)
        assertEquals(intervalKm, interval.intervalKm)
        assertNull(interval.intervalMonths)
    }

    /**
     * Vérifie qu'un intervalle de maintenance peut être défini
     * uniquement en mois, sans intervalle kilométrique.
     *
     * Exemple : entretien tous les 12 mois.
     */
    @Test
    fun `should create maintenance interval with months only`() {

        // Given
        val vehicleId = 1L
        val intervalMonths = 12

        // When
        val interval = MaintenanceInterval(
            vehicleId = vehicleId,
            type = MaintenanceType.OIL_CHANGE,
            intervalMonths = intervalMonths
        )

        // Then
        assertEquals(vehicleId, interval.vehicleId)
        assertEquals(MaintenanceType.OIL_CHANGE, interval.type)
        assertNull(interval.intervalKm)
        assertEquals(intervalMonths, interval.intervalMonths)
    }

    /**
     * Vérifie qu'un intervalle de maintenance peut combiner
     * une distance kilométrique et une durée en mois.
     *
     * Exemple : vidange tous les 10 000 km ou tous les 12 mois.
     *
     * Ce test valide uniquement la création du modèle,
     * pas encore le calcul de la prochaine échéance.
     */
    @Test
    fun `should create maintenance interval with mileage and months`() {

        // Given
        val vehicleId = 1L
        val intervalKm = 10_000
        val intervalMonths = 12

        // When
        val interval = MaintenanceInterval(
            vehicleId = vehicleId,
            type = MaintenanceType.OIL_CHANGE,
            intervalKm = intervalKm,
            intervalMonths = intervalMonths
        )

        // Then
        assertEquals(vehicleId, interval.vehicleId)
        assertEquals(MaintenanceType.OIL_CHANGE, interval.type)
        assertEquals(intervalKm, interval.intervalKm)
        assertEquals(intervalMonths, interval.intervalMonths)
    }

    /**
     * Vérifie qu'il est impossible de créer un intervalle
     * de maintenance sans kilométrage ni durée.
     *
     * Les deux valeurs null doivent provoquer une
     * IllegalArgumentException.
     */
    @Test
    fun `should throw exception when both intervals are null`() {

        // Given
        val vehicleId = 1L

        // When & Then
        val exception = assertThrows(IllegalArgumentException::class.java) {
            MaintenanceInterval(
                vehicleId = vehicleId,
                type = MaintenanceType.OIL_CHANGE,
                intervalKm = null,
                intervalMonths = null
            )
        }

        assertEquals(
            "At least one maintenance interval must be defined",
            exception.message
        )
    }

    /**
     * Vérifie qu'un intervalle kilométrique doit être
     * strictement supérieur à zéro.
     *
     * Les valeurs 0 et -5000 doivent être refusées,
     * même si l'intervalle temporel est valide.
     */
    @Test
    fun `should throw exception when mileage interval is zero or negative`() {

        // Given
        val invalidIntervals = listOf(0, -5000)

        // When & Then
        invalidIntervals.forEach { invalidKm ->

            val exception = assertThrows(IllegalArgumentException::class.java) {
                MaintenanceInterval(
                    vehicleId = 1L,
                    type = MaintenanceType.OIL_CHANGE,
                    intervalKm = invalidKm,
                    intervalMonths = 12
                )
            }

            assertEquals(
                "Mileage interval must be positive",
                exception.message
            )
        }
    }

    /**
     * Vérifie qu'un intervalle temporel doit être
     * strictement supérieur à zéro mois.
     *
     * Les valeurs 0 et -6 doivent être refusées,
     * même si l'intervalle kilométrique est valide.
     */
    @Test
    fun `should throw exception when month interval is zero or negative`() {

        // Given
        val invalidIntervals = listOf(0, -6)

        // When & Then
        invalidIntervals.forEach { invalidMonths ->

            val exception = assertThrows(IllegalArgumentException::class.java) {
                MaintenanceInterval(
                    vehicleId = 1L,
                    type = MaintenanceType.OIL_CHANGE,
                    intervalKm = 10_000,
                    intervalMonths = invalidMonths
                )
            }

            assertEquals(
                "Month interval must be positive",
                exception.message
            )
        }
    }
}
