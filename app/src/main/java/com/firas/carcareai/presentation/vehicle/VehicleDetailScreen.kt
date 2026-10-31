package com.firas.carcareai.presentation.vehicle

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun VehicleDetailScreen(
    vehicleId: Long,
    viewModel: VehicleViewModel,
    onVehicleDeleted: () -> Unit,
    onEditVehicle: (Long) -> Unit,
    onMaintenancesClick: (Long) -> Unit,
    onMaintenanceIntervalsClick: (Long) -> Unit
) {
    val vehicle by viewModel.selectedVehicle.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(vehicleId) {
        viewModel.loadVehicle(vehicleId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        if (vehicle == null) {
            Text("Détails véhicule introuvable . . . ! ")
            // Text("Chargement... ")
        } else {
            val currentVehicle = vehicle!!

            Text("Détail du véhicule")

            Spacer(modifier = Modifier.height(24.dp))

            Text("Marque : ${currentVehicle.brand}")
            Text("Modèle : ${currentVehicle.model}")
            Text("Année : ${currentVehicle.year}")
            Text("Kilométrage : ${currentVehicle.mileage} km")
            Text("Carburant : ${currentVehicle.fuelType}")
            Text("Immatriculation : ${currentVehicle.licensePlate ?: "Non renseignée"}")

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    onMaintenancesClick(currentVehicle.id)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Voir les entretiens")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    onMaintenanceIntervalsClick(currentVehicle.id)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Configurer les intervalles d'entretien")
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    onEditVehicle(currentVehicle.id)
                }
            ) {
                Text("Modifier le véhicule")
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    showDeleteDialog = true
                }
            ) {
                Text("Supprimer le véhicule")
            }
            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = {
                        showDeleteDialog = false
                    },

                    title = { Text("Supprimer le véhicule ?") },

                    text = {
                        Text(
                            "Voulez-vous vraiment supprimer " +
                                    "${currentVehicle.brand} ${currentVehicle.model} ? " +
                                    "Cette action est irréversible."
                        )
                    },

                    confirmButton = {
                        TextButton(
                            onClick = {
                                showDeleteDialog = false
                                viewModel.deleteVehicle(currentVehicle)
                                onVehicleDeleted()
                            }
                        ) {
                            Text("Supprimer")
                        }
                    },

                    dismissButton = {
                        TextButton(
                            onClick = { showDeleteDialog = false }
                        ) {
                            Text("Annuler")
                        }
                    }
                )
            }

        }
    }
}