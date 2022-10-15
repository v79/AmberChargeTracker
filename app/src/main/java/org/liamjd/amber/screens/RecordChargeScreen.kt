package org.liamjd.amber.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.liamjd.amber.R
import org.liamjd.amber.db.entities.ChargeEvent
import org.liamjd.amber.screens.composables.CurrencyTextField
import org.liamjd.amber.screens.composables.Heading
import org.liamjd.amber.screens.composables.NumberTextField
import org.liamjd.amber.ui.theme.AmberChargeTrackerTheme
import org.liamjd.amber.viewModels.ChargeEventViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun RecordChargeScreen(navController: NavController, viewModel: ChargeEventViewModel) {

    val context = LocalContext.current
    AmberChargeTrackerTheme {
        val chargeRecordId = 123
        var chargeTime by remember {
            mutableStateOf(LocalDateTime.now())
        }
        var odometer by remember {
            mutableStateOf("0")
        }
        var batteryStartRange by remember {
            mutableStateOf("100")
        }
        var batteryStartPct by remember {
            mutableStateOf("50")
        }
        var batteryEndRange by remember {
            mutableStateOf("200")
        }
        var batteryEndPct by remember {
            mutableStateOf("80")
        }
        var chargeDuration by remember {
            mutableStateOf("30")
        }
        var minimumFee by remember {
            mutableStateOf("100")
        }
        var costPerKWH by remember {
            mutableStateOf("15")
        }
        var totalCost by remember {
            mutableStateOf("0")
        }

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
                    NumberTextField(
                        value = odometer,
                        onValueChange = {
                            odometer = it
                        },
                        label = R.string.screen_recordCharge_odometer
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        NumberTextField(
                            modifier = Modifier.weight(1f),
                            value = batteryStartRange,
                            onValueChange = { batteryStartRange = it },
                            label = R.string.screen_recordCharge_range
                        )
                        Spacer(Modifier.width(10.dp))
                        NumberTextField(
                            modifier = Modifier.weight(1f),
                            value = batteryStartPct,
                            onValueChange = { batteryStartPct = it },
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
                            value = batteryEndRange,
                            onValueChange = { batteryEndRange = it },
                            label = R.string.screen_recordCharge_range
                        )
                        Spacer(Modifier.width(10.dp))
                        NumberTextField(
                            modifier = Modifier.weight(1f),
                            value = batteryEndPct,
                            onValueChange = { batteryEndPct = it },
                            label = R.string.screen_recordCharge_chargePct
                        )
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        NumberTextField(
                            value = chargeDuration,
                            onValueChange = { chargeDuration = it },
                            label = R.string.screen_recordCharge_duration
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
                            minimumFee = "0"; costPerKWH = "0"; totalCost = "0"
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
                            value = minimumFee,
                            onValueChange = { minimumFee = it },
                            label = R.string.screen_recordCharge_minFee
                        )
                        Spacer(Modifier.width(10.dp))
                        CurrencyTextField(
                            modifier = Modifier.weight(1f),
                            value = costPerKWH,
                            onValueChange = { costPerKWH = it },
                            label = R.string.screen_recordCharge_costPkwh
                        )
                        Spacer(Modifier.width(10.dp))
                        CurrencyTextField(
                            modifier = Modifier.weight(1f),
                            value = totalCost,
                            onValueChange = { totalCost = it },
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
                        Toast.makeText(context, "Attempting to save", Toast.LENGTH_LONG).show()
                        val chargeEvent = ChargeEvent(
                            odometer = odometer,
                            batteryStartingRange = batteryStartRange,
                            batteryEndingRange = batteryEndRange,
                            batteryStartingPct = batteryStartPct,
                            batteryEndingPct = batteryEndPct,
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


@Preview
@Composable
fun KWMenu() {
    var kw by remember { mutableStateOf(22) }
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
