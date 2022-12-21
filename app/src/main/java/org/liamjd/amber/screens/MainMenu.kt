package org.liamjd.amber.screens

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
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
import org.liamjd.amber.screens.composables.TimerDisplay
import org.liamjd.amber.screens.vehicles.VehicleCard
import org.liamjd.amber.ui.theme.AmberChargeTrackerTheme
import org.liamjd.amber.ui.theme.md_theme_light_surfaceTint
import org.liamjd.amber.viewModels.MainMenuViewModel
import org.liamjd.amber.viewModels.RecordChargingStatus
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainMenu(navController: NavController, viewModel: MainMenuViewModel) {

    val lifecycleOwner = LocalLifecycleOwner.current
    // This, in combination with with the viewModel init { refreshView() }, gives me the desired effect
    /*LaunchedEffect(Unit) {
        lifecycleOwner.withStateAtLeast(Lifecycle.State.CREATED) {
            Log.i("MainMenu comp", "Lifecycle resumed, calling refresh")
            viewModel.refreshView()
        }
    }*/

    val vehicleCount by viewModel.vehicleCount.observeAsState()
    Log.i("MainMenu comp", "vehicleCount: $vehicleCount")
    val activeChargeEvent by viewModel.activeChargeEvent.observeAsState()
    Log.i("MainMenu comp", "activeChargeEvent: $activeChargeEvent")
    val hasVehicles =
        remember { derivedStateOf { vehicleCount != null && vehicleCount!! > 0 } }
    val isCharging =
        remember { derivedStateOf { activeChargeEvent != null && activeChargeEvent?.endDateTime == null } }
    val showAbortChargeDialog = remember { mutableStateOf(false) }
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
                if(!hasVehicles.value) {
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
                                TimerDisplay(isActive = true, startingSeconds = timeSoFar)
                                Spacer(modifier = Modifier.height(8.dp))
                                BigRoundChargingButton(status = RecordChargingStatus.CHARGING) {
                                    navController.navigate(Screen.StartChargingScreen.buildRoute("${event.id}"))
                                }
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

@Composable
fun AbortChargeDialogContent() {

}