package org.liamjd.amber.screens

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.liamjd.amber.R
import org.liamjd.amber.screens.composables.LoadingMessage
import org.liamjd.amber.screens.composables.NumberTextField
import org.liamjd.amber.screens.composables.TimerDisplay
import org.liamjd.amber.screens.state.UIState
import org.liamjd.amber.toIntOrZero
import org.liamjd.amber.ui.theme.*
import org.liamjd.amber.viewModels.*
import java.time.LocalDateTime

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChargingScreen(navController: NavController, viewModel: ChargeEventViewModel) {

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

fun Number.leadingZero(): String = this.toString().padStart(2, '0')

@Preview
@Composable
fun KWMenu(kw: Int = 22, onSelection: (Int) -> Unit = {}) {

    val kwList = listOf(3,7,11,22,50,100,150,180,300,350)
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
            kwList.forEach {
                DropdownMenuItem(text = { Text("${it}kw") },
                    onClick = { onSelection.invoke(it); kwMenuExpanded = false })
            }
        }
    }
}