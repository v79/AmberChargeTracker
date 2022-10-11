package org.liamjd.amber.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.VerticalAlignmentLine
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import org.liamjd.amber.ui.theme.AmberChargeTrackerTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        ) {
            Text(
                text = "Record Charging Event",
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
                Text("ID")
                Text("$chargeRecordId")
                Text("Time")
                TextButton(onClick = {}) {
                    Text(
                        text = chargeTime.format(DateTimeFormatter.ofPattern("yyyy MM dd H:m"))
                    )
                }
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
                            text = "Starting values",
                            fontWeight = FontWeight.Bold
                        )
                    }
                    OutlinedTextField(
                        value = odometer.toString(),
                        onValueChange = { odometer = Integer.parseInt(it) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        label = { Text("Odometer (mph)") },
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
                            label = { Text("Range (mph)") },
                        )
                        OutlinedTextField(
                            modifier = Modifier.weight(1f),
                            value = batteryStartPct.toString(),
                            onValueChange = { batteryStartPct = Integer.parseInt(it) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            label = { Text("Charge Percentage") },
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
                            text = "Ending values",
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        OutlinedTextField(
                            modifier = Modifier.weight(1f),
                            value = batteryEndRange.toString(),
                            onValueChange = { batteryEndRange = Integer.parseInt(it) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            label = { Text("Range (mph)") },
                        )
                        OutlinedTextField(
                            modifier = Modifier.weight(1f),
                            value = batteryEndPct.toString(),
                            onValueChange = { batteryEndPct = Integer.parseInt(it) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            label = { Text("Charge Percentage") },
                        )
                    }
                    Row {
                        OutlinedTextField(
                            value = chargeDuration.toString(),
                            onValueChange = { chargeDuration = Integer.parseInt(it) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            label = { Text("Duration (min)") },
                        )
                        KWMenu()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun KWMenu() {
    var kw by remember { mutableStateOf(3) }
    var kwMenuExpanded by remember { mutableStateOf(false) }
    Row(modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly) {
        Text(text = "Wattage")
        Text(text = "$kw")
        IconButton(
            modifier = Modifier.weight(1f),
            onClick = { kwMenuExpanded = true }) {
            Icon(Icons.Default.Menu, contentDescription = "Charger wattage")
        }

        DropdownMenu(
            modifier = Modifier.weight(1f),
            expanded = kwMenuExpanded, onDismissRequest = { kwMenuExpanded = false }) {
            DropdownMenuItem(text = { Text("3kw") }, onClick = { kw = 3; kwMenuExpanded = false })
            DropdownMenuItem(text = { Text("7kw") }, onClick = { kw = 7 ; kwMenuExpanded = false})
            DropdownMenuItem(
                text = { Text("11kw") },
                onClick = { kw = 11 ; kwMenuExpanded = false})
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

@Preview
@Composable
fun RecordChargingPreview() {
    RecordChargeScreen(navController = rememberNavController())
}