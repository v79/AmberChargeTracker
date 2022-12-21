package org.liamjd.amber.viewModels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.liamjd.amber.AmberApplication
import org.liamjd.amber.db.entities.ChargeEvent
import org.liamjd.amber.db.entities.SettingsKey
import org.liamjd.amber.db.entities.Vehicle
import org.liamjd.amber.db.repositories.ChargeEventRepository
import org.liamjd.amber.db.repositories.SettingsRepository
import org.liamjd.amber.db.repositories.VehicleRepository

class MainMenuViewModel(application: AmberApplication) : ViewModel() {

    private val vehicleRepository: VehicleRepository = application.vehicleRepo
    private val settingsRepository: SettingsRepository = application.settingsRepo
    private val chargeEventRepository: ChargeEventRepository = application.chargeEventRepo

    var selectedVehicleId: Long? = null

    private var _vehicleCount: LiveData<Int> = MutableLiveData()
    val vehicleCount: LiveData<Int>
        get() = _vehicleCount

    var vehicle = mutableStateOf<Vehicle?>(null)

    private var _activeChargeEvent: LiveData<ChargeEvent?> = MutableLiveData()
    val activeChargeEvent: LiveData<ChargeEvent?>
        get() = _activeChargeEvent


    init {
        viewModelScope.launch {
            Log.i("MainMenuViewModel", "init{}")
            _vehicleCount = vehicleRepository.getVehicleCount()
            selectedVehicleId = settingsRepository.getSettingLongValue(SettingsKey.SELECTED_VEHICLE)

            selectedVehicleId?.let {
                vehicle.value = vehicleRepository.getVehicleById(it)
                Log.i("MainMenuViewModel","looked for vehicle with id=$it, found $vehicle")
            }

            val activeChargeId =
                settingsRepository.getSettingLongValue(SettingsKey.CURRENT_CHARGE_EVENT)
            Log.i("ChargeEventViewModel refresh", "activeChargeId = $activeChargeId")
            activeChargeId?.let {
                _activeChargeEvent = chargeEventRepository.getLiveChargeEventWithId(it)
            }
        }
    }

    /**
     * Refresh the view by fetching the number of vehicles, and searching for an active charge event ID
     * If it exists, fetch the charge event
     * Called by ViewModel init, and also in the MainMenu composable LaunchedEffect
     *//*
    fun refreshView() {
        Log.i("MainMenuViewModel","refreshView(): ${LocalDateTime.now().toLocalString()}")
        viewModelScope.launch {
            _vehicleCount = vehicleRepository.getVehicleCount()
            Log.i("MainMenuViewModel", "refreshView(): _vehicleCount = ${_vehicleCount.value}")
            _selectedVehicleId = settingsRepository.getSetting(SettingsKey.SELECTED_VEHICLE)?.lValue

            if(_selectedVehicleId == null) {
                // Somehow we don't have a vehicle set, so get the most recent value, hoping it exists
                _selectedVehicleId = vehicleRepository.getMostRecentVehicleId()
                if(_selectedVehicleId != null) {
                    // and now we are sure we have a value, save it
                    Log.i("MainMenuViewModel","refreshView(): Updating selected vehicle in repo to $_selectedVehicleId")
                    settingsRepository.update(Setting(SettingsKey.SELECTED_VEHICLE, lValue = _selectedVehicleId))
                }
            }

            _selectedVehicleId?.let {
                this.launch {
                    Log.i(
                        "MainMenuViewModel",
                        "refreshView(): Almost certainly got a vehicle by now, so fetch it (id=$_selectedVehicleId)!"
                    )
                    vehicle = vehicleRepository.getVehicleById(it)
                    Log.i("MainMenuViewModel", "refreshView(): Fetched -> ${vehicle.value}")
                }
            }
            val activeChargeId =
                settingsRepository.getSetting(SettingsKey.CURRENT_CHARGE_EVENT)?.lValue
            Log.i("ChargeEventViewModel refresh", "activeChargeId = $activeChargeId")
            if (activeChargeId != null) {
                _activeChargeEvent = chargeEventRepository.getLiveChargeEventWithId(activeChargeId)
                Log.i(
                    "ChargeEventViewModel refresh",
                    "_activeChargeEvent = ${_activeChargeEvent.value}"
                )
            }
        }
    }*/

    /**
     * Abort the current charge event, deleting the row from the ChargeEvent table, and clearing the
     * CURRENT_CHARGE_EVENT setting
     */
    fun abortCharging() {
        Log.i("ChargeEventViewModel", "Aborting charge event ${_activeChargeEvent.value}")
        viewModelScope.launch {
            _activeChargeEvent.value?.apply {
                chargeEventRepository.deleteChargeEvent(this.id)
                settingsRepository.clear(SettingsKey.CURRENT_CHARGE_EVENT)
            }
        }
    }

}

class MainMenuViewModelFactory(private val application: AmberApplication) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainMenuViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainMenuViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class ${modelClass.canonicalName}")
    }
}