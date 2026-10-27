package com.firas.carcareai.presentation.maintenance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.firas.carcareai.core.model.Maintenance
import com.firas.carcareai.core.model.MaintenanceType
import com.firas.carcareai.core.model.toFrenchLabel
import com.firas.carcareai.utils.formatDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditMaintenanceScreen(
    maintenanceId: Long,
    viewModel: MaintenanceViewModel,
    onMaintenanceUpdated: () -> Unit
) {
    val maintenance by viewModel.selectedMaintenance.collectAsState()

    LaunchedEffect(maintenanceId) {
        viewModel.loadMaintenance(maintenanceId)
    }

    val currentMaintenance = maintenance
    if (currentMaintenance == null) {
        Text("Chargement...")
        return
    }

    var type by remember(currentMaintenance.id) { mutableStateOf(currentMaintenance.type) }
    var mileage by remember(currentMaintenance.id) { mutableStateOf(currentMaintenance.mileage.toString()) }
    var cost by remember(currentMaintenance.id) { mutableStateOf(currentMaintenance.cost?.toString() ?: "") }
    var notes by remember(currentMaintenance.id) { mutableStateOf(currentMaintenance.notes ?: "") }
    var selectedDate by remember(currentMaintenance.id) { mutableStateOf(currentMaintenance.date) }
    var showDatePicker by remember { mutableStateOf(false) }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Modifier l'entretien")
            Text("Type d'entretien")

            Spacer(modifier = Modifier.height(6.dp))

            MaintenanceType.entries.forEach { maintenanceType ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = type == maintenanceType,
                        onClick = { type = maintenanceType }
                    )

                    Text(text = maintenanceType.toFrenchLabel())
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            Text("Date de l'entretien")

            Button(
                onClick = { showDatePicker = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(formatDate(selectedDate))
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = mileage,
                onValueChange = { mileage = it },
                label = { Text("Kilométrage") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = cost,
                onValueChange = { cost = it },
                label = { Text("Coût (€)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val mileageValue = mileage.toIntOrNull()

                    if (mileageValue != null) {

                        val updatedMaintenance = Maintenance(
                            id = currentMaintenance.id,
                            vehicleId = currentMaintenance.vehicleId,
                            type = type,
                            date = selectedDate,
                            mileage = mileageValue,
                            cost = cost
                                .replace(",", ".")
                                .toDoubleOrNull(),
                            notes = notes.ifBlank { null }
                        )

                        viewModel.updateMaintenance(
                            updatedMaintenance
                        )

                        onMaintenanceUpdated()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Enregistrer les modifications")
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDate)

        DatePickerDialog(
            onDismissRequest = {
                showDatePicker = false
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { date ->
                            selectedDate = date
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },

            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePicker = false
                    }
                ) {
                    Text("Annuler")
                }
            }
        ) {
            DatePicker(
                state = datePickerState
            )
        }
    }

}
