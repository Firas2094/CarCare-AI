package com.firas.carcareai.core.model

/**
 * Dépenses regroupées par mois.
 * Month est au format YYYY-MM.
 */
data class MonthlyExpense(
    val month: String,
    val totalCents: Long
)

/**
 * Dépenses regroupées par type d'entretien.
 */
data class ExpenseByType(
    val type: MaintenanceType,
    val totalCents: Long
)

/**
 * Statistiques d'entretien pour un véhicule.
 *
 * Les montants sont conservés en centimes
 * afin de garantir des calculs monétaires exacts.
 */
data class MaintenanceStatistics(
    val maintenanceCount: Int,
    val paidMaintenanceCount: Int,
    val totalCostCents: Long,
    val averageCostCents: Long,
    val monthlyExpenses: List<MonthlyExpense>,
    val expensesByType: List<ExpenseByType>
)