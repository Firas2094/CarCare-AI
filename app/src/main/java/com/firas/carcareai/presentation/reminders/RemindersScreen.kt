package com.firas.carcareai.presentation.reminders

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.firas.carcareai.core.model.MaintenanceReminder
import com.firas.carcareai.core.model.MaintenanceStatus
import com.firas.carcareai.core.model.toFrenchLabel
import com.firas.carcareai.ui.theme.MaintenanceOverdueColor
import com.firas.carcareai.ui.theme.MaintenanceSoonColor
import com.firas.carcareai.utils.formatRemainingDays
import com.firas.carcareai.utils.formatRemainingKm


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RemindersScreen(
    viewModel: RemindersViewModel,
    onReminderClick: (Long) -> Unit,
    onBack: () -> Unit
) {
    val reminders by viewModel.reminders.collectAsState()

    val overdueReminders = reminders.filter {
        it.schedule.status == MaintenanceStatus.OVERDUE
    }
    val soonReminders = reminders.filter {
        it.schedule.status == MaintenanceStatus.SOON
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Rappels d'entretien")
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector =
                                Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Retour"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {
                    ReminderSummaryCard(
                        title = "En retard",
                        count = overdueReminders.size,
                        color = MaintenanceOverdueColor,
                        modifier = Modifier.weight(1f)
                    )

                    ReminderSummaryCard(
                        title = "À venir",
                        count = soonReminders.size,
                        color = MaintenanceSoonColor,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            if (reminders.isEmpty()) {
                item {
                    EmptyRemindersContent()
                }
            }

            if (overdueReminders.isNotEmpty()) {

                item {
                    ReminderSectionTitle(
                        title = "En retard",
                        count = overdueReminders.size,
                        color = MaintenanceOverdueColor
                    )
                }

                items(
                    items = overdueReminders,
                    key = {
                        "${it.vehicle.id}_${it.schedule.type.name}"
                    }
                ) { reminder ->

                    ReminderCard(
                        reminder = reminder,
                        color = MaintenanceOverdueColor,
                        onClick = {
                            onReminderClick(reminder.vehicle.id)
                        }
                    )
                }
            }

            if (soonReminders.isNotEmpty()) {

                item {
                    ReminderSectionTitle(
                        title = "À venir",
                        count = soonReminders.size,
                        color = MaintenanceSoonColor
                    )
                }

                items(
                    items = soonReminders,
                    key = {
                        "${it.vehicle.id}_${it.schedule.type.name}"
                    }
                ) { reminder ->

                    ReminderCard(
                        reminder = reminder,
                        color = MaintenanceSoonColor,
                        onClick = {
                            onReminderClick(reminder.vehicle.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ReminderSummaryCard(
    title: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.12f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                tint = color
            )

            Text(
                text = count.toString(),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )

            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = color
            )
        }
    }
}

@Composable
private fun ReminderSectionTitle(
    title: String,
    count: Int,
    color: Color
) {
    Text(
        text = "$title ($count)",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = color,
        modifier = Modifier.padding(top = 8.dp)
    )
}


@Composable
private fun ReminderCard(
    reminder: MaintenanceReminder,
    color: Color,
    onClick: () -> Unit
) {
    val vehicle = reminder.vehicle
    val schedule = reminder.schedule

    OutlinedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Build,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(28.dp)
            )

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = schedule.type.toFrenchLabel(),
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "${vehicle.brand} ${vehicle.model}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                schedule.remainingKm?.let { remainingKm ->
                    Text(
                        text = formatRemainingKm(remainingKm),
                        style = MaterialTheme.typography.bodySmall,
                        color = when {
                            remainingKm < 0 -> MaintenanceOverdueColor
                            remainingKm <= 2_000 -> MaintenanceSoonColor
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }

                schedule.remainingDays?.let { remainingDays ->
                    Text(
                        text = formatRemainingDays(remainingDays),
                        style = MaterialTheme.typography.bodySmall,
                        color = when {
                            remainingDays < 0 -> MaintenanceOverdueColor
                            remainingDays <= 60 -> MaintenanceSoonColor
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }

            Icon(
                imageVector =
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EmptyRemindersContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 48.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Aucun rappel d'entretien",
            style = MaterialTheme.typography.titleLarge
        )

        Text(
            text = "Aucune échéance en retard ou à venir " +
                    "n'a été détectée pour vos véhicules.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
