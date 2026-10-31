package com.firas.carcareai.presentation.maintenanceinterval

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import com.firas.carcareai.core.model.MaintenanceType

@Composable
fun MaintenanceIntervalScreen(
    vehicleId: Long,
    viewModel: MaintenanceIntervalViewModel,
    onBack: () -> Unit
) {
    val intervals by viewModel.intervals.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(vehicleId) { viewModel.loadIntervals(vehicleId) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Intervalles d'entretien",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(onClick = onBack) { Text("Retour") }

        error?.let { message ->
            Text(
                text = message,
                color = MaterialTheme.colorScheme.error
            )

            TextButton(onClick = viewModel::clearError) {
                Text("Fermer")
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(MaintenanceType.entries.toList()) { type ->

                val currentInterval = intervals.find {
                    it.type == type
                }

                MaintenanceIntervalEditor(
                    vehicleId = vehicleId,
                    type = type,
                    intervalKm = currentInterval?.intervalKm,
                    intervalMonths = currentInterval?.intervalMonths,
                    onSave = { km, months ->
                        viewModel.saveInterval(
                            vehicleId = vehicleId,
                            type = type,
                            intervalKm = km,
                            intervalMonths = months
                        )
                    },
                    onReset = {
                        viewModel.resetInterval(vehicleId, type)
                    }
                )
            }
        }
    }
}

@Composable
private fun MaintenanceIntervalEditor(
    vehicleId: Long,
    type: MaintenanceType,
    intervalKm: Int?,
    intervalMonths: Int?,
    onSave: (Int?, Int?) -> Unit,
    onReset: () -> Unit
){
    var kmText by remember(vehicleId, type) {
        mutableStateOf(intervalKm?.toString().orEmpty())
    }

    var monthsText by remember(vehicleId, type) {
        mutableStateOf(intervalMonths?.toString().orEmpty())
    }

    // Synchronise les champs lorsque Room fournit les valeurs enregistrées.
    LaunchedEffect(intervalKm, intervalMonths) {
        kmText = intervalKm?.toString().orEmpty()
        monthsText = intervalMonths?.toString().orEmpty()
    }

    val km = kmText.toIntOrNull()
    val months = monthsText.toIntOrNull()

    val validKm = kmText.isBlank() || (km != null && km > 0)
    val validMonths = monthsText.isBlank() || (months != null && months > 0)

    val canSave = validKm && validMonths && (km != null || months != null)

    Card(
        modifier = Modifier.fillMaxWidth()
    ){
        Column(
            modifier = Modifier.padding(16.dp)
        ){
            Text(
                text = type.name.replace("_", " "),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = kmText,
                onValueChange = { kmText = it },
                label = { Text("Intervalle en kilomètres") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = !validKm
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = monthsText,
                onValueChange = { monthsText = it },
                label = { Text("Intervalle en mois") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = !validMonths
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ){
                OutlinedButton(
                    onClick = onReset,
                    enabled = intervalKm != null || intervalMonths != null
                ) {
                    Text("Réinitialiser")
                }

                Button(
                    onClick = {
                        if (canSave) {
                            onSave(km, months)
                        }
                    },
                    enabled = canSave
                ) {
                    Text("Enregistrer")
                }
            }
        }
    }
}