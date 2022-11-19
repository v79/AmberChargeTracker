package org.liamjd.amber.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import org.liamjd.amber.R
import org.liamjd.amber.ui.theme.AmberChargeTrackerTheme
import org.liamjd.amber.viewModels.MainMenuViewModel
import org.liamjd.amber.viewModels.RecordChargingStatus
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Composable
fun MainMenu(navController: NavController, viewModel: MainMenuViewModel) {
    val vehicleCount by viewModel.vehicleCount.observeAsState()
    Log.i("MainMenu comp", "vehicleCount: $vehicleCount")
    val activeChargeEvent by viewModel.activeChargeEvent.observeAsState()
    Log.i("MainMenu comp", "activeChargeEvent: $activeChargeEvent")
    val hasVehicles =
        remember { derivedStateOf { vehicleCount != null && vehicleCount!! > 0 } }
    val isCharging = remember { derivedStateOf { activeChargeEvent?.endDateTime == null } }

    AmberChargeTrackerTheme {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .background(Color.White)
        ) {
            ScreenTitle()
            Row(
                modifier = Modifier
                    .fillMaxHeight(0.7f)
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    if (isCharging.value) {
                        activeChargeEvent?.let { event ->
                            val timeSoFar =
                                ChronoUnit.SECONDS.between(event.startDateTime, LocalDateTime.now())
                            TimerDisplay(isActive = true, startingSeconds = timeSoFar)
                            BigRoundChargingButton(status = RecordChargingStatus.CHARGING) {
                                navController.navigate(Screen.StartChargingScreen.buildRoute("${event.id}"))
                            }
                            TextButton(onClick = { viewModel.abortCharging() }) {
                                Text(text = "Abort")
                            }
                        }
                    }
                    Button(
                        enabled = false,
                        onClick = { navController.navigate(Screen.StartChargingScreen.route) }

                    ) {
                        Text(text = stringResource(R.string.screen_menu_RecordHistoricalCharge))
                    }
                    Button(
                        onClick = { navController.navigate(Screen.VehicleDetailsScreen.route) }) {
                        Text(stringResource(R.string.screen_menu_Vehicles))
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
                    Text(stringResource(R.string.screen_menu_ChargeHistory))
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .align(Alignment.End),
                horizontalArrangement = Arrangement.End
            ) {
                if (hasVehicles.value && activeChargeEvent == null) {
                    StartChargeFab(navController)
                }
            }
        }
    }
}

@Preview
@Composable
fun StartChargeFab(navController: NavController = rememberNavController()) {
    FloatingActionButton(onClick = { navController.navigate(Screen.StartChargingScreen.route) }) {
        Icon(
            painterResource(id = R.drawable.ic_baseline_ev_station_24),
            stringResource(R.string.screen_menu_fab_RecordCharge_desc)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ScreenTitle() {
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
}
