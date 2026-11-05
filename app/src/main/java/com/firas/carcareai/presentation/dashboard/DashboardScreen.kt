package com.firas.carcareai.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.firas.carcareai.core.model.ExpenseByType
import com.firas.carcareai.core.model.MaintenanceStatistics
import com.firas.carcareai.core.model.MonthlyExpense
import com.firas.carcareai.utils.maintenanceTypeLabel
import java.text.NumberFormat
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    vehicleId: Long,
    viewModel: DashboardViewModel,
    onBack: () -> Unit
) {
    LaunchedEffect(vehicleId) {
        viewModel.loadStatistics(vehicleId)
    }

    val statistics by viewModel.statistics.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistiques") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        val currentStatistics = statistics

        if (currentStatistics == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            DashboardContent(
                statistics = currentStatistics,
                modifier = Modifier.padding(paddingValues)
            )
        }
    }
}

@Composable
private fun DashboardContent(
    statistics: MaintenanceStatistics,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Text(
                "Vue d'ensemble",
                style = MaterialTheme.typography.titleLarge
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatisticCard(
                    "Dépenses totales",
                    formatCurrency(statistics.totalCostCents),
                    Modifier.weight(1f)
                )
                StatisticCard(
                    "Entretiens",
                    statistics.maintenanceCount.toString(),
                    Modifier.weight(1f)
                )
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatisticCard(
                    "Coût moyen",
                    formatCurrency(statistics.averageCostCents),
                    Modifier.weight(1f)
                )
                StatisticCard(
                    "Avec coût",
                    statistics.paidMaintenanceCount.toString(),
                    Modifier.weight(1f)
                )
            }
        }

        item {
            DashboardSection("Dépenses mensuelles") {
                if (statistics.monthlyExpenses.isEmpty()) {
                    Text("Aucune dépense enregistrée.")
                } else {
                    MonthlyExpensesChart(statistics.monthlyExpenses)
                }
            }
        }

        item {
            DashboardSection("Répartition par catégorie") {
                if (statistics.expensesByType.isEmpty()) {
                    Text("Aucune dépense par catégorie.")
                } else {
                    val maxCost = statistics.expensesByType
                        .maxOf { it.totalCents }

                    Column(
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        statistics.expensesByType.forEach { expense ->
                            ExpenseByTypeItem(
                                expense = expense,
                                maxCost = maxCost
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatisticCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier.heightIn(min = 110.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun DashboardSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    ElevatedCard(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                title,
                style = MaterialTheme.typography.titleMedium
            )

            content()
        }
    }
}

@Composable
private fun MonthlyExpensesChart(
    expenses: List<MonthlyExpense>
) {
    val maxCost = expenses.maxOfOrNull { it.totalCents } ?: 0L
    val primaryColor = MaterialTheme.colorScheme.primary
    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    // Affichage horizontal pour rester lisible
    Column(
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        expenses.forEach { expense ->

            val fraction = if (maxCost > 0L) {
                (expense.totalCents.toDouble() / maxCost.toDouble())
                    .toFloat()
                    .coerceIn(0f, 1f)
            } else {
                0f
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatMonth(expense.month),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = formatCurrency(expense.totalCents),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(50))
                    .background(trackColor)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fraction)
                        .fillMaxHeight()
                        .background(primaryColor)
                )
            }
        }
    }
}

@Composable
private fun ExpenseByTypeItem(
    expense: ExpenseByType,
    maxCost: Long
) {
    val fraction = if (maxCost > 0L) {
        (expense.totalCents.toDouble() / maxCost.toDouble())
            .toFloat()
            .coerceIn(0f, 1f)
    } else {
        0f
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = maintenanceTypeLabel(expense.type.name),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.width(8.dp))

            Text(
                text = formatCurrency(expense.totalCents),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(50))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}


private fun formatMonth(month: String): String {
    return YearMonth.parse(month)
        .format(
            DateTimeFormatter.ofPattern(
                "MMM yyyy",
                Locale.FRENCH
            )
        )
}

private fun formatCurrency(cents: Long): String {
    return NumberFormat
        .getCurrencyInstance(Locale.FRANCE)
        .format(cents / 100.0)
}
