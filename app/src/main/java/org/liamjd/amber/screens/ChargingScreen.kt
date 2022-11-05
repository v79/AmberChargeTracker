package org.liamjd.amber.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import org.liamjd.amber.R
import org.liamjd.amber.getConfigLong
import org.liamjd.amber.screens.composables.Heading
import org.liamjd.amber.screens.composables.LoadingMessage
import org.liamjd.amber.screens.composables.NumberTextField
import org.liamjd.amber.screens.state.UIState
import org.liamjd.amber.screens.state.rememberFieldState
import org.liamjd.amber.screens.validators.PercentageValidator
import org.liamjd.amber.ui.theme.AmberChargeTrackerTheme
import org.liamjd.amber.ui.theme.md_theme_light_disabledButtonBackground
import org.liamjd.amber.ui.theme.md_theme_light_startButtonBackground
import org.liamjd.amber.ui.theme.md_theme_light_stopButtonBackground
import org.liamjd.amber.viewModels.ChargeEventViewModel
import org.liamjd.amber.viewModels.RecordChargingStatus
import java.time.LocalDateTime

@Composable
fun ChargingScreen(navController: NavController, viewModel: ChargeEventViewModel) {

    val selectedVehicleId = navController.getConfigLong(R.string.CONFIG_selected_vehicle_id)
    val initOdo = viewModel.odo.observeAsState()
    val context = LocalContext.current
    val inputEnabled by remember { derivedStateOf { viewModel.uiState.value != UIState.Saving } }

    AmberChargeTrackerTheme {
        when (viewModel.uiState.value) {
            is UIState.Loading -> {
                LoadingMessage()
            }
            is UIState.Navigating -> {
                // because navigating is a "side effect", we wrap it in a LaunchedEffect. It seems.
                LaunchedEffect(key1 = viewModel.uiState.value) {
                    val next = (viewModel.uiState.value as UIState.Navigating)
                    navController.navigate(next.nextScreen.route) {
                        next.backScreen?.let {
                            popUpTo(it.route)
                        }
                    }
                }
            }
            else -> {
                val chargeStartTime = LocalDateTime.now()
                val odometer = rememberFieldState(initialValue = initOdo.value.toString())
                val batteryStartPct =
                    rememberFieldState(initialValue = "50", validator = PercentageValidator)
                val batteryStartRange = rememberFieldState(initialValue = "100")

                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Heading(text = R.string.screen_recordCharge_title)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        NumberTextField(
                            field = batteryStartPct,
                            onValueChange = { batteryStartPct.onFieldUpdate(it) },
                            enabled = inputEnabled,
                            label = R.string.screen_recordCharge_chargePct,
                            modifier = Modifier.weight(0.5f)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        NumberTextField(
                            field = batteryStartRange,
                            onValueChange = { batteryStartRange.onFieldUpdate(it) },
                            enabled = inputEnabled,
                            label = R.string.screen_recordCharge_range,
                            modifier = Modifier.weight(0.5f)
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        BigRoundChargingButton(viewModel.chargingStatus.value) {
                            if (viewModel.chargingStatus.value == RecordChargingStatus.NOT_STARTED) {
                                viewModel.startCharging()
                            } else {
                                viewModel.stopCharging()
                            }
                        }
                    }
                    if (viewModel.chargingStatus.value.isActive()) {
                        TimerDisplay(
                            viewModel.chargingStatus.value == RecordChargingStatus.CHARGING,
                            startingSeconds = 0
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BigRoundChargingButton(
    status: RecordChargingStatus = RecordChargingStatus.NOT_STARTED,
    clickAction: () -> Unit = { }
) {
    val startColor = md_theme_light_startButtonBackground
    val stopColor = md_theme_light_stopButtonBackground
    val disabledColor = md_theme_light_disabledButtonBackground
    FilledIconButton(
        modifier = Modifier
            .width(96.dp)
            .height(96.dp),
        shape = IconButtonDefaults.filledShape,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = when (status) {
                RecordChargingStatus.NOT_STARTED -> startColor
                RecordChargingStatus.CHARGING -> stopColor
                RecordChargingStatus.FINISHED -> disabledColor
                RecordChargingStatus.CANCELLED -> disabledColor
            }
        ),
        onClick = { clickAction.invoke() }) {
        Icon(
            painterResource(id = R.drawable.ic_baseline_electrical_services_24),
            contentDescription = "Start charging now"
        )
    }
}

@OptIn(ExperimentalUnitApi::class)
@Preview
@Composable
fun TimerDisplay(isActive: Boolean = false, startingSeconds: Int = 7653) {
    var timeTakenSeconds by remember {
        mutableStateOf(startingSeconds)
    }
    // timer function
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .border(border = BorderStroke(2.dp, color = Color.Red)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = formatTime(timeTakenSeconds),
                textAlign = TextAlign.Center,
                color = Color.Red,
                fontSize = TextUnit(
                    12f,
                    TextUnitType.Em
                )
            )
        }

        LaunchedEffect(key1 = "chargingTime") {
            while (isActive) {
                timeTakenSeconds++
                delay(1_000)
            }
        }
    }
}

fun formatTime(time: Int): String {
    val hours: Double = time / 3600.0
    val wholeHours: Int = hours.toInt()
    val minutes: Double = (hours - wholeHours) * 60
    val wholeMinutes = minutes.toInt()
    val seconds: Int = ((minutes - wholeMinutes) * 60).toInt()
    return "${wholeHours.leadingZero()}h${wholeMinutes.leadingZero()}m:${seconds.leadingZero()}s"
}

fun Number.leadingZero(): String = this.toString().padStart(2, '0')