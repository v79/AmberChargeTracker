package org.liamjd.amber.screens

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
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
import org.liamjd.amber.screens.composables.NumberTextField
import org.liamjd.amber.screens.composables.Table
import org.liamjd.amber.screens.composables.VehicleCard
import org.liamjd.amber.toIntOrZero
import org.liamjd.amber.ui.theme.*
import org.liamjd.amber.viewModels.VehicleDetailsMode
import org.liamjd.amber.viewModels.VehicleDetailsViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun VehicleDetailsScreen(navController: NavController, viewModel: VehicleDetailsViewModel) {
    val context = LocalContext.current
    val totalVehicles by viewModel.vehicleCount.observeAsState()
    val mode by viewModel.mode
    val selectedVehicleId by viewModel.selectedVehicleId.observeAsState()

    AmberChargeTrackerTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(stringResource(id = R.string.screen_vehicleDetails_title))
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.navigate(Screen.StartScreen.route) }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                "Back to main menu"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = md_theme_light_surfaceTint)
                )
            },
            content = { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {
                    Row(modifier = Modifier.fillMaxHeight()) {
                        when (mode) {
                            VehicleDetailsMode.ADD -> {
                                AddVehicle(context, viewModel)
                            }
                            VehicleDetailsMode.LIST -> {
                                viewModel.selectedVehicleId.value?.let {
                                    Column {
                                        Row {
                                            Text("Selected vehicle: $selectedVehicleId")
                                        }
                                        Row(horizontalArrangement = Arrangement.Center) {
                                            ShowAllVehicles(
                                                vehicles = viewModel.vehicles,
                                                selectedVehicleId = it
                                            ) { newVehicleId ->
                                                viewModel.updateSelectedVehicle(newVehicleId)
                                            }
                                        }
                                    }
                                }
                            }
                            VehicleDetailsMode.DELETE -> TODO()
                            VehicleDetailsMode.EDIT -> TODO()
                        }
                    }
                }
            },
            floatingActionButtonPosition = FabPosition.End,
            floatingActionButton = { AddViewVehicleFab(viewModel) },
            bottomBar = { BottomAppBar { Text("$totalVehicles vehicles registered") } }
        )
    }
}

@Composable
fun AddViewVehicleFab(viewModel: VehicleDetailsViewModel) {
    FloatingActionButton(onClick = { viewModel.addNewVehicle() }) {
        Icon(
            painterResource(id = R.drawable.ic_baseline_electric_car_24),
            "Add new vehicle"
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ShowAllVehicles(
    vehicles: List<Vehicle> = listOf(
        Vehicle("Audi", "A1", 334, "AD11ADU").apply { id = 1L },
        Vehicle("Zaphod", "Zero", 15512, "ZZ88BAD").apply { id = 2L },
        Vehicle("Mercedes", "S-Class", 61423, "M415HAD").apply { id = 3L }
    ),
    selectedVehicleId: Long = 2L,
    updateSelectedVehicle: (Long) -> Unit = {}
) {
    var chosenVehicleId by remember {
        mutableStateOf(selectedVehicleId)
    }
    val selectButtonEnabled by remember(chosenVehicleId) { derivedStateOf { chosenVehicleId != selectedVehicleId } }
    val onItemClick = { index: Long -> chosenVehicleId = index }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .padding(2.dp)
        ) {
            items(vehicles) { vehicle ->
                VehicleCard(vehicle, isSelected = chosenVehicleId == vehicle.id, onClickAction = {
                    chosenVehicleId = it
                    Log.i(
                        "VehicleDetailsScreen",
                        "Clicked on car $chosenVehicleId (selectedVehicle was $selectedVehicleId)"
                    )
                    onItemClick.invoke(it)
                })
            }
        }
        ElevatedButton(
            onClick = { updateSelectedVehicle.invoke(chosenVehicleId) },
            enabled = selectButtonEnabled
        ) {
            Text("Select vehicle $chosenVehicleId")
        }
    }
    /* val selectedVehicle = viewModel.selectedVehicle?.observeAsState()
     if (selectedVehicle != null) {
         selectedVehicle.value?.let {
             Column(modifier = Modifier.fillMaxWidth()) {
                 Row { Text(text = "Current Vehicle ${it.id}") }
                 VehicleTable(vehicle = it)
             }
         }
     }*/
}

@Composable
@Preview(uiMode = UI_MODE_NIGHT_YES, showBackground = true)
fun VehicleTable(vehicle: Vehicle = Vehicle("Rolls Royce", "Silver Cloud", 5176, "MNY 99 BGS")) {

    val headingTextColour = if (isSystemInDarkTheme()) {
        md_theme_dark_background
    } else {
        md_theme_light_background
    }
    val cellWidth: (Int) -> Dp = { index ->
        when (index) {
            0 -> 125.dp
            1 -> 100.dp
            2 -> 75.dp
            else -> 100.dp
        }
    }

    val headerCellTitle: @Composable (Int) -> Unit = { index ->
        val value = when (index) {
            0 -> "Model"
            1 -> "Registration"
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
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            color = headingTextColour
        )
    }

    val tableContent: @Composable (Int, Vehicle) -> Unit = { index, item ->
        val value: String = when (index) {
            0 -> "${item.manufacturer}\n${item.model}"
            1 -> item.registration
            2 -> item.odometerReading.toString()
            else -> ""
        }
        Text(
            text = value,
            fontSize = 10.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(8.dp),
            maxLines = 2,
            overflow = TextOverflow.Clip
        )

    }

    Table(
        columnCount = 3, cellWidth = cellWidth, data = listOf(vehicle),
        headerCellContent = headerCellTitle, cellContent = tableContent,
        modifier = Modifier.pointerInput(Unit) {
            detectTapGestures(
                onLongPress = {
                    Log.i("VehicleTable", "Long press detected")
                }
            )
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVehicle(context: Context, viewModel: VehicleDetailsViewModel) {
    var vehicleManufacturer by remember { mutableStateOf("") }
    var vehicleModel by remember { mutableStateOf("") }
    var vehicleOdometerReading by remember { mutableStateOf("") }
    var vehicleRegistration by remember { mutableStateOf("") }
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

        OutlinedTextField(value = vehicleRegistration, onValueChange = { vehicleRegistration = it },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            enabled = entryEnabled,
            label = { Text("Registration") })

        FilledIconButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                entryEnabled = false
                Toast.makeText(
                    context,
                    R.string.screen_VehicleDetails_toast_saving,
                    Toast.LENGTH_LONG
                ).show()
                val newVehicle =
                    Vehicle(
                        vehicleManufacturer,
                        vehicleModel,
                        vehicleOdometerReading.toIntOrZero(),
                        vehicleRegistration
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