package org.liamjd.amber.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import org.liamjd.amber.R
import org.liamjd.amber.db.entities.Vehicle
import org.liamjd.amber.screens.composables.Heading
import org.liamjd.amber.screens.composables.NumberTextField
import org.liamjd.amber.screens.composables.Table
import org.liamjd.amber.toIntOrZero
import org.liamjd.amber.ui.theme.AmberChargeTrackerTheme
import org.liamjd.amber.ui.theme.md_theme_light_onSurface
import org.liamjd.amber.viewModels.VehicleDetailsViewModel

@Composable
fun VehicleDetailsScreen(navController: NavController, viewModel: VehicleDetailsViewModel) {
    val context = LocalContext.current

    val totalVehicles by viewModel.vehicleCount.observeAsState()

    AmberChargeTrackerTheme {
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
            if (totalVehicles == null || totalVehicles == 0) {
                AddVehicle(context, viewModel)
            } else {
                ShowCurrentVehicle(context, viewModel)
            }
        }
    }
}

@Composable
fun ShowCurrentVehicle(context: Context, viewModel: VehicleDetailsViewModel) {
    val selectedVehicle = viewModel.selectedVehicle.observeAsState()
    Column(modifier = Modifier.fillMaxWidth()) {
        Row { Text(text = "Current Vehicle ${selectedVehicle.value?.id}") }
        selectedVehicle.value?.let { VehicleTable(vehicle = it) }
    }
}

@Preview
@Composable
fun VehicleTable(vehicle: Vehicle = Vehicle("Rolls Royce","Silver Cloud",5176)) {
    val cellWidth: (Int) -> Dp = { index ->
        when (index) {
            0 -> 125.dp
            1 -> 100.dp
            2 -> 100.dp
            else -> 100.dp
        }
    }

    val headerCellTitle: @Composable (Int) -> Unit = { index ->
        val value = when (index) {
            0 -> "Manufacturer"
            1 -> "Model"
            2 -> "Odometer"
            else -> ""
        }
        Text(
            modifier = Modifier
                .background(Color.LightGray)
                .padding(8.dp),
            text = value,
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }

    val tableContent: @Composable (Int, Vehicle) -> Unit = { index, item ->
        val value: String = when (index) {
            0 -> item.manufacturer
            1 -> item.model
            2 -> item.odometerReading.toString()
            else -> ""
        }
        Text(
            text = value,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(8.dp),
            maxLines = 2
        )

    }

    Table(
        columnCount = 3, cellWidth = cellWidth, data = listOf(vehicle),
        headerCellContent = headerCellTitle, cellContent = tableContent
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVehicle(context: Context, viewModel: VehicleDetailsViewModel) {
    var vehicleManufacturer by remember { mutableStateOf("") }
    var vehicleModel by remember { mutableStateOf("") }
    var vehicleOdometerReading by remember { mutableStateOf("") }
    var entryEnabled by rememberSaveable { mutableStateOf(true) }

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
            colors = TextFieldDefaults.outlinedTextFieldColors(textColor = md_theme_light_onSurface),
            label = { Text(stringResource(R.string.vehicle_manufacturer)) }
        )

        OutlinedTextField(value = vehicleModel, onValueChange = { vehicleModel = it },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            enabled = entryEnabled,
            colors = TextFieldDefaults.outlinedTextFieldColors(textColor = md_theme_light_onSurface),
            label = { Text(stringResource(R.string.vehicle_model)) })

        NumberTextField(
            value = vehicleOdometerReading,
            onValueChange = { vehicleOdometerReading = it },
            enabled = entryEnabled,
            label = R.string.screen_vehicleDetails_currentOdo
        )

        FilledIconButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                entryEnabled = false
                Toast.makeText(context, R.string.screen_VehicleDetails_toast_saving, Toast.LENGTH_LONG).show()
                val newVehicle =
                    Vehicle(
                        vehicleManufacturer,
                        vehicleModel,
                        vehicleOdometerReading.toIntOrZero(),
                    )
                viewModel.insert(newVehicle)
            }) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = stringResource(R.string.screen_vehicleDetails_saveDescription)
                )
                Text(stringResource(id = R.string.screen_vehicleDetails_saveDescription))
            }
        }

    }
}