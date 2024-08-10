package org.liamjd.amber.screens

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.liamjd.amber.AmberApplication
import org.liamjd.amber.screens.vehicles.VehicleDetailsScreen
import org.liamjd.amber.viewModels.ChargeEventVMFactory
import org.liamjd.amber.viewModels.ChargeHistoryViewModelFactory
import org.liamjd.amber.viewModels.MainMenuViewModelFactory
import org.liamjd.amber.viewModels.TimerViewModelFactory
import org.liamjd.amber.viewModels.VehicleDetailsViewModelFactory

@Composable
fun Navigation(application: AmberApplication) {

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.StartScreen.route) {

        composable(route = Screen.StartScreen.route) {
            MainMenu(
                navController = navController,
                viewModel = viewModel(factory = MainMenuViewModelFactory(application)),
                timerViewModel = viewModel(factory = TimerViewModelFactory())
            )
        }
        composable(route = Screen.ChargeHistoryScreen.route) {
            ChargeHistoryScreen(
                navController = navController,
                viewModel(factory = ChargeHistoryViewModelFactory(application))
            )
        }
        composable(route = Screen.VehicleDetailsScreen.route) {
            VehicleDetailsScreen(
                navController = navController,
                viewModel(factory = VehicleDetailsViewModelFactory(application))
            )
        }
        composable(route = Screen.StartChargingScreen.route) {
            ChargingScreen(
                navController = navController,
                viewModel = viewModel(factory = ChargeEventVMFactory(application, null)),
                timerViewModel = viewModel(factory = TimerViewModelFactory())
            )
        }
        composable(route = Screen.StartChargingScreen.buildRoute("{eventId}")) { backStackEntry ->
            val activeChargeID = backStackEntry.arguments?.getString("eventId")
            ChargingScreen(
                navController = navController,
                viewModel = viewModel(factory = ChargeEventVMFactory(application, activeChargeID)),
                timerViewModel = viewModel(factory = TimerViewModelFactory())
            )
        }
    }
}