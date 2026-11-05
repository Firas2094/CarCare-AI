package com.firas.carcareai.domain.usecase.maintenance

import com.firas.carcareai.core.model.Maintenance
import com.firas.carcareai.core.model.MaintenanceType
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

/**
 * Tests unitaires du calcul des statistiques
 * d'entretien d'un véhicule.
 */
class GetMaintenanceStatisticsUseCaseTest {

    private val useCase = GetMaintenanceStatisticsUseCase()

    private val maintenanceType = MaintenanceType.entries.first()

    /**
     * Crée un entretien de test.
     * La date est convertie en timestamp, comme dans l'application.
     */
    private fun createMaintenance(
        id: Long,
        date: LocalDate,
        costCents: Long?,
        type: MaintenanceType = maintenanceType
    ): Maintenance {
        return Maintenance(
            id = id,
            vehicleId = 1L,
            type = type,
            date = date.atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli(),
            mileage = 100000,
            costCents = costCents
        )
    }

    @Test
    fun `empty list returns zero statistics`() {
        // GIVEN : aucun entretien.
        val maintenances = emptyList<Maintenance>()

        // WHEN : calcul des statistiques.
        val result = useCase(maintenances)

        // THEN : tous les indicateurs sont à zéro.
        assertEquals(0, result.maintenanceCount)
        assertEquals(0, result.paidMaintenanceCount)
        assertEquals(0L, result.totalCostCents)
        assertEquals(0L, result.averageCostCents)
        assertTrue(result.monthlyExpenses.isEmpty())
        assertTrue(result.expensesByType.isEmpty())
    }

    @Test
    fun `calculates total and average expenses`() {
        // GIVEN : trois entretiens avec des coûts.
        val maintenances = listOf(
            createMaintenance(1L, LocalDate.of(2026, 1, 10), 10000L),
            createMaintenance(2L, LocalDate.of(2026, 1, 15), 20000L),
            createMaintenance(3L, LocalDate.of(2026, 2, 5), 30000L)
        )

        // WHEN : calcul des statistiques.
        val result = useCase(maintenances)

        // THEN : total 600 €, moyenne 200 €.
        assertEquals(3, result.maintenanceCount)
        assertEquals(3, result.paidMaintenanceCount)
        assertEquals(60000L, result.totalCostCents)
        assertEquals(20000L, result.averageCostCents)
    }

    @Test
    fun `ignores missing costs in average calculation`() {
        // GIVEN : un entretien payant et un sans coût.
        val maintenances = listOf(
            createMaintenance(1L, LocalDate.of(2026, 1, 10), 15000L),
            createMaintenance(2L, LocalDate.of(2026, 1, 15), null)
        )

        // WHEN : calcul des statistiques.
        val result = useCase(maintenances)

        // THEN : la moyenne exclut le coût absent.
        assertEquals(2, result.maintenanceCount)
        assertEquals(1, result.paidMaintenanceCount)
        assertEquals(15000L, result.totalCostCents)
        assertEquals(15000L, result.averageCostCents)
    }

    @Test
    fun `groups expenses by month`() {
        // GIVEN : deux dépenses en janvier, une en février.
        val maintenances = listOf(
            createMaintenance(1L, LocalDate.of(2026, 1, 10), 10000L),
            createMaintenance(2L, LocalDate.of(2026, 1, 20), 5000L),
            createMaintenance(3L, LocalDate.of(2026, 2, 5), 20000L)
        )

        // WHEN : calcul des statistiques.
        val result = useCase(maintenances)

        // THEN : regroupement chronologique par mois.
        assertEquals(2, result.monthlyExpenses.size)
        assertEquals("2026-01", result.monthlyExpenses[0].month)
        assertEquals(15000L, result.monthlyExpenses[0].totalCents)
        assertEquals("2026-02", result.monthlyExpenses[1].month)
        assertEquals(20000L, result.monthlyExpenses[1].totalCents)
    }

    @Test
    fun `groups expenses by maintenance type`() {
        // GIVEN : deux entretiens du même type.
        val maintenances = listOf(
            createMaintenance(1L, LocalDate.of(2026, 1, 10), 10000L),
            createMaintenance(2L, LocalDate.of(2026, 2, 10), 25000L)
        )

        // WHEN : calcul des statistiques.
        val result = useCase(maintenances)

        // THEN : les dépenses sont regroupées.
        assertEquals(1, result.expensesByType.size)
        assertEquals(maintenanceType, result.expensesByType[0].type)
        assertEquals(35000L, result.expensesByType[0].totalCents)
    }

    @Test
    fun `zero cost is counted as specified expense`() {
        // GIVEN : un entretien gratuit.
        val maintenances = listOf(
            createMaintenance(1L, LocalDate.of(2026, 1, 10), 0L)
        )

        // WHEN : calcul des statistiques.
        val result = useCase(maintenances)

        // THEN : zéro est différent d'un coût absent.
        assertEquals(1, result.maintenanceCount)
        assertEquals(1, result.paidMaintenanceCount)
        assertEquals(0L, result.totalCostCents)
        assertEquals(0L, result.averageCostCents)
    }

    @Test
    fun `sorts monthly expenses chronologically`() {
        // GIVEN : des entretiens dans le désordre.
        val maintenances = listOf(
            createMaintenance(1L, LocalDate.of(2026, 3, 10), 10000L),
            createMaintenance(2L, LocalDate.of(2026, 1, 10), 20000L),
            createMaintenance(3L, LocalDate.of(2026, 2, 10), 30000L)
        )

        // WHEN : calcul des statistiques.
        val result = useCase(maintenances)

        // THEN : les mois sont triés chronologiquement.
        assertEquals(
            listOf("2026-01", "2026-02", "2026-03"),
            result.monthlyExpenses.map { it.month }
        )
    }

    @Test
    fun `sorts expenses by type descending`() {
        // GIVEN : deux catégories de dépenses différentes.
        val types = MaintenanceType.entries.toTypedArray()
        require(types.size >= 2) {
            "Ce test nécessite au moins deux types d'entretien"
        }

        val firstType = types[0]
        val secondType = types[1]

        val maintenances = listOf(
            createMaintenance(
                1L, LocalDate.of(2026, 1, 10),
                10000L, firstType
            ),
            createMaintenance(
                2L, LocalDate.of(2026, 1, 15),
                30000L, secondType
            )
        )

        // WHEN : calcul des statistiques.
        val result = useCase(maintenances)

        // THEN : la catégorie la plus coûteuse apparaît d'abord.
        assertEquals(2, result.expensesByType.size)
        assertEquals(secondType, result.expensesByType[0].type)
        assertEquals(30000L, result.expensesByType[0].totalCents)
        assertEquals(firstType, result.expensesByType[1].type)
        assertEquals(10000L, result.expensesByType[1].totalCents)
    }
}