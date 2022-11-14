package org.liamjd.amber.screens

/*
@Composable
fun RecordChargeScreen(navController: NavController, viewModel: ChargeEventViewModel) {

    val preferences = navController.context.getSharedPreferences(stringResource(R.string.CONFIG), 0)
    val selectedVehicleId = navController.getConfigLong(R.string.CONFIG_selected_vehicle_id)
    val initOdo = viewModel.odo.observeAsState()
    Log.e("RecordChargeScreen", "initOdo has been set to ${initOdo.value}")
    val context = LocalContext.current
    val inputEnabled by remember { derivedStateOf { viewModel.uiState.value != UIState.Saving } }
    AmberChargeTrackerTheme {

        when (viewModel.uiState.value) {
            is UIState.Loading -> {
                Text("Loading")
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
                val chargeDateTime by remember {
                    mutableStateOf(LocalDateTime.now())
                }
                val odometer = rememberFieldState(initialValue = initOdo.value.toString())
                val batteryStartRange = rememberFieldState(initialValue = "100")
                val batteryStartPct =
                    rememberFieldState(initialValue = "50", validator = PercentageValidator)
                val batteryEndRange = rememberFieldState(initialValue = "200")
                val batteryEndPct =
                    rememberFieldState(initialValue = "80", validator = PercentageValidator)
                val chargeDuration = rememberFieldState(initialValue = "30")
                val minimumFee =
                    rememberFieldState(initialValue = "1.00", validator = CurrencyValidator)
                val costPerKWH =
                    rememberFieldState(initialValue = "0.15", validator = CurrencyValidator)
                val totalCost =
                    rememberFieldState(initialValue = "1.01", validator = CurrencyValidator)
                var kw by remember { mutableStateOf(22) }
                var saveDisabled by remember { mutableStateOf(false) }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(4.dp)
                ) {
                    Heading(text = R.string.screen_recordCharge_title)
                    // METADATA
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            stringResource(id = R.string.screen_recordCharge_time),
                            color = Color.DarkGray
                        )
                        Text(
                            text = chargeDateTime.format(
                                DateTimeFormatter.ofLocalizedDateTime(
                                    FormatStyle.MEDIUM
                                )
                            ),
                            modifier = Modifier.clickable { }
                        )
                    }
                    // STARTING VALUES
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp), horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.screen_recordCharge_starting),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            NumberTextField(
                                field = odometer,
                                onValueChange = {
                                    odometer.onFieldUpdate(it)
                                },
                                enabled = inputEnabled,
                                label = R.string.screen_recordCharge_odometer
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                NumberTextField(
                                    modifier = Modifier.weight(1f),
                                    field = batteryStartRange,
                                    onValueChange = {
                                        batteryStartRange.onFieldUpdate(
                                            it,
                                            after = { saveDisabled = !batteryStartRange.valid })
                                    },
                                    enabled = inputEnabled,
                                    label = R.string.screen_recordCharge_range
                                )
                                Spacer(Modifier.width(10.dp))
                                NumberTextField(
                                    modifier = Modifier.weight(1f),
                                    field = batteryStartPct,
                                    onValueChange = {
                                        batteryStartPct.onFieldUpdate(
                                            it,
                                            after = { saveDisabled = !batteryStartPct.valid })
                                    },
                                    enabled = inputEnabled,
                                    label = R.string.screen_recordCharge_chargePct
                                )
                            }
                        }
                    }
                    // END VALUES
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp), horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column {
                            Row {
                                Text(
                                    text = stringResource(R.string.screen_recordCharge_ending),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                NumberTextField(
                                    modifier = Modifier.weight(1f),
                                    field = batteryEndRange,
                                    onValueChange = {
                                        batteryEndRange.onFieldUpdate(
                                            it,
                                            after = { saveDisabled = !batteryEndRange.valid })
                                    },
                                    enabled = inputEnabled,
                                    label = R.string.screen_recordCharge_range
                                )
                                Spacer(Modifier.width(10.dp))
                                NumberTextField(
                                    field = batteryEndPct,
                                    modifier = Modifier.weight(1f),
                                    onValueChange = {
                                        batteryEndPct.onFieldUpdate(
                                            it,
                                            after = { saveDisabled = !batteryEndPct.valid })
                                    },
                                    enabled = inputEnabled,
                                    label = R.string.screen_recordCharge_chargePct
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                NumberTextField(
                                    field = chargeDuration,
                                    onValueChange = {
                                        chargeDuration.onFieldUpdate(
                                            it,
                                            after = { saveDisabled = !chargeDuration.valid })
                                    },
                                    enabled = inputEnabled,
                                    label = R.string.screen_recordCharge_duration
                                )
                                Spacer(Modifier.width(10.dp))
                                KWMenu(kw, onSelection = { kw = it })
                            }
                        }
                    }
                    // COSTS
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
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
                                    .fillMaxWidth(),
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
                            enabled = !saveDisabled,
                            onClick = {
                                Toast.makeText(context, "Attempting to save", Toast.LENGTH_LONG)
                                    .show()
                                val chargeEvent = ChargeEvent(
                                    odometer = odometer.computed.toIntOrZero(),
                                    batteryStartingRange = batteryStartRange.computed,
                                    batteryEndingRange = batteryEndRange.computed,
                                    batteryStartingPct = batteryStartPct.computed,
                                    batteryEndingPct = batteryEndPct.computed,
                                    vehicleId = selectedVehicleId,
                                    kilowatt = kw.toFloat(),
                                    totalCost = totalCost.computed.toIntOrZero()
                                )
                                viewModel.insert(chargeEvent)

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

fun extractCostFromInput(input: String): Int {
    val components = input.split(".")
    if (components.size > 2) {
        Log.e("RecordCharge", "Unable to convert $input to pennies as there are two many '.'")
        return 0
    }
    return 0
}

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

/* This doesn't even compile!
class PercentageVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        TODO("Not yet implemented")
    }
}
*/

/*
@Preview(showBackground = true)
@Composable
fun RecordChargingPreview() {
    RecordChargeScreen(navController = rememberNavController())
}*/


 */