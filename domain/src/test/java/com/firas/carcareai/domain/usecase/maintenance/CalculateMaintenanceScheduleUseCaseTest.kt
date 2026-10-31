package com.firas.carcareai.domain.usecase.maintenance

import com.firas.carcareai.core.model.FuelType
import com.firas.carcareai.core.model.Maintenance
import com.firas.carcareai.core.model.MaintenanceInterval
import com.firas.carcareai.core.model.MaintenanceStatus
import com.firas.carcareai.core.model.MaintenanceType
import com.firas.carcareai.core.model.Vehicle
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit

class CalculateMaintenanceScheduleUseCaseTest {

    private val useCase = CalculateMaintenanceScheduleUseCase()

    /**
     * Crée un véhicule fictif pour les tests unitaires.
     *
     * Le véhicule possède un identifiant fixe (1L).
     * Seul son kilométrage varie selon le scénario testé.
     *
     * Kilométrage par défaut : 100 000 km.
     */
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

    /**
     * Crée un entretien fictif pour les tests unitaires.
     *
     * Permet de personnaliser :
     * - Le type d'entretien.
     * - Le kilométrage auquel il a été effectué.
     * - Sa date de réalisation.
     *
     * L'entretien est associé au véhicule ayant l'identifiant 1L.
     */
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

    /**
     * Convertit une date LocalDate en timestamp Unix (millisecondes).
     *
     * Cette fonction est utilisée pour simuler des dates
     * de contrôle technique dans les tests.
     */
    private fun dateToTimestamp(date: LocalDate): Long {
        return date
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    /**
     * Vérifie que le statut d'un entretien est UNKNOWN
     * lorsqu'aucun historique de maintenance n'existe.
     *
     * Sans dernière maintenance connue, l'application
     * ne peut pas calculer la prochaine échéance.
     */
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

    /**
     * Vérifie que le statut est OK lorsque la prochaine
     * vidange est encore suffisamment éloignée.
     *
     * Exemple :
     * - Kilométrage actuel : 100 000 km
     * - Dernière vidange : 95 000 km
     * - Intervalle par défaut : 10 000 km
     * - Prochaine vidange : 105 000 km
     * - Distance restante : 5 000 km
     *
     * Comme il reste plus de 2 000 km, le statut doit être OK.
     */
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

    /**
     * Vérifie que le statut devient SOON lorsque
     * la prochaine vidange approche.
     *
     * Exemple :
     * - Kilométrage actuel : 100 000 km
     * - Dernière vidange : 91 000 km
     * - Prochaine vidange : 101 000 km
     * - Distance restante : 1 000 km
     *
     * Comme il reste moins de 2 000 km, le statut doit être SOON.
     */
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

    /**
     * Vérifie que le statut devient OVERDUE lorsque
     * le kilométrage prévu pour la vidange est dépassé.
     *
     * Exemple :
     * - Kilométrage actuel : 100 000 km
     * - Dernière vidange : 89 500 km
     * - Prochaine vidange : 99 500 km
     * - Distance restante : -500 km
     *
     * Le véhicule a dépassé l'échéance de 500 km.
     */
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

    /**
     * Vérifie que le calcul utilise l'entretien possédant
     * le kilométrage le plus élevé.
     *
     * Exemple :
     * - Ancienne vidange : 80 000 km
     * - Dernière vidange : 95 000 km
     *
     * Le calcul doit partir de 95 000 km et non de 80 000 km.
     *
     * Prochaine vidange attendue : 105 000 km.
     */
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

    /**
     * Vérifie que le contrôle technique possède le statut SOON
     * lorsqu'il expire dans 30 jours.
     *
     * La règle actuelle prévoit un contrôle tous les 2 ans.
     *
     * Comme il reste 30 jours, soit moins que le seuil
     * d'alerte de 60 jours, le statut doit être SOON.
     */
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

    /**
     * Vérifie que le contrôle technique possède le statut OVERDUE
     * lorsque sa date d'expiration est dépassée.
     *
     * Exemple :
     * - Dernier contrôle : il y a 2 ans et 10 jours
     * - Prochaine échéance : il y a 10 jours
     * - Jours restants : -10
     *
     * Le statut attendu est OVERDUE.
     */
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

    /**
     * Vérifie que le type de maintenance OTHER retourne UNKNOWN.
     *
     * Ce type ne possède aucun intervalle kilométrique
     * prédéfini dans le UseCase actuel.
     *
     * Sans intervalle connu, l'application ne peut pas
     * déterminer automatiquement la prochaine échéance.
     */
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

    /**
     * Vérifie le comportement à la limite exacte
     * du seuil d'alerte kilométrique.
     *
     * Exemple :
     * - Kilométrage actuel : 100 000 km
     * - Dernière vidange : 92 000 km
     * - Prochaine vidange : 102 000 km
     * - Distance restante : 2 000 km
     *
     * Le seuil étant inclusif (<= 2 000 km),
     * le statut attendu est SOON.
     */
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

    /**
     * Vérifie le comportement lorsque le véhicule atteint
     * exactement le kilométrage prévu pour la maintenance.
     *
     * Exemple :
     * - Kilométrage actuel : 100 000 km
     * - Dernière vidange : 90 000 km
     * - Prochaine vidange : 100 000 km
     * - Distance restante : 0 km
     *
     * Dans la logique actuelle, une échéance atteinte
     * mais non dépassée possède le statut SOON.
     */
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

    /**
     * Vérifie le comportement à la limite exacte
     * du seuil d'alerte temporel du contrôle technique.
     *
     * Exemple :
     * - Prochaine échéance dans exactement 60 jours
     * - Seuil d'alerte : 60 jours
     *
     * Le seuil étant inclusif (<= 60 jours),
     * le statut attendu est SOON.
     */
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

    /**
     * Vérifie qu'un intervalle kilométrique personnalisé
     * remplace l'intervalle par défaut.
     *
     * Exemple :
     * - Dernière vidange : 50 000 km
     * - Intervalle par défaut : 10 000 km
     * - Intervalle personnalisé : 15 000 km
     * - Kilométrage actuel : 58 000 km
     *
     * Résultats attendus :
     * - Prochaine vidange : 65 000 km
     * - Distance restante : 7 000 km
     * - Statut : OK
     *
     * Ce test protège la fonctionnalité de personnalisation
     * contre une éventuelle régression.
     */
    @Test
    fun `should use custom mileage interval instead of default interval`() {

        // Given
        val vehicle = createVehicle(
            mileage = 58_000
        )

        val maintenance = createMaintenance(
            type = MaintenanceType.OIL_CHANGE,
            mileage = 50_000
        )

        val customInterval = MaintenanceInterval(
            vehicleId = vehicle.id,
            type = MaintenanceType.OIL_CHANGE,
            intervalKm = 15_000
        )

        // When
        val schedules = useCase(
            vehicle = vehicle,
            maintenances = listOf(maintenance),
            customIntervals = listOf(customInterval)
        )

        // Then
        val oilChangeSchedule = schedules.first {
            it.type == MaintenanceType.OIL_CHANGE
        }

        assertEquals(65_000, oilChangeSchedule.nextMaintenanceMileage)
        assertEquals(7_000, oilChangeSchedule.remainingKm)
        assertEquals(MaintenanceStatus.OK, oilChangeSchedule.status)
    }


    /**
     * Vérifie qu'un intervalle personnalisé appartenant
     * à un autre véhicule n'affecte pas le calcul.
     *
     * Exemple :
     * - Véhicule testé : ID 1
     * - Dernière vidange : 50 000 km
     * - Intervalle personnalisé : 15 000 km, mais pour le véhicule ID 2
     * - Intervalle par défaut : 10 000 km
     *
     * Résultats attendus :
     * - Prochaine vidange : 60 000 km
     * - Distance restante : 2 000 km
     * - Statut : SOON
     */
    @Test
    fun `should ignore custom interval from another vehicle`() {

        // Given
        val vehicle = createVehicle(
            mileage = 58_000
        )

        val maintenance = createMaintenance(
            type = MaintenanceType.OIL_CHANGE,
            mileage = 50_000
        )

        val customInterval = MaintenanceInterval(
            vehicleId = 2L,
            type = MaintenanceType.OIL_CHANGE,
            intervalKm = 15_000
        )

        // When
        val schedules = useCase(
            vehicle = vehicle,
            maintenances = listOf(maintenance),
            customIntervals = listOf(customInterval)
        )

        // Then
        val oilChangeSchedule = schedules.first {
            it.type == MaintenanceType.OIL_CHANGE
        }

        assertEquals(60_000, oilChangeSchedule.nextMaintenanceMileage)
        assertEquals(2_000, oilChangeSchedule.remainingKm)
        assertEquals(MaintenanceStatus.SOON, oilChangeSchedule.status)
    }

    /**
     * Vérifie qu'un intervalle personnalisé associé à un autre
     * type de maintenance n'affecte pas le calcul de la vidange.
     *
     * Exemple :
     * - Véhicule testé : ID 1
     * - Dernière vidange : 50 000 km
     * - Intervalle personnalisé des pneus : 30 000 km
     * - Intervalle de vidange par défaut : 10 000 km
     *
     * Résultats attendus pour la vidange :
     * - Prochaine vidange : 60 000 km
     * - Distance restante : 2 000 km
     * - Statut : SOON
     */
    @Test
    fun `should ignore custom interval for another maintenance type`() {

        // Given
        val vehicle = createVehicle(
            mileage = 58_000
        )

        val maintenance = createMaintenance(
            type = MaintenanceType.OIL_CHANGE,
            mileage = 50_000
        )

        val customInterval = MaintenanceInterval(
            vehicleId = vehicle.id,
            type = MaintenanceType.TIRES,
            intervalKm = 30_000
        )

        // When
        val schedules = useCase(
            vehicle = vehicle,
            maintenances = listOf(maintenance),
            customIntervals = listOf(customInterval)
        )

        // Then
        val oilChangeSchedule = schedules.first {
            it.type == MaintenanceType.OIL_CHANGE
        }

        assertEquals(60_000, oilChangeSchedule.nextMaintenanceMileage)
        assertEquals(2_000, oilChangeSchedule.remainingKm)
        assertEquals(MaintenanceStatus.SOON, oilChangeSchedule.status)
    }

    /**
     * Vérifie qu'un intervalle temporel personnalisé
     * permet de calculer la prochaine échéance d'entretien.
     *
     * Exemple :
     * - Dernière vidange : il y a 6 mois
     * - Intervalle personnalisé : 12 mois
     * - Prochaine vidange : 12 mois après la dernière
     *
     * Résultats attendus :
     * - Date de prochaine vidange correctement calculée
     * - Environ 6 mois restants
     * - Statut : OK
     */
    @Test
    fun `should calculate next maintenance date using custom month interval`() {

        // Given
        val vehicle = createVehicle(mileage = 58_000)

        val lastMaintenanceDate = LocalDate.now()
            .minusMonths(6)

        val maintenance = createMaintenance(
            type = MaintenanceType.OIL_CHANGE,
            mileage = 50_000,
            date = dateToTimestamp(lastMaintenanceDate)
        )

        val customInterval = MaintenanceInterval(
            vehicleId = vehicle.id,
            type = MaintenanceType.OIL_CHANGE,
            intervalMonths = 12
        )

        // When
        val schedules = useCase(
            vehicle = vehicle,
            maintenances = listOf(maintenance),
            customIntervals = listOf(customInterval)
        )

        // Then
        val oilChangeSchedule = schedules.first {
            it.type == MaintenanceType.OIL_CHANGE
        }

        val expectedNextDate = lastMaintenanceDate.plusMonths(12)

        assertEquals(
            dateToTimestamp(expectedNextDate),
            oilChangeSchedule.nextMaintenanceDate
        )

        assertEquals(

            ChronoUnit.DAYS.between(LocalDate.now(), expectedNextDate),
            oilChangeSchedule.remainingDays
        )

        assertEquals(
            MaintenanceStatus.OK,
            oilChangeSchedule.status
        )
    }
}