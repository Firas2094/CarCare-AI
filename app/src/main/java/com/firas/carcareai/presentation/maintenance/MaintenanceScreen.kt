package com.firas.carcareai.presentation.maintenance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.firas.carcareai.core.model.Maintenance
import com.firas.carcareai.core.model.toFrenchLabel
import com.firas.carcareai.utils.formatDate
import java.util.Locale
import androidx.compose.foundation.lazy.items
import com.firas.carcareai.core.model.MaintenanceSchedule
import com.firas.carcareai.core.model.MaintenanceStatus

@Composable
fun MaintenanceScreen(
    vehicleId: Long,
    viewModel: MaintenanceViewModel,
    onAddMaintenance: (Long) -> Unit,
    onMaintenanceClick: (Long) -> Unit
) {
    val maintenances by viewModel.maintenances.collectAsState()
    val schedules by viewModel.maintenanceSchedules.collectAsState()

    LaunchedEffect(vehicleId) {
        viewModel.loadMaintenances(vehicleId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text(text = "Entretiens du véhicule")

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            item { Text("Échéances") }

            items(
                items = schedules,
                key = { schedule -> schedule.type.name }
            ) { schedule ->

                MaintenanceScheduleItem( schedule = schedule )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Historique")
            }

            items(
                items = maintenances,
                key = { maintenance -> maintenance.id }
            ) { maintenance ->

                MaintenanceItem(
                    maintenance = maintenance,
                    onClick = {
                        onMaintenanceClick(
                            maintenance.id
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onAddMaintenance(vehicleId) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ajouter un entretien")
        }
    }
}

@Composable
fun MaintenanceItem(
    maintenance: Maintenance,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            Text(
                text = maintenance.type.toFrenchLabel()
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Text(
                    text = "${maintenance.mileage} km"
                )

                maintenance.cost?.let { cost ->
                    Text(
                        text = String.format(
                            Locale.FRANCE,
                            "%.2f €",
                            cost
                        )
                    )
                }
            }

            Text(
                text = formatDate(maintenance.date)
            )
        }
    }
}

@Composable
fun MaintenanceScheduleItem(
    schedule: MaintenanceSchedule
) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {

            Text(text = schedule.type.toFrenchLabel())

            Text(
                text = when (schedule.status) {
                    MaintenanceStatus.OK -> "🟢 OK"
                    MaintenanceStatus.SOON -> "🟠 Bientôt"
                    MaintenanceStatus.OVERDUE -> "🔴 En retard"
                    MaintenanceStatus.UNKNOWN -> "⚪ Historique inconnu"
                }
            )

            schedule.nextMaintenanceMileage?.let { mileage ->
                Text(text = "Prochaine échéance : $mileage km")
            }

            schedule.remainingKm?.let { remainingKm ->
                when {
                    remainingKm < 0 -> {
                        Text(text = "Dépassée d'environ ${-remainingKm} km")
                    }

                    remainingKm == 0 -> {
                        Text(text = "Échéance atteinte")
                    }

                    else -> {
                        Text(text = "Dans environ $remainingKm km")
                    }
                }
            }

            schedule.nextMaintenanceDate?.let { nextDate ->
                Text(text = "Prochaine échéance : ${formatDate(nextDate)}")
            }

            schedule.remainingDays?.let { remainingDays ->
                when {
                    remainingDays < 0 -> { Text(text = "Dépassée de ${-remainingDays} jours")
                    }

                    remainingDays == 0L -> { Text(text = "Échéance aujourd'hui")
                    }

                    else -> { Text(text = "Dans $remainingDays jours")
                    }
                }
            }
        }
    }
}