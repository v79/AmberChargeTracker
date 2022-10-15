package org.liamjd.amber.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import org.liamjd.amber.R
import org.liamjd.amber.db.entities.Vehicle
import org.liamjd.amber.screens.composables.Heading
import org.liamjd.amber.screens.composables.NumberTextField
import org.liamjd.amber.ui.theme.AmberChargeTrackerTheme
import org.liamjd.amber.viewModels.VehicleDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleDetailsScreen(navController: NavController, viewModel: VehicleDetailsViewModel) {
    val context = LocalContext.current

    val totalVehicles by viewModel.getVehicleCount().observeAsState()
    var entryEnabled by rememberSaveable { mutableStateOf(true) }

    AmberChargeTrackerTheme {
        var vehicleManufacturer by remember { mutableStateOf("") }
        var vehicleModel by remember { mutableStateOf("") }
        var vehicleOdometerReading by remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp)
        ) {
            Heading(text = R.string.screen_vehicleDetails_title)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(
                        id = R.string.screen_vehicleDetails_vehicleCount,
                        totalVehicles ?: 0
                    )
                )
            }
            if (totalVehicles == 0) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(text = stringResource(R.string.screen_vehicleDetails_addNew))
                    OutlinedTextField(
                        value = vehicleManufacturer,
                        onValueChange = { vehicleManufacturer = it },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        enabled = entryEnabled,
                        label = { Text(stringResource(R.string.vehicle_manufacturer)) }
                    )
                    OutlinedTextField(value = vehicleModel, onValueChange = { vehicleModel = it },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        ),
                        enabled = entryEnabled,
                        label = { Text(stringResource(R.string.vehicle_model)) })
                    NumberTextField(
                        value = vehicleOdometerReading,
                        onValueChange = { vehicleOdometerReading = it },
                        enabled = entryEnabled,
                        label = R.string.screen_vehicleDetails_currentOdo
                    )
                    Button(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { /*TODO*/ }) {
                        FilledIconButton(onClick = {
                            entryEnabled = false
                            Toast.makeText(context, "Attempting to save", Toast.LENGTH_LONG).show()
                            val newVehicle = Vehicle(vehicleManufacturer, vehicleModel)
                            viewModel.insert(newVehicle)
                        }) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = stringResource(R.string.screen_vehicleDetails_saveDescription)
                                )
                                Text(stringResource(id = R.string.screen_vehicleDetails_saveDescription))
                            }
                        }
                    }
                }
            } else {

            }
        }
    }
}
