package com.firas.carcareai.domain.usecase.maintenance

import com.firas.carcareai.core.model.ExpenseByType
import com.firas.carcareai.core.model.Maintenance
import com.firas.carcareai.core.model.MaintenanceStatistics
import com.firas.carcareai.core.model.MonthlyExpense
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId

/**
 * Calcule les statistiques des entretiens d'un véhicule.
 *
 */
class GetMaintenanceStatisticsUseCase {

    operator fun invoke(
        maintenances: List<Maintenance>
    ): MaintenanceStatistics {

        // Exclure les maintenances sans coût
        val expenses = maintenances.filter {
            it.costCents != null
        }

        // Somme des dépenses en centimes.
        val totalCostCents = expenses.fold(0L) { total, maintenance ->
            Math.addExact(total, requireNotNull(maintenance.costCents))
        }

        // Coût moyen des entretiens avec un coût renseigné.
        val averageCostCents = if (expenses.isEmpty()) {
            0L
        } else {
            totalCostCents / expenses.size
        }

        // Regroupement des dépenses par mois.
        val monthlyExpenses = expenses
            .groupBy { maintenance ->
                val date = Instant.ofEpochMilli(maintenance.date)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()

                YearMonth.from(date).toString()
            }
            .map { (month, items) ->
                MonthlyExpense(
                    month = month,
                    totalCents = items.fold(0L) { total, item ->
                        Math.addExact(total, requireNotNull(item.costCents))
                    }
                )
            }
            .sortedBy { it.month }

        // Regroupement des dépenses par catégorie.
        val expensesByType = expenses
            .groupBy { it.type }
            .map { (type, items) ->
                ExpenseByType(
                    type = type,
                    totalCents = items.fold(0L) { total, item ->
                        Math.addExact(total, requireNotNull(item.costCents))
                    }
                )
            }
            .sortedByDescending { it.totalCents }

        return MaintenanceStatistics(
            maintenanceCount = maintenances.size,
            paidMaintenanceCount = expenses.size,
            totalCostCents = totalCostCents,
            averageCostCents = averageCostCents,
            monthlyExpenses = monthlyExpenses,
            expensesByType = expensesByType
        )
    }
}