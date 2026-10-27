package com.firas.carcareai.presentation.vehicle

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import com.firas.carcareai.core.model.Vehicle

@Composable
fun VehicleScreen(
    viewModel: VehicleViewModel,
    onAddVehicle: () -> Unit,
    onVehicleClick: (Long) -> Unit
) {
    val vehicles by viewModel.vehicles.collectAsState()

    Scaffold(
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

            Text(
                "Mes véhicules",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (vehicles.isEmpty()) {
                Text(text = "Aucun véhicule enregistré")
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = vehicles,
                        key = { it.id }
                    ) { vehicle ->
                        VehicleItem(
                            vehicle,
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