package org.liamjd.amber.viewModels

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.liamjd.amber.AmberApplication
import org.liamjd.amber.R
import org.liamjd.amber.db.entities.Setting
import org.liamjd.amber.db.entities.SettingsKey
import org.liamjd.amber.db.entities.Vehicle
import org.liamjd.amber.db.repositories.SettingsRepository
import org.liamjd.amber.db.repositories.VehicleRepository

class VehicleDetailsViewModel(private val application: AmberApplication) : ViewModel() {

    private val repository: VehicleRepository = application.vehicleRepo
    private val settingsRepository: SettingsRepository = application.settingsRepo

    private val preferences = application.applicationContext.getSharedPreferences(
        application.applicationContext.resources.getString(R.string.CONFIG), Context.MODE_PRIVATE
    )

    private var selectedVehicle: LiveData<Vehicle>? = null
    private var _selectedVehicleId: MutableLiveData<Long> = MutableLiveData<Long>()
    val selectedVehicleId
        get() = _selectedVehicleId

    private var _mode = mutableStateOf(VehicleDetailsMode.LIST)
    val mode
        get() = _mode

    var vehicleCount: LiveData<Int> = repository.getVehicleCount()

    var vehicles by mutableStateOf(emptyList<Vehicle>())


    init {
        getVehicles()
        viewModelScope.launch(Dispatchers.IO) {
            val mostRecentVehicleId = repository.getMostRecentVehicleId()
            Log.i("VehicleDetailsViewModel INIT:", "mostRecentVehicleId: $mostRecentVehicleId")
            val selectedVehicleIdFromSettings =
                settingsRepository.getSetting(SettingsKey.SELECTED_VEHICLE)
            Log.i(
                "VehicleDetailsViewModel INIT:",
                "selectedVehicleIdFromSettings: $selectedVehicleIdFromSettings"
            )

            selectedVehicleIdFromSettings?.lValue?.let {
                updateSelectedVehicle(it)
            }

            //            mostRecentVehicleId?.let {
//                _selectedVehicleId.postValue(it)
//                selectedVehicle = repository.getVehicleById(it)
//            }
//
//            _selectedVehicleId.postValue(
//                settingsRepository.getSetting(SettingsKey.SELECTED_VEHICLE)?.lValue ?: -1L
//            )
//            Log.i("VehicleDetailsViewModel INIT","mostRecentVehicleId: $mostRecentVehicleId, _selectedVehicleId: ${_selectedVehicleId.value}, selectedVehicle: ${selectedVehicle?.value}")
        }
        if (vehicleCount.value == 0) {
            _mode.value = VehicleDetailsMode.ADD
        }
    }

    fun addNewVehicle() {
        _mode.value = VehicleDetailsMode.ADD
    }

    fun updateSelectedVehicle(newVehicleId: Long) {
        _selectedVehicleId.postValue(newVehicleId)
        selectedVehicle = repository.getVehicleById(newVehicleId)
        viewModelScope.launch {
            settingsRepository.update(Setting(SettingsKey.SELECTED_VEHICLE, lValue = newVehicleId))
        }
    }

    private fun getVehicles() {
        viewModelScope.launch {
            repository.getAllVehicles().collect { response ->
                vehicles = response
            }
        }
    }

    /**
     * Insert the new vehicle and immediately set it as the currently selected vehicle, storing that value in the Settings database table
     */
    fun insert(vehicle: Vehicle) {
        viewModelScope.launch(Dispatchers.IO) {
            val primaryKey = repository.insert(vehicle)
            _selectedVehicleId.postValue(primaryKey)
            selectedVehicle = repository.getVehicleById(primaryKey)
            settingsRepository.insert(Setting(SettingsKey.SELECTED_VEHICLE, lValue = primaryKey))
            Log.i(
                "VehicleDetailsViewModel",
                "Saved selectedVehicle ID = $primaryKey to Settings ${SettingsKey.SELECTED_VEHICLE.keyString}"
            )
            _mode.value = VehicleDetailsMode.LIST
        }
    }

    private fun getMostRecentVehicleId() {
        viewModelScope.launch {
            repository.getMostRecentVehicleId()  // TODO: really this should be stored elsewhere
        }
    }
}

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