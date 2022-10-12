package org.liamjd.amber.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import org.liamjd.amber.R
import org.liamjd.amber.ui.theme.AmberChargeTrackerTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordChargeScreen(navController: NavController) {

    AmberChargeTrackerTheme {
        val chargeRecordId = 123
        var chargeTime by remember {
            mutableStateOf(LocalDateTime.now())
        }
        var odometer by remember {
            mutableStateOf(0)
        }
        var batteryStartRange by remember {
            mutableStateOf(100)
        }
        var batteryStartPct by remember {
            mutableStateOf(50)
        }
        var batteryEndRange by remember {
            mutableStateOf(200)
        }
        var batteryEndPct by remember {
            mutableStateOf(80)
        }
        var chargeDuration by remember {
            mutableStateOf(30)
        }
        var minimumFee by remember {
            mutableStateOf(100)
        }
        var costPerKWH by remember {
            mutableStateOf(15)
        }
        var totalCost by remember {
            mutableStateOf(0)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        ) {
            Text(
                text = stringResource(id = R.string.screen_recordCharge_title),
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            // METADATA
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(stringResource(id = R.string.screen_recordCharge_ID), color = Color.DarkGray)
                Text("$chargeRecordId")
                Text(stringResource(id = R.string.screen_recordCharge_time), color = Color.DarkGray)
                Text(
                    text = chargeTime.format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)),
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
                    OutlinedTextField(
                        value = odometer.toString(),
                        onValueChange = { odometer = Integer.parseInt(it) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text(stringResource(R.string.screen_recordCharge_odometer)) },
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.weight(1f),
                            value = batteryStartRange.toString(),
                            onValueChange = { batteryStartRange = Integer.parseInt(it) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            label = { Text(stringResource(R.string.screen_recordCharge_range)) },
                        )
                        Spacer(Modifier.width(10.dp))
                        OutlinedTextField(
                            modifier = Modifier.weight(1f),
                            value = batteryStartPct.toString(),
                            onValueChange = { batteryStartPct = Integer.parseInt(it) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            label = { Text(stringResource(R.string.screen_recordCharge_chargePct)) },
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
                        OutlinedTextField(
                            modifier = Modifier.weight(1f),
                            value = batteryEndRange.toString(),
                            onValueChange = { batteryEndRange = Integer.parseInt(it) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            label = { Text(stringResource(id = R.string.screen_recordCharge_range)) },
                        )
                        Spacer(Modifier.width(10.dp))
                        OutlinedTextField(
                            modifier = Modifier.weight(1f),
                            value = batteryEndPct.toString(),
                            onValueChange = { batteryEndPct = Integer.parseInt(it) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            label = { Text(stringResource(R.string.screen_recordCharge_chargePct)) },
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedTextField(
                            value = chargeDuration.toString(),
                            onValueChange = { chargeDuration = Integer.parseInt(it) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            label = { Text(stringResource(R.string.screen_recordCharge_duration)) },
                        )
                        Spacer(Modifier.width(10.dp))
                        KWMenu()
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
                Column() {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.screen_recordCharge_costs),
                            fontWeight = FontWeight.Bold
                        )
                        TextButton(onClick = { minimumFee = 0; costPerKWH = 0; totalCost = 0 }) {
                            Text(stringResource(R.string.screen_recordCharge_BUTTON_reset))
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.weight(1f),
                            value = minimumFee.toString(),
                            onValueChange = { minimumFee = Integer.parseInt(it) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            label = { Text(stringResource(R.string.screen_recordCharge_minFee)) },
                        )
                        Spacer(Modifier.width(10.dp))
                        OutlinedTextField(
                            modifier = Modifier.weight(1f),
                            value = costPerKWH.toString(),
                            onValueChange = { costPerKWH = Integer.parseInt(it) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            label = { Text(stringResource(R.string.screen_recordCharge_costPkwh)) },
                        )
                        Spacer(Modifier.width(10.dp))
                        OutlinedTextField(
                            modifier = Modifier.weight(1f),
                            value = totalCost.toString(),
                            onValueChange = { totalCost = Integer.parseInt(it) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            label = { Text(stringResource(R.string.screen_recordCharge_totalCost)) },
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
                    onClick = { /*TODO*/ }) {

                    Text(text = stringResource(R.string.screen_recordCharge_BUTTON_cancel))
                }
                FilledIconButton(
                    modifier = Modifier.weight(0.8f),
                    onClick = { /*TODO*/ }) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Check, contentDescription = stringResource(R.string.screen_recordCharge_saveDesc))
                        Text(text = stringResource(R.string.screen_recordCharge_BUTTON_save))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun KWMenu() {
    var kw by remember { mutableStateOf(3) }
    var kwMenuExpanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {

        OutlinedButton(modifier = Modifier.weight(1f),
            onClick = { kwMenuExpanded = true }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "$kw kw")
                Icon(Icons.Default.Menu, contentDescription = "Charger wattage")
            }
        }
        DropdownMenu(
            modifier = Modifier.weight(1f),
            expanded = kwMenuExpanded, onDismissRequest = { kwMenuExpanded = false }) {
            DropdownMenuItem(text = { Text("3kw") }, onClick = { kw = 3; kwMenuExpanded = false })
            DropdownMenuItem(text = { Text("7kw") }, onClick = { kw = 7; kwMenuExpanded = false })
            DropdownMenuItem(
                text = { Text("11kw") },
                onClick = { kw = 11; kwMenuExpanded = false })
            DropdownMenuItem(
                text = { Text("22kw") },
                onClick = { kw = 22; kwMenuExpanded = false })
            DropdownMenuItem(
                text = { Text("50kw") },
                onClick = { kw = 55; kwMenuExpanded = false })
            DropdownMenuItem(
                text = { Text("100kw") },
                onClick = { kw = 150; kwMenuExpanded = false })
            DropdownMenuItem(
                text = { Text("350kw") },
                onClick = { kw = 350; kwMenuExpanded = false })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RecordChargingPreview() {
    RecordChargeScreen(navController = rememberNavController())
}