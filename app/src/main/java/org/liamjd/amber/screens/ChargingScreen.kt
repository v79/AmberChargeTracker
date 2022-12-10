package org.liamjd.amber.screens

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
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
import org.liamjd.amber.screens.composables.CurrencyTextField
import org.liamjd.amber.screens.composables.LoadingMessage
import org.liamjd.amber.screens.composables.NumberTextField
import org.liamjd.amber.screens.state.UIState
import org.liamjd.amber.toIntOrZero
import org.liamjd.amber.ui.theme.*
import org.liamjd.amber.viewModels.*
import java.time.LocalDateTime

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChargingScreen(navController: NavController, viewModel: ChargeEventViewModel) {

    val initOdo = viewModel.odo
    val context = LocalContext.current


    AmberChargeTrackerTheme {

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(text = stringResource(id = R.string.screen_recordCharge_title))
                    },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = md_theme_light_surfaceTint),
                    navigationIcon = {
                        IconButton(onClick = { navController.navigate(Screen.StartScreen.route) }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                "Back to main menu"
                            )
                        }
                    },
                )
            },
            content = { innerPadding ->
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    ChargingScreenContent(
                        viewModel = viewModel,
                        navController = navController,
                        context
                    )
                }
            }
        )
    }
}

@Composable
fun ChargingScreenContent(
    viewModel: ChargeEventViewModel,
    navController: NavController,
    context: Context
) {
    val inputEnabled by remember { derivedStateOf { viewModel.uiState.value != UIState.Saving && viewModel.chargingStatus != RecordChargingStatus.CHARGING } }
    val startModel = viewModel.startModel.observeAsState()
    val endModel = viewModel.endModel.observeAsState()
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
            val odometer = remember { mutableStateOf(startModel.value?.odometer.toString()) }
            val batteryStartRange =
                remember { mutableStateOf(startModel.value?.range.toString()) }
            val batteryStartPct =
                remember { mutableStateOf(startModel.value?.percentage.toString()) }
            val batteryEndPct = remember { mutableStateOf("80") }
            val batteryEndRange = remember { mutableStateOf("200") }
            val minimumFee = remember { mutableStateOf("1.00") }
            val costPerKWH = remember { mutableStateOf("0.15") }
            val totalCost = remember { mutableStateOf("1.50") }
            var kw by remember { mutableStateOf(22) }


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
                    value = odometer.value, onValueChange = { odometer.value = it },
                    enabled = inputEnabled,
                    label = R.string.screen_recordCharge_odometer,
                    modifier = Modifier.weight(0.3f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                NumberTextField(
                    value = batteryStartPct.value,
                    onValueChange = { batteryStartPct.value = it },
                    enabled = inputEnabled,
                    label = R.string.screen_recordCharge_chargePct,
                    modifier = Modifier.weight(0.3f)
                )
                Spacer(modifier = Modifier.width(10.dp))
                NumberTextField(
                    value = batteryStartRange.value,
                    onValueChange = { batteryStartRange.value = it },
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
                                odometer.value.toIntOrZero(),
                                batteryStartRange.value.toIntOrZero(),
                                batteryStartPct.value.toIntOrZero()
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
                        value = batteryEndPct.value,
                        onValueChange = { batteryEndPct.value = it },
                        enabled = inputEnabled,
                        label = R.string.screen_recordCharge_chargePct,
                        modifier = Modifier.weight(0.3f)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    NumberTextField(
                        value = batteryEndRange.value,
                        onValueChange = { batteryEndRange.value = it },
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
                                minimumFee.value = "0.00"
                                costPerKWH.value = "0.00"
                                totalCost.value = "0.00"
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
                                value = minimumFee.value,
                                onValueChange = { minimumFee.value = it },
                                enabled = inputEnabled,
                                label = R.string.screen_recordCharge_minFee
                            )
                            Spacer(Modifier.width(10.dp))
                            CurrencyTextField(
                                modifier = Modifier.weight(1f),
                                value = costPerKWH.value,
                                onValueChange = { costPerKWH.value = it },
                                enabled = inputEnabled,
                                label = R.string.screen_recordCharge_costPkwh
                            )
                            Spacer(Modifier.width(10.dp))
                            CurrencyTextField(
                                modifier = Modifier.weight(1f),
                                value = totalCost.value,
                                onValueChange = { totalCost.value = it },
                                enabled = inputEnabled,
                                label = R.string.screen_recordCharge_totalCost
                            )
                        }
                    }
                }
//                    Spacer(modifier = Modifier.weight(1f))
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
                            Toast.makeText(
                                context,
                                context.getText(R.string.toast_attemptingToSave),
                                Toast.LENGTH_LONG
                            )
                                .show()
                            val chargeEvent = EndingChargeEventModel(
                                LocalDateTime.now(),
                                batteryEndRange.value.toIntOrZero(),
                                batteryEndPct.value.toIntOrZero(),
                                kw.toFloat(),
                                totalCost.value.toIntOrZero() // this can't work yet because input is "1.50" and this function won't do rounding
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

@Preview
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
fun TimerDisplay(isActive: Boolean = false, startingSeconds: Long = 7653L) {
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
fun formatTime(time: Long): String {
    val hours: Double = time / 3600.0
    val wholeHours: Int = hours.toInt()
    val minutes: Double = (hours - wholeHours) * 60
    val wholeMinutes = minutes.toInt()
    val seconds: Int = ((minutes - wholeMinutes) * 60).toInt()
    return "${wholeHours.leadingZero()}h${wholeMinutes.leadingZero()}m:${seconds.leadingZero()}s"
}

fun Number.leadingZero(): String = this.toString().padStart(2, '0')

@Preview
@Composable
fun KWMenu(kw: Int = 22, onSelection: (Int) -> Unit = {}) {

    var kwMenuExpanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(
            onClick = { kwMenuExpanded = true }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "$kw kw")
                Icon(imageVector = Icons.Default.Menu, contentDescription = "Charger wattage")
            }
        }
        DropdownMenu(
            expanded = kwMenuExpanded, onDismissRequest = { kwMenuExpanded = false }) {
            DropdownMenuItem(
                text = { Text("3kw") },
                onClick = { onSelection.invoke(3); kwMenuExpanded = false })
            DropdownMenuItem(
                text = { Text("7kw") },
                onClick = { onSelection.invoke(7); kwMenuExpanded = false })
            DropdownMenuItem(
                text = { Text("11kw") },
                onClick = { onSelection.invoke(11); kwMenuExpanded = false })
            DropdownMenuItem(
                text = { Text("22kw") },
                onClick = { onSelection.invoke(22); kwMenuExpanded = false })
            DropdownMenuItem(
                text = { Text("50kw") },
                onClick = { onSelection.invoke(50); kwMenuExpanded = false })
            DropdownMenuItem(
                text = { Text("100kw") },
                onClick = { onSelection.invoke(150); kwMenuExpanded = false })
            DropdownMenuItem(
                text = { Text("350kw") },
                onClick = { onSelection.invoke(350); kwMenuExpanded = false })
        }
    }
}