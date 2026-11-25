package com.firas.carcareai.presentation.vehicle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.firas.carcareai.core.model.Vehicle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleScreen(
    viewModel: VehicleViewModel,
    onAddVehicle: () -> Unit,
    onVehicleClick: (Long) -> Unit,
    remindersCount: Int,
    onRemindersClick: () -> Unit
) {
    val vehicles by viewModel.vehicles.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Mes véhicules")
                },
                actions = {
                    RemindersAction(
                        remindersCount = remindersCount,
                        onClick = onRemindersClick
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddVehicle
            ) {
                Text("+")
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            if (vehicles.isEmpty()) {
                Text(text = "Aucun véhicule enregistré")
            } else {
                LazyColumn(
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = vehicles,
                        key = { it.id }
                    ) { vehicle ->
                        VehicleItem(
                            vehicle = vehicle,
                            onVehicleClick = onVehicleClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun VehicleItem(
    vehicle: Vehicle,
    onVehicleClick: (Long) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            onVehicleClick(vehicle.id)
        }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "${vehicle.brand} ${vehicle.model}",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = "Année : ${vehicle.year}"
            )

            Text(
                text = "Kilométrage : ${vehicle.mileage} km"
            )

            Text(
                text = "Carburant : ${vehicle.fuelType}"
            )

            Text(
                text = "Immatriculation : ${vehicle.licensePlate}"
            )
        }
    }
}

/**
 * Affiche le bouton d'accès au centre de rappels
 * avec le nombre d'échéances actives.
 */
@Composable
private fun RemindersAction(
    remindersCount: Int,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        BadgedBox(
            badge = {
                if (remindersCount > 0) {
                    Badge(
                        containerColor =
                            MaterialTheme.colorScheme.error,
                        contentColor =
                            MaterialTheme.colorScheme.onError
                    ) {
                        Text(
                            text = if (remindersCount > 99) {
                                "99+"
                            } else {
                                remindersCount.toString()
                            }
                        )
                    }
                }
            }
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription =
                    "Rappels d'entretien : $remindersCount"
            )
        }
    }
}