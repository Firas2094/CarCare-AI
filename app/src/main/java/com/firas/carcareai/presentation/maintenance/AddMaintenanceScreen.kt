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
fun AddMaintenanceScreen(
    vehicleId: Long,
    viewModel: MaintenanceViewModel,
    onMaintenanceAdded: () -> Unit
) {
    var type by remember { mutableStateOf(MaintenanceType.OIL_CHANGE) }
    var mileage by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Ajouter un entretien")

        Text("Type d'entretien")

        MaintenanceType.entries.forEach { maintenanceType ->

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = type == maintenanceType,
                    onClick = {
                        type = maintenanceType
                    }
                )

                Text(
                    text = maintenanceType.toFrenchLabel()
                )
            }
        }

        Text(text = "Date de l'entretien")
        Button(
            onClick = { showDatePicker = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = formatDate(selectedDate))
        }

        OutlinedTextField(
            value = mileage,
            onValueChange = { mileage = it },
            label = {
                Text("Kilométrage")
            },
            modifier = Modifier.fillMaxWidth()
        )


        OutlinedTextField(
            value = cost,
            onValueChange = { cost = it },
            label = {
                Text("Coût (€)")
            },
            modifier = Modifier.fillMaxWidth()
        )


        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = {
                Text("Notes")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                val mileageValue = mileage.toIntOrNull()
                if (mileageValue != null) {

                    val maintenance = Maintenance(
                        vehicleId = vehicleId,
                        type = type,
                        date = selectedDate,
                        mileage = mileageValue,
                        cost = cost
                            .replace(",", ".")
                            .toDoubleOrNull(),
                        notes = notes.ifBlank { null }
                    )

                    viewModel.addMaintenance(maintenance)
                    onMaintenanceAdded()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enregistrer")
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = selectedDate
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
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
            DatePicker(state = datePickerState)
        }
    }
}
