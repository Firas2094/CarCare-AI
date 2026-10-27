package com.firas.carcareai.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.firas.carcareai.presentation.maintenance.AddMaintenanceScreen
import com.firas.carcareai.presentation.maintenance.EditMaintenanceScreen
import com.firas.carcareai.presentation.maintenance.MaintenanceDetailScreen
import com.firas.carcareai.presentation.maintenance.MaintenanceScreen
import com.firas.carcareai.presentation.maintenance.MaintenanceViewModel
import com.firas.carcareai.presentation.vehicle.AddVehicleScreen
import com.firas.carcareai.presentation.vehicle.VehicleDetailScreen
import com.firas.carcareai.presentation.vehicle.VehicleScreen
import com.firas.carcareai.presentation.vehicle.VehicleViewModel
import com.firas.carcareai.presentation.vehicle.EditVehicleScreen

@Composable
fun CarCareNavigation(
    vehicleViewModel: VehicleViewModel,
    maintenanceViewModel: MaintenanceViewModel,
    notificationVehicleId: Long? = null
) {
    val navController = rememberNavController()

    LaunchedEffect(notificationVehicleId) {
        notificationVehicleId?.let { vehicleId ->
            navController.navigate("maintenances/$vehicleId") {
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "vehicles"
    ) {
        composable("vehicles") {
            VehicleScreen(
                viewModel = vehicleViewModel,
                onAddVehicle = {
                    navController.navigate("add_vehicle")
                },
                onVehicleClick = { vehicleId ->
                    navController.navigate("vehicle_detail/$vehicleId")
                }
            )
        }

        composable("add_vehicle") {
            AddVehicleScreen(
                viewModel = vehicleViewModel,
                onVehicleAdded = { navController.popBackStack() }
            )
        }

        composable(
            route = "vehicle_detail/{vehicleId}",
            arguments = listOf(
                navArgument("vehicleId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->

            val vehicleId = backStackEntry.arguments
                ?.getLong("vehicleId")
                ?: return@composable

            VehicleDetailScreen(
                vehicleId = vehicleId,
                viewModel = vehicleViewModel,
                onVehicleDeleted = { navController.popBackStack() },
                onEditVehicle = {id -> navController.navigate("edit_vehicle/$id")},
                onMaintenancesClick = { id -> navController.navigate("maintenances/$id")}
            )
        }

        composable(
            route = "edit_vehicle/{vehicleId}",
            arguments = listOf(
                navArgument("vehicleId") {
                    type = NavType.LongType
                }
            )
        ){ backStackEntry ->

            val vehicleId = backStackEntry.arguments
                ?.getLong("vehicleId")
                ?: return@composable

            EditVehicleScreen(
                vehicleId = vehicleId,
                viewModel = vehicleViewModel,
                onVehicleUpdated = { navController.popBackStack() }
            )
        }

        composable(
            route = "maintenances/{vehicleId}",
            arguments = listOf(
                navArgument("vehicleId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->

            val vehicleId =
                backStackEntry.arguments?.getLong("vehicleId")
                    ?: return@composable

            MaintenanceScreen(
                vehicleId = vehicleId,
                viewModel = maintenanceViewModel,
                onAddMaintenance = { id ->
                    navController.navigate("add_maintenance/$id")
                },
                onMaintenanceClick = { maintenanceId ->
                    navController.navigate(
                        "maintenance_detail/$maintenanceId"
                    )
                }
            )
        }


        composable(
            route = "add_maintenance/{vehicleId}",
            arguments = listOf(
                navArgument("vehicleId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->

            val vehicleId =
                backStackEntry.arguments?.getLong("vehicleId")
                    ?: return@composable

            AddMaintenanceScreen(
                vehicleId = vehicleId,
                viewModel = maintenanceViewModel,
                onMaintenanceAdded = {
                    navController.popBackStack()
                }
            )
        }



        composable(
            route = "maintenance_detail/{maintenanceId}",
            arguments = listOf(
                navArgument("maintenanceId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->

            val maintenanceId =
                backStackEntry.arguments?.getLong("maintenanceId")
                    ?: return@composable

            MaintenanceDetailScreen(
                maintenanceId = maintenanceId,
                viewModel = maintenanceViewModel,
                onEditMaintenance = { id ->
                    navController.navigate(
                        "edit_maintenance/$id")
                },

                onMaintenanceDeleted = {
                    navController.popBackStack()
                }
            )
        }



        composable(
            route = "edit_maintenance/{maintenanceId}",
            arguments = listOf(
                navArgument("maintenanceId") {
                    type = NavType.LongType
                }
            )
        ) { backStackEntry ->

            val maintenanceId =
                backStackEntry.arguments?.getLong("maintenanceId")
                    ?: return@composable

            EditMaintenanceScreen(
                maintenanceId = maintenanceId,
                viewModel = maintenanceViewModel,
                onMaintenanceUpdated = {
                    navController.popBackStack()
                }
            )
        }
    }
}