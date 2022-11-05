package org.liamjd.amber.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.liamjd.amber.R
import org.liamjd.amber.getConfigLong
import org.liamjd.amber.ui.theme.AmberChargeTrackerTheme
import org.liamjd.amber.viewModels.MainMenuViewModel

@Composable
fun MainMenu(navController: NavController, viewModel: MainMenuViewModel) {
    var selectedVehicleId by remember {
        mutableStateOf(
            navController.getConfigLong(R.string.CONFIG_selected_vehicle_id)
        )
    }
    val vehicleCount by viewModel.vehicleCount.observeAsState()
    val hasVehicles =
        remember { derivedStateOf { vehicleCount != null && vehicleCount!! > 0 } }

    AmberChargeTrackerTheme {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .background(Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight(0.2f)
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(id = R.string.app_title_electric),
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxHeight(0.7f)
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Button(
                        onClick = { navController.navigate(Screen.VehicleDetailsScreen.route) }) {
                        Text("Vehicles")
                    }
                    Button(
                        enabled = hasVehicles.value,
                        onClick = { navController.navigate(Screen.RecordJourneyScreen.route) }

                    ) {
                        Text(text = "Record Charge")
                    }
                    Button(enabled = false,
                        onClick = { /*TODO*/ }) {
                        Text("Record Journey")
                    }
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxHeight(0.2f)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    enabled = hasVehicles.value,
                    onClick = { navController.navigate(Screen.ChargeHistoryScreen.route) }) {
                    Text("Charge History")
                }
                Text("Journey History")
            }
        }
    }
}
