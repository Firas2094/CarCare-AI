package com.firas.carcareai

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModelProvider
import com.firas.carcareai.di.AppContainer
import com.firas.carcareai.presentation.maintenance.MaintenanceViewModel
import com.firas.carcareai.presentation.maintenance.MaintenanceViewModelFactory
import com.firas.carcareai.presentation.maintenanceinterval.MaintenanceIntervalViewModel
import com.firas.carcareai.presentation.maintenanceinterval.MaintenanceIntervalViewModelFactory
import com.firas.carcareai.presentation.navigation.CarCareNavigation
import com.firas.carcareai.presentation.vehicle.VehicleViewModel
import com.firas.carcareai.presentation.vehicle.VehicleViewModelFactory
import com.firas.carcareai.worker.MaintenanceReminderScheduler


class MainActivity : ComponentActivity() {

    private lateinit var appContainer: AppContainer
    private lateinit var vehicleViewModel: VehicleViewModel
    private lateinit var maintenanceViewModel: MaintenanceViewModel
    private lateinit var maintenanceIntervalViewModel: MaintenanceIntervalViewModel

    private val notificationVehicleId = mutableStateOf<Long?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handleNotificationIntent(intent)
        appContainer = AppContainer(applicationContext)

        val vehicleViewModelfactory = VehicleViewModelFactory(
            getVehiclesUseCase = appContainer.getVehiclesUseCase,
            addVehicleUseCase = appContainer.addVehicleUseCase,
            getVehicleByIdUseCase = appContainer.getVehicleByIdUseCase,
            deleteVehicleUseCase = appContainer.deleteVehicleUseCase,
            updateVehicleUseCase = appContainer.updateVehicleUseCase
        )

        val maintenanceViewModelFactory = MaintenanceViewModelFactory(
            appContainer.getMaintenancesByVehicleUseCase,
            appContainer.getMaintenanceByIdUseCase,
            appContainer.addMaintenanceUseCase,
            appContainer.updateMaintenanceUseCase,
            appContainer.deleteMaintenanceUseCase,
            appContainer.calculateMaintenanceScheduleUseCase,
            appContainer.getVehicleByIdUseCase,
            appContainer.getMaintenanceIntervalsUseCase
        )

        val maintenanceIntervalViewModelFactory = MaintenanceIntervalViewModelFactory(
                getIntervalsUseCase = appContainer.getMaintenanceIntervalsUseCase,
                saveIntervalUseCase = appContainer.saveMaintenanceIntervalUseCase,
                deleteIntervalUseCase = appContainer.deleteMaintenanceIntervalUseCase
            )





        vehicleViewModel = ViewModelProvider(
            this,
            vehicleViewModelfactory
        )[VehicleViewModel::class.java]

        maintenanceViewModel = ViewModelProvider(
            this,
            maintenanceViewModelFactory
        )[MaintenanceViewModel::class.java]

        maintenanceIntervalViewModel = ViewModelProvider(
            this,
            maintenanceIntervalViewModelFactory
        )[MaintenanceIntervalViewModel::class.java]



        MaintenanceReminderScheduler.schedule(applicationContext)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }

        setContent {
            CarCareNavigation(
                vehicleViewModel = vehicleViewModel,
                maintenanceViewModel = maintenanceViewModel,
                notificationVehicleId = notificationVehicleId.value,
                maintenanceIntervalViewModel = maintenanceIntervalViewModel,
            )
        }
    }

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            // L'autorisation a été acceptée ou refusée.
        }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent?) {
        notificationVehicleId.value =
            intent?.getLongExtra("vehicleId", -1L)
                ?.takeIf { it > 0L }
    }
}
