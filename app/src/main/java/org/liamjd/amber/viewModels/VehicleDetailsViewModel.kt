package org.liamjd.amber.viewModels

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.liamjd.amber.AmberApplication
import org.liamjd.amber.db.entities.Setting
import org.liamjd.amber.db.entities.SettingsKey
import org.liamjd.amber.db.entities.Vehicle
import org.liamjd.amber.db.repositories.SettingsRepository
import org.liamjd.amber.db.repositories.VehicleRepository
import java.io.IOException
import java.time.LocalDateTime
import java.time.ZoneOffset

class VehicleDetailsViewModel(private val application: AmberApplication) : ViewModel() {

    private val repository: VehicleRepository = application.vehicleRepo
    private val settingsRepository: SettingsRepository = application.settingsRepo

    private var _selectedVehicle = mutableStateOf<Vehicle?>(null)
    val selectedVehicle
        get() = _selectedVehicle

    var selectedVehicleId = mutableStateOf(-1L) // will get reset on load

    private var _mode = mutableStateOf(VehicleDetailsMode.LIST)
    val mode
        get() = _mode

    var vehicleCount: LiveData<Int> = repository.getVehicleCount()
    var vehicles by mutableStateOf(emptyList<Vehicle>())
    var chosenPhotoUri: MutableState<Uri?> = mutableStateOf(null)


    init {
        Log.i("VehicleDetailsViewModel", "INIT: About to get all the vehicles")
        getVehicles()
        viewModelScope.launch(Dispatchers.IO) {
            val mostRecentVehicleId = repository.getMostRecentVehicleId()
            Log.i("VehicleDetailsViewModel", "INIT: mostRecentVehicleId: $mostRecentVehicleId")
            val selectedVehicleIdFromSettings =
                settingsRepository.getSettingLongValue(SettingsKey.SELECTED_VEHICLE)
            Log.i(
                "VehicleDetailsViewModel",
                "INIT: selectedVehicleIdFromSettings: $selectedVehicleIdFromSettings"
            )

            selectedVehicleIdFromSettings?.let {
                Log.i("VehicleDetailsViewModel", "INIT: updateSelectedVehicle($it)")
                updateSelectedVehicle(it)
            }
        }
        if (vehicleCount.value == 0) {
            _mode.value = VehicleDetailsMode.ADD
        }
    }

    /**
     * Switch to ADD mode
     */
    fun addNewVehicle() {
        _mode.value = VehicleDetailsMode.ADD
    }

    /**
     * Switch to edit mode and update the vehicle selection
     */
    fun switchToEditMode(editVehicleId: Long) {
        Log.i("VehicleDetailsVM","switchToEditMode($editVehicleId)")
        _mode.value = VehicleDetailsMode.EDIT
        updateSelectedVehicle(editVehicleId)
    }

    /**
     * Update the selected vehicle based on the `newVehicleId` param, and save that to the settings repository
     */
    fun updateSelectedVehicle(newVehicleId: Long) {
        viewModelScope.launch {
            selectedVehicleId.value = newVehicleId
            _selectedVehicle.value = repository.getVehicleById(newVehicleId)
            Log.i("VehicleDetailsVM","updatedSelectedVehicle($newVehicleId) fetching new vehicle and updating selected vehicle in Settings")
            settingsRepository.update(Setting(SettingsKey.SELECTED_VEHICLE, lValue = newVehicleId))
        }
    }

    /**
     * Get all the vehicles in the database
     */
    private fun getVehicles() {
        Log.i("ViewDetailsVM", "getVehicles() collecting flow")
        viewModelScope.launch {
            repository.getAllVehicles().collect { response ->
                vehicles = response
            }
        }
    }

    /**
     * Insert the new vehicle and immediately set it as the currently selected vehicle, storing that value in the Settings database table
     */
    fun insert(vehicle: Vehicle): Long {
        viewModelScope.launch(Dispatchers.IO) {
            val primaryKey = repository.insert(vehicle)
            selectedVehicleId.value = primaryKey
            _selectedVehicle.value = repository.getVehicleById(primaryKey)
            settingsRepository.insert(Setting(SettingsKey.SELECTED_VEHICLE, lValue = primaryKey))
            Log.i(
                "VehicleDetailsViewModel",
                "Saved selectedVehicle ID = $primaryKey to Settings ${SettingsKey.SELECTED_VEHICLE.keyString}"
            )
            _mode.value = VehicleDetailsMode.LIST
            // now that we have a vehicle, look to see if there is an image for it
            chosenPhotoUri.value?.let { uri ->
                val photoPath = saveImageToStorage(uri, primaryKey, vehicle.manufacturer)
                repository.updatePhotoPath(primaryKey, photoPath)
            }
        }
        return selectedVehicleId.value
    }

    /**
     * Update the database entry for the given VehicleDTO
     */
    fun saveEditedVehicle(vehicleDTO: VehicleDTO) {
        Log.i("VehicleDetailsVM","saveEditedVehicle($vehicleDTO)")
        if(vehicleDTO.id == null) {
            Log.e("VehicleDetailsVM","Unable to update vehicle $vehicleDTO as the ID is null; this should be impossible")
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                val updatedVehicle = vehicleDTO.toVehicle()
                repository.updateVehicle(updatedVehicle)
            }
        }
    }

    /**
     * Copy the photograph from the given uri to the application's internal storage
     * The filename will be "id-manufacturer-timestamp.jpg"
     * @param uri Uri provided by the photo picker
     * @param id primary key of the vehicle
     * @param manufacturer vehicle manufacturer, just a string to make the filename a little more human readable
     * @return the calculated filename
     * */
    private fun saveImageToStorage(uri: Uri, id: Long, manufacturer: String): String {
        val fileName = "$id-$manufacturer-${
            LocalDateTime.now().toInstant(
                ZoneOffset.UTC
            ).toEpochMilli()
        }.jpg"
        Log.i("ViewDetailsViewModel", "Chosen photo is $uri")
        try {
            val inputStream = application.contentResolver.openInputStream(uri)
            val imageBytes = inputStream?.readBytes()
            inputStream?.close()
            if (imageBytes?.isNotEmpty() == true) {

                application.openFileOutput(fileName, Context.MODE_PRIVATE)
                    .use { stream ->
                        Log.i(
                            "ViewDetailsViewModel",
                            "Writing bytes from $uri to new byte array with name $fileName"
                        )
                        stream.write(imageBytes)
                    }
            }
        } catch (e: IOException) {
            Log.e("ViewDetailsViewModel", e.toString())
        }
        return fileName
    }

    private fun getMostRecentVehicleId() {
        viewModelScope.launch {
            repository.getMostRecentVehicleId()  // TODO: really this should be stored elsewhere
        }
    }
}

/**
 * Enum class represents the different modes the VehicleDetailsScreen can be in
 */
enum class VehicleDetailsMode {
    LIST,
    ADD,
    DELETE,
    EDIT
}


class VehicleDetailsViewModelFactory(private val application: AmberApplication) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(VehicleDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return VehicleDetailsViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class ${modelClass.canonicalName}")
    }
}

/**
 * Represents a Vehicle but fully editable
 */
data class VehicleDTO(
    var manufacturer: String = "",
    var model: String = "",
    var odometerReading: Int = 0,
    var registration: String = "",
    var photoPath: String? = null,
    var id: Long? = null
)

/**
 * Convert a database Vehicle entity to the [VehicleDTO] data class
 */
fun Vehicle.toDTO() = VehicleDTO(
    manufacturer = this.manufacturer,
    model = this.model,
    odometerReading = this.odometerReading,
    registration = this.registration,
    photoPath = this.photoPath,
    id = this.id
)

/**
 * Convert the VehicleDTO to the [Vehicle] database entity class
 */
fun VehicleDTO.toVehicle() = Vehicle(
    manufacturer = this.manufacturer,
    model = this.model,
    odometerReading = this.odometerReading,
    registration = this.registration,
    lastUpdated = LocalDateTime.now(),
    photoPath = this.photoPath
)