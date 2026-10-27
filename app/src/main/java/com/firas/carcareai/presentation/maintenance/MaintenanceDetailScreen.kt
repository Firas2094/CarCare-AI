package com.firas.carcareai.presentation.maintenance

import androidx.compose.foundation.layout.Arrangement
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
import com.firas.carcareai.core.model.toFrenchLabel
import com.firas.carcareai.utils.formatDate
import java.util.Locale

@Composable
fun MaintenanceDetailScreen(
    maintenanceId: Long,
    viewModel: MaintenanceViewModel,
    onEditMaintenance: (Long) -> Unit,
    onMaintenanceDeleted: () -> Unit
) {
    val maintenance by viewModel.selectedMaintenance.collectAsState()

    LaunchedEffect(maintenanceId) {
        viewModel.loadMaintenance(maintenanceId)
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

    val currentMaintenance = maintenance
    if (currentMaintenance == null) {
        Text("Chargement...")
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = currentMaintenance.type.toFrenchLabel())
        Text(text = "Date : ${formatDate(currentMaintenance.date)}")
        Text(text = "Kilométrage : ${currentMaintenance.mileage} km")
        Text(text = "Coût : ${
                currentMaintenance.cost?.let {
                    String.format(Locale.FRANCE, "%.2f €", it)
                } ?: "Non renseigné"
            }")
        Text(text = "Notes : ${
                currentMaintenance.notes ?: "Aucune note"
            }"
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                onEditMaintenance(currentMaintenance.id)
            },
            modifier = Modifier.fillMaxWidth()
        ){
            Text("Modifier")
        }

        Button(
            onClick = { showDeleteDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Supprimer")
        }


        if (showDeleteDialog) {

            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Supprimer l'entretien ?") },
                text = { Text("Voulez-vous vraiment supprimer " +
                                "« ${currentMaintenance.type.toFrenchLabel()} » ? " +
                                "Cette action est irréversible.") },
                confirmButton = { TextButton(
                        onClick = {
                            showDeleteDialog = false
                            viewModel.deleteMaintenance(currentMaintenance)
                            onMaintenanceDeleted()
                        }
                    ) {
                        Text("Supprimer")
                    }
                },

                dismissButton = { TextButton(
                        onClick = {
                            showDeleteDialog = false
                        }
                    ) {
                        Text("Annuler")
                    }
                }
            )
        }
    }
}