package org.liamjd.amber.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import org.liamjd.amber.BuildConfig
import org.liamjd.amber.R
import org.liamjd.amber.screens.composables.TimerDisplay
import org.liamjd.amber.screens.vehicles.VehicleCard
import org.liamjd.amber.ui.theme.AmberChargeTrackerTheme
import org.liamjd.amber.ui.theme.md_theme_light_surfaceTint
import org.liamjd.amber.viewModels.MainMenuViewModel
import org.liamjd.amber.viewModels.RecordChargingStatus
import org.liamjd.amber.viewModels.TimerViewModel
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenu(
    navController: NavController,
    viewModel: MainMenuViewModel,
    timerViewModel: TimerViewModel
) {

    val vehicleCount by viewModel.vehicleCount.observeAsState()
    Log.i("MainMenu comp", "vehicleCount: $vehicleCount")
    val activeChargeEvent by viewModel.activeChargeEvent.observeAsState()
    Log.i("MainMenu comp", "activeChargeEvent: $activeChargeEvent")
    val hasVehicles =
        remember { derivedStateOf { vehicleCount != null && vehicleCount!! > 0 } }
    val isCharging =
        remember { derivedStateOf { activeChargeEvent != null && activeChargeEvent?.endDateTime == null } }
    val showAbortChargeDialog = rememberSaveable { mutableStateOf(false) }
    val currentVehicle by remember { viewModel.vehicle }
    Log.i("MainMenu comp", "currentVehicle: $currentVehicle")

    AmberChargeTrackerTheme {

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        Icon(Icons.Default.Menu, stringResource(R.string.screen_menu_navDesc))
                    },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = md_theme_light_surfaceTint
                    ),
                    title = {
                        Text(
                            text = stringResource(id = R.string.app_title_electric),
                            fontWeight = FontWeight.Bold
                        )
                    })
            },
            floatingActionButtonPosition = FabPosition.End,
            floatingActionButton = {
                if (hasVehicles.value && !isCharging.value) {
                    StartChargeFab(navController)
                }
                if (!hasVehicles.value) {
                    // TODO: Add a fab which navigates to the VehicleDetailsScreen into ADD mode
                }
            },
            content = {
                Row(
                    modifier = Modifier
                        .fillMaxHeight(1f)
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {

                        if (hasVehicles.value) {
                            currentVehicle?.let {
                                VehicleCard(
                                    it,
                                    isSelected = true,
                                    onClickAction = {})
                            }
                        }

                        if (isCharging.value) {
                            activeChargeEvent?.let { event ->
                                val timeSoFar =
                                    ChronoUnit.SECONDS.between(
                                        event.startDateTime,
                                        LocalDateTime.now()
                                    )
                                Spacer(modifier = Modifier.height(8.dp))
                                TimerDisplay(
                                    isActive = true,
                                    startingSeconds = timeSoFar,
                                    viewModel = timerViewModel
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                BigRoundChargingButton(status = RecordChargingStatus.CHARGING) {
                                    navController.navigate(Screen.StartChargingScreen.buildRoute("${event.id}"))
                                }
                                // TODO: Extract this as a function so I can repeat it on the charging screen
                                TextButton(onClick = { showAbortChargeDialog.value = true }) {
                                    Text(text = stringResource(id = R.string.screen_menu_abortCharge))
                                }
                                if (showAbortChargeDialog.value) {
                                    AlertDialog(
                                        onDismissRequest = {
                                            showAbortChargeDialog.value = false
                                        },
                                        title = { Text(text = stringResource(R.string.screen_menu_abortDialog_title)) },
                                        text = { Text(text = stringResource(id = R.string.screen_menu_abortDialog_text)) },
                                        confirmButton = {
                                            TextButton(onClick = {
                                                viewModel.abortCharging(); showAbortChargeDialog.value =
                                                false
                                            }) {
                                                Text(stringResource(R.string.screen_menu_abortDialog_abort))
                                            }
                                        },
                                        dismissButton = {
                                            TextButton(onClick = {
                                                showAbortChargeDialog.value = false
                                            }) {
                                                Text(stringResource(R.string.screen_menu_abortDialog_dismiss))
                                            }
                                        })
                                }
                            }
                        }
                        Button(
                            enabled = false,
                            onClick = { navController.navigate(Screen.StartChargingScreen.route) }

                        ) {
                            Text(text = stringResource(R.string.screen_menu_RecordHistoricalCharge))
                        }
                        Button(onClick = { navController.navigate(Screen.ChargeHistoryScreen.route) }) {
                            Text(text = stringResource(R.string.screen_menu_chargeHistory))
                        }
                        Button(
                            onClick = { navController.navigate(Screen.VehicleDetailsScreen.route) }) {
                            Text(stringResource(R.string.screen_menu_Vehicles))
                        }
                        Button(enabled = false,
                            onClick = { /*TODO*/ }) {
                            Text(stringResource(R.string.screen_menu_recordJourney))
                        }
                    }
                }
            },
            bottomBar = {
                BottomAppBar {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "v${BuildConfig.VERSION_NAME}"
                        )
                        Text(
                            text = stringResource(
                                id = R.string.screen_vehicleDetails_vehicleCount,
                                vehicleCount ?: 0
                            )
                        )
                    }
                    Text(
                        text = stringResource(
                            id = R.string.screen_vehicleDetails_vehicleCount,
                            vehicleCount ?: 0
                        )
                    )
                }
            }
        )
    }
}

@Preview(showBackground = true)
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
