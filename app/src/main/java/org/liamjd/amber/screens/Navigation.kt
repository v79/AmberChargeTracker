package org.liamjd.amber.screens

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Screen.StartScreen.route ) {
        composable(route = Screen.StartScreen.route) {
            MainMenu(navController = navController)
        }
    }
}