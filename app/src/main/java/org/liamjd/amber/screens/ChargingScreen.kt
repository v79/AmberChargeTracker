package org.liamjd.amber.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import org.liamjd.amber.screens.composables.CurrencyTextField
import org.liamjd.amber.screens.composables.Heading
import org.liamjd.amber.screens.composables.LoadingMessage
import org.liamjd.amber.screens.composables.NumberTextField
import org.liamjd.amber.screens.state.UIState
import org.liamjd.amber.screens.state.rememberFieldState
import org.liamjd.amber.screens.validators.CurrencyValidator
import org.liamjd.amber.screens.validators.PercentageValidator
import org.liamjd.amber.toIntOrZero
import org.liamjd.amber.ui.theme.AmberChargeTrackerTheme
import org.liamjd.amber.ui.theme.md_theme_light_disabledButtonBackground
import org.liamjd.amber.ui.theme.md_theme_light_startButtonBackground
import org.liamjd.amber.ui.theme.md_theme_light_stopButtonBackground
import org.liamjd.amber.viewModels.ChargeEventViewModel
import org.liamjd.amber.viewModels.EndingChargeEventModel
import org.liamjd.amber.viewModels.RecordChargingStatus
import org.liamjd.amber.viewModels.StartingChargeEventModel
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
                val odometer = rememberFieldState(initialValue = initOdo.value.toString())
                val batteryStartPct =
                    rememberFieldState(initialValue = "50", validator = PercentageValidator)
                val batteryStartRange = rememberFieldState(initialValue = "100")
                val batteryEndPct = rememberFieldState("100", validator = PercentageValidator)
                val batteryEndRange = rememberFieldState(initialValue = "200")
                val minimumFee =
                    rememberFieldState(initialValue = "1.00", validator = CurrencyValidator)
                val costPerKWH =
                    rememberFieldState(initialValue = "0.15", validator = CurrencyValidator)
                val totalCost =
                    rememberFieldState(initialValue = "1.01", validator = CurrencyValidator)
                var kw by remember { mutableStateOf(22) }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    Heading(text = R.string.screen_recordCharge_title)
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = stringResource(id = R.string.screen_recordCharge_starting),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        NumberTextField(
                            field = odometer, onValueChange = { odometer.onFieldUpdate(it) },
                            enabled = inputEnabled,
                            label = R.string.screen_recordCharge_odometer,
                            modifier = Modifier.weight(0.3f)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        NumberTextField(
                            field = batteryStartPct,
                            onValueChange = { batteryStartPct.onFieldUpdate(it) },
                            enabled = inputEnabled,
                            label = R.string.screen_recordCharge_chargePct,
                            modifier = Modifier.weight(0.3f)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        NumberTextField(
                            field = batteryStartRange,
                            onValueChange = { batteryStartRange.onFieldUpdate(it) },
                            enabled = inputEnabled,
                            label = R.string.screen_recordCharge_range,
                            modifier = Modifier.weight(0.3f)
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        BigRoundChargingButton(viewModel.chargingStatus) {
                            if (viewModel.chargingStatus == RecordChargingStatus.NOT_STARTED) {
                                viewModel.startCharging(
                                    StartingChargeEventModel(
                                        LocalDateTime.now(),
                                        odometer.value.value.toIntOrZero(),
                                        batteryStartRange.computed.toIntOrZero(),
                                        batteryStartPct.computed.toIntOrZero()
                                    )
                                )
                            } else {
                                viewModel.stopCharging()
                            }
                        }
                    }
                    Row {
                        if (viewModel.chargingStatus == RecordChargingStatus.CHARGING || viewModel.chargingStatus == RecordChargingStatus.FINISHED) {
                            TimerDisplay(
                                isActive = (viewModel.chargingStatus == RecordChargingStatus.CHARGING),
                                startingSeconds = viewModel.chargingSeconds.value
                            )
                        }
                    }

                    if (viewModel.chargingStatus == RecordChargingStatus.FINISHED) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.screen_recordCharge_ending),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            NumberTextField(
                                field = batteryEndPct,
                                onValueChange = { batteryEndPct.onFieldUpdate(it) },
                                enabled = inputEnabled,
                                label = R.string.screen_recordCharge_chargePct,
                                modifier = Modifier.weight(0.3f)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            NumberTextField(
                                field = batteryEndRange,
                                onValueChange = { batteryEndRange.onFieldUpdate(it) },
                                enabled = inputEnabled,
                                label = R.string.screen_recordCharge_range,
                                modifier = Modifier.weight(0.3f)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            KWMenu(kw = kw, onSelection = { kw = it })
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = stringResource(R.string.screen_recordCharge_costs),
                                        fontWeight = FontWeight.Bold
                                    )
                                    TextButton(onClick = {
                                        minimumFee.resetValue("0.00"); costPerKWH.resetValue(
                                        "0.00"
                                    ); totalCost.resetValue("0.00")
                                    }) {
                                        Text(stringResource(R.string.screen_recordCharge_BUTTON_reset))
                                    }
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceAround,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    CurrencyTextField(
                                        modifier = Modifier.weight(1f),
                                        field = minimumFee,
                                        onValueChange = { minimumFee.onFieldUpdate(it) },
                                        enabled = inputEnabled,
                                        label = R.string.screen_recordCharge_minFee
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    CurrencyTextField(
                                        modifier = Modifier.weight(1f),
                                        field = costPerKWH,
                                        onValueChange = { costPerKWH.onFieldUpdate(it) },
                                        enabled = inputEnabled,
                                        label = R.string.screen_recordCharge_costPkwh
                                    )
                                    Spacer(Modifier.width(10.dp))
                                    CurrencyTextField(
                                        modifier = Modifier.weight(1f),
                                        field = totalCost,
                                        onValueChange = { totalCost.onFieldUpdate(it) },
                                        enabled = inputEnabled,
                                        label = R.string.screen_recordCharge_totalCost
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(
                                modifier = Modifier.weight(0.2f),
                                onClick = { navController.navigate(Screen.StartScreen.route) }) {
                                Text(text = stringResource(R.string.screen_recordCharge_BUTTON_cancel))
                            }

                            FilledIconButton(
                                modifier = Modifier.weight(0.8f),
                                onClick = {
                                    Toast.makeText(context, "Attempting to save", Toast.LENGTH_LONG)
                                        .show()
                                    val chargeEvent = EndingChargeEventModel(
                                        LocalDateTime.now(),
                                        batteryEndRange.computed.toIntOrZero(),
                                        batteryEndPct.computed.toIntOrZero(),
                                        kw.toFloat(),
                                        totalCost.computed.toIntOrZero()
                                    )
                                    viewModel.saveCharge(chargeEvent)

                                }) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = stringResource(R.string.screen_recordCharge_saveDesc)
                                    )
                                    Text(text = stringResource(R.string.screen_recordCharge_BUTTON_save))
                                }
                            }
                        }
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
    val label = when (status) {
        RecordChargingStatus.NOT_STARTED -> "Start charging"
        RecordChargingStatus.CHARGING -> "End charging"
        RecordChargingStatus.FINISHED -> "Charging complete"
        RecordChargingStatus.CANCELLED -> ""
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row {
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
        Row { Text(text = label, textAlign = TextAlign.Center) }
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

        LaunchedEffect(key1 = isActive) {
            while (isActive) {
                timeTakenSeconds++
                delay(1_000)
            }
        }
    }
}

/**
 * Format count of seconds as an hours/minutes/seconds string
 * @param time number of seconds
 * @return string in format "hh:mm:ss"
 */
fun formatTime(time: Int): String {
    val hours: Double = time / 3600.0
    val wholeHours: Int = hours.toInt()
    val minutes: Double = (hours - wholeHours) * 60
    val wholeMinutes = minutes.toInt()
    val seconds: Int = ((minutes - wholeMinutes) * 60).toInt()
    return "${wholeHours.leadingZero()}h${wholeMinutes.leadingZero()}m:${seconds.leadingZero()}s"
}

fun Number.leadingZero(): String = this.toString().padStart(2, '0')