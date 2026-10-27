package com.firas.carcareai.presentation.vehicle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
fun AddVehicleScreen(
    viewModel: VehicleViewModel,
    onVehicleAdded: () -> Unit
) {
    var brand by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }
    var mileage by remember { mutableStateOf("") }
    var fuelType by remember { mutableStateOf(FuelType.GASOLINE) }
    var licensePlate by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = "Ajouter un véhicule")

        Spacer(modifier = Modifier.height(24.dp))

        CarCareTextField(
            value = brand,
            onValueChange = { brand = it },
            label = "Marque",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        CarCareTextField(
            value = model,
            onValueChange = { model = it },
            label = "Modèle",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        CarCareTextField(
            value = year,
            onValueChange = { year = it },
            label = "Année",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        CarCareTextField(
            value = mileage,
            onValueChange = { mileage = it },
            label = "Kilométrage",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(text = "Carburant")

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

        Spacer(modifier = Modifier.height(12.dp))

        CarCareTextField(
            value = licensePlate,
            onValueChange = { licensePlate = it },
            label = "Immatriculation",
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val vehicle = Vehicle(
                    brand = brand,
                    model = model,
                    year = year.toIntOrNull() ?: 0,
                    mileage = mileage.toIntOrNull() ?: 0,
                    fuelType = fuelType,
                    licensePlate = licensePlate,
                )
                viewModel.addVehicle(vehicle)

                onVehicleAdded()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ajouter un véhicule")
        }
    }
}
