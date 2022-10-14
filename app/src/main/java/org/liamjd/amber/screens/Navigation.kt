package org.liamjd.amber.screens

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.liamjd.amber.AmberApplication
import org.liamjd.amber.viewModels.ChargeEventVMFactory

@Composable
fun Navigation(application: AmberApplication) {

    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.StartScreen.route) {

        composable(route = Screen.StartScreen.route) {
            MainMenu(navController = navController)
        }
        composable(route = Screen.RecordJourneyScreen.route) {
            RecordChargeScreen(
                navController = navController,
                viewModel(factory = ChargeEventVMFactory(application))
            )
        }
    }
}