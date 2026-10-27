package com.firas.carcareai.presentation.vehicle

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
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
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
import com.firas.carcareai.core.model.FuelType
import com.firas.carcareai.core.model.Vehicle

@Composable
fun EditVehicleScreen(
    vehicleId: Long,
    viewModel: VehicleViewModel,
    onVehicleUpdated: () -> Unit
) {
    val vehicle by viewModel.selectedVehicle.collectAsState()

    LaunchedEffect(vehicleId) { viewModel.loadVehicle(vehicleId) }

    val currentVehicle = vehicle

    if (currentVehicle == null) {
       // Text("le véhicule n'est pas introuvable...")
        Text("chargement ...")
        return
    }

    var brand by remember(currentVehicle.id) { mutableStateOf(currentVehicle.brand ) }
    var model by remember(currentVehicle.id) { mutableStateOf(currentVehicle.model ) }
    var year by remember(currentVehicle.id) { mutableStateOf(currentVehicle.year.toString()) }
    var mileage by remember(currentVehicle.id) { mutableStateOf(currentVehicle.mileage.toString()) }
    var fuelType by remember(currentVehicle.id) { mutableStateOf(currentVehicle.fuelType) }
    var licensePlate by remember(currentVehicle.id) { mutableStateOf(currentVehicle.licensePlate ?: "") }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("Modifier le véhicule")

        Spacer(modifier = Modifier.height(10.dp))

        CarCareTextField(
            value = brand,
            onValueChange = { brand = it },
            label = "Marque",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        CarCareTextField(
            value = model,
            onValueChange = { model = it },
            label = "Modèle",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        CarCareTextField(
            value = year,
            onValueChange = { year = it },
            label = "Année",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        CarCareTextField(
            value = mileage,
            onValueChange = { mileage = it },
            label = "Kilométrage",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text("Carburant")

        FuelType.entries.forEach { type ->

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                RadioButton(
                    selected = fuelType == type,
                    onClick = {
                        fuelType = type
                    }
                )

                Text(
                    text = when (type) {
                        FuelType.GASOLINE -> "Essence"
                        FuelType.DIESEL -> "Diesel"
                        FuelType.HYBRID -> "Hybride"
                        FuelType.ELECTRIC -> "Électrique"
                        FuelType.LPG -> "GPL"
                        FuelType.OTHER -> "Autre"
                    }
                )
            }
        }

        CarCareTextField(
            value = licensePlate,
            onValueChange = { licensePlate = it },
            label = "Immatriculation",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val updatedVehicle = Vehicle(
                    id = currentVehicle.id, // ⚠️ très important
                    brand = brand,
                    model = model,
                    year = year.toIntOrNull() ?: currentVehicle.year,
                    mileage = mileage.toIntOrNull() ?: currentVehicle.mileage,
                    fuelType = fuelType,
                    licensePlate = licensePlate.ifBlank { null }
                )

                viewModel.updateVehicle(updatedVehicle)

                onVehicleUpdated()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Enregistrer les modifications")
        }
    }

}