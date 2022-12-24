package org.liamjd.amber.screens.vehicles

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import org.liamjd.amber.R
import org.liamjd.amber.db.entities.Vehicle
import org.liamjd.amber.screens.Screen
import org.liamjd.amber.screens.composables.NumberTextField
import org.liamjd.amber.toIntOrZero
import org.liamjd.amber.ui.theme.*
import org.liamjd.amber.viewModels.*
import java.time.LocalDateTime

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleDetailsScreen(navController: NavController, viewModel: VehicleDetailsViewModel) {
    val context = LocalContext.current
    val vehicleCount by viewModel.vehicleCount.observeAsState()
    val mode by viewModel.mode
    val selectedVehicleId by remember { mutableStateOf(viewModel.selectedVehicleId) }
    val selectedVehicle = viewModel.selectedVehicle
    val hasVehicles =
        remember { derivedStateOf { vehicleCount != null && vehicleCount!! > 0 } }

    AmberChargeTrackerTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(stringResource(id = R.string.screen_vehicleDetails_title))
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            when (viewModel.mode.value) {
                                VehicleDetailsMode.LIST -> {
                                    navController.navigate(Screen.StartScreen.route)
                                }
                                else -> {
                                    viewModel.mode.value = VehicleDetailsMode.LIST
                                }
                            }
                        }
                        ) {
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
                                AddOrEditVehicle(context, vehicle = null, onSave = { dto, uri ->
                                    val newPK = viewModel.insert(dto.toVehicle())
                                    viewModel.chosenPhotoUri.value = uri
                                    viewModel.selectedVehicleId.value = newPK
                                    selectedVehicleId.value = newPK
                                })
                            }
                            VehicleDetailsMode.LIST -> {
                                if (hasVehicles.value) {
                                    Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.Center,
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Text(
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.Center,
                                                text = "${selectedVehicle.value?.manufacturer} ${selectedVehicle.value?.model}"
                                            )
                                        }
                                        Row(horizontalArrangement = Arrangement.Center) {
                                            ShowAllVehicles(
                                                vehicles = viewModel.vehicles,
                                                selectedVehicleId = selectedVehicleId.value,
                                                updateSelectedVehicle = { newVehicleId ->
                                                    viewModel.updateSelectedVehicle(newVehicleId)
                                                },
                                                onLongPress = { newVehicleId ->
                                                    viewModel.switchToEditMode(newVehicleId)
                                                }
                                            )
                                        }
                                    }
                                } else {
                                    Text("No vehicles found. Press the 'Add Vehicle' button below to start")
                                }
                            }
                            VehicleDetailsMode.DELETE -> TODO()
                            VehicleDetailsMode.EDIT -> {
                                AddOrEditVehicle(
                                    context = context,
                                    vehicle = selectedVehicle.value?.toDTO(),
                                    isEdit = true,
                                    onSave = { dto, photoUri ->
                                        Log.i("VehicleDetailsScreen","Edit onSave dto = $dto")
                                        viewModel.chosenPhotoUri.value = photoUri
                                        viewModel.saveEditedVehicle(dto)
                                    })
                            }
                        }
                    }
                }
            },
            floatingActionButtonPosition = FabPosition.End,
            floatingActionButton = { AddNewVehicleFab(viewModel) },
            bottomBar = { BottomAppBar { Text("$vehicleCount vehicles registered") } }
        )
    }
}

@Composable
fun AddNewVehicleFab(viewModel: VehicleDetailsViewModel) {
    FloatingActionButton(onClick = { viewModel.addNewVehicle() }) {
        Icon(
            painterResource(id = R.drawable.ic_baseline_electric_car_24),
            "Add new vehicle"
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Preview(showBackground = true)
@Composable
fun ShowAllVehicles(
    vehicles: List<Vehicle> = listOf(
        Vehicle("Audi", "A1", 334, "AD11ADU", LocalDateTime.now(), null).apply { id = 1L },
        Vehicle("Zaphod", "Zero", 15512, "ZZ88BAD", LocalDateTime.now(), null).apply { id = 2L },
        Vehicle("Mercedes", "S-Class", 61423, "M415HAD", LocalDateTime.now(), null).apply {
            id = 3L
        }
    ),
    selectedVehicleId: Long = 2L,
    updateSelectedVehicle: (Long) -> Unit = {},
    onLongPress: (Long) -> Unit = {}
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
                VehicleCard(vehicle,
                    isSelected = chosenVehicleId == vehicle.id,
                    isEditable = true,
                    onClickAction = {
                        chosenVehicleId = it
                        Log.i(
                            "VehicleDetailsScreen",
                            "Clicked on car $chosenVehicleId (selectedVehicle was $selectedVehicleId)"
                        )
                        onItemClick.invoke(it)
                    },
                    onLongClickAction = {
                        chosenVehicleId = it
                        onLongPress(it)
                    })
            }
        }
        Text(text = "Long-press a vehicle to edit it", fontStyle = FontStyle.Italic)
        ElevatedButton(
            onClick = { updateSelectedVehicle.invoke(chosenVehicleId) },
            enabled = selectButtonEnabled
        ) {
            Text("Select vehicle $chosenVehicleId")
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrEditVehicle(
    context: Context,
    vehicle: VehicleDTO? = null,
    isEdit: Boolean = false,
    onSave: (VehicleDTO, Uri?) -> Unit = { _: VehicleDTO, _: Uri? -> {}}
) {
    var dto = VehicleDTO()
    if(vehicle != null) {
        dto = vehicle
    }
    var vehicleManufacturer by remember { mutableStateOf(dto.manufacturer) }
    var vehicleModel by remember { mutableStateOf(dto.model) }
    var vehicleOdometerReading by remember { mutableStateOf(dto.odometerReading) }
    var vehicleRegistration by remember { mutableStateOf(dto.registration) }
    var entryEnabled by remember { mutableStateOf(true) }
        var vehiclePhotoUri by remember { mutableStateOf(dto.photoPath?.let { Uri.parse( it ) }) }
    val vehicleId: Long? = dto.id

    Log.i("VehicleDetailsScreen", "AddOrEditVehicle(): Vehicle=${dto}, edit=$isEdit")

    Column(modifier = Modifier.fillMaxWidth()) {
        if (isEdit) {
            Text(text = "Editing vehicle ${dto.id}")
        } else {
            Text(text = stringResource(R.string.screen_vehicleDetails_addNew))
        }
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
            value = vehicleOdometerReading.toString(),
            onValueChange = { vehicleOdometerReading = it.toIntOrZero() },
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

        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            VehiclePhotoSelector(currentPhoto = dto.photoPath, photoChosen = {
                vehiclePhotoUri = it
            })
        }

        FilledIconButton(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                entryEnabled = false
                Toast.makeText(
                    context,
                    R.string.screen_VehicleDetails_toast_saving,
                    Toast.LENGTH_LONG
                ).show()
                dto = VehicleDTO(vehicleManufacturer,vehicleModel,vehicleOdometerReading,vehicleRegistration,vehiclePhotoUri?.path,vehicleId)
                onSave(dto, vehiclePhotoUri)
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

@Preview(showBackground = true)
@Composable
fun VehiclePhotoSelector(currentPhoto: String? = null, photoChosen: (picUri: Uri) -> Unit = { }) {
    val storagePath = LocalContext.current.filesDir.path
    val existingUri = if (currentPhoto != null) {
        Uri.parse("$storagePath/$currentPhoto")
    } else {
        null
    }
    var hasPhotograph by remember { mutableStateOf(currentPhoto != null) }
    var choosePhotograph by remember { mutableStateOf(false) }

    var selectedImageUri by remember { mutableStateOf(existingUri) }

    val pickMediaActivityResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
            photoChosen.invoke(it)
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = "Add photograph")
        Box(
            modifier = Modifier
                .border(2.dp, Color.Blue)
                .width(100.dp)
                .height(100.dp)
                .clickable {
                    pickMediaActivityResultLauncher.launch(
                        PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                    )
                }) {
            AsyncImage(
                model = selectedImageUri,
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop
            )

        }

    }
}