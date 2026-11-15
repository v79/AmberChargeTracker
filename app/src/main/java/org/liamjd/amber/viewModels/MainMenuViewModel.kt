package org.liamjd.amber.viewModels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.liamjd.amber.AmberApplication
import org.liamjd.amber.db.entities.ChargeEvent
import org.liamjd.amber.db.entities.Setting
import org.liamjd.amber.db.entities.SettingsKey
import org.liamjd.amber.db.entities.Vehicle
import org.liamjd.amber.db.repositories.ChargeEventRepository
import org.liamjd.amber.db.repositories.SettingsRepository
import org.liamjd.amber.db.repositories.VehicleRepository
import org.liamjd.amber.notifications.ChargingNotificationHelper

class MainMenuViewModel(application: AmberApplication) : ViewModel() {

    private val vehicleRepository: VehicleRepository = application.vehicleRepo
    private val settingsRepository: SettingsRepository = application.settingsRepo
    private val chargeEventRepository: ChargeEventRepository = application.chargeEventRepo
    private val notificationHelper = ChargingNotificationHelper(application.applicationContext)

    var selectedVehicleId: Long? = null

    private var _vehicleCount: LiveData<Int> = MutableLiveData()
    val vehicleCount: LiveData<Int>
        get() = _vehicleCount

    var vehicle = mutableStateOf<Vehicle?>(null)

    // Use a MediatorLiveData to observe the ChargeEvent from the database
    private val _activeChargeEvent = MediatorLiveData<ChargeEvent?>()
    val activeChargeEvent: LiveData<ChargeEvent?>
        get() = _activeChargeEvent

    private var _isCharging: LiveData<Boolean> = MutableLiveData(false)

    init {
        viewModelScope.launch {
            Log.i("MainMenuViewModel", "init{}")
            _vehicleCount = vehicleRepository.getVehicleCount()
            selectedVehicleId = settingsRepository.getSettingLongValue(SettingsKey.SELECTED_VEHICLE)

            vehicle.value = getSelectedVehicle(selectedVehicleId)

            selectedVehicleId?.let {
                try {
                    vehicle.value = vehicleRepository.getVehicleById(it)
                    Log.i("MainMenuViewModel", "looked for vehicle with id=$it, found $vehicle")
                } catch (ise: IllegalStateException) {
                    Log.e(
                        "MainMenuViewModel",
                        "Tried to load vehicle $it but the vehicle was not found"
                    )
                    Log.e("MainMenuViewModel", ise.message ?: "")
                }
            }

            val activeChargeId =
                settingsRepository.getSettingLongValue(SettingsKey.CURRENT_CHARGE_EVENT)
            Log.i("MainMenuViewModel", "activeChargeId = $activeChargeId")
            if (activeChargeId != null) {
                // Observe the ChargeEvent from the database and update the MediatorLiveData
                val dbLiveData = chargeEventRepository.getLiveChargeEventWithId(activeChargeId)
                _activeChargeEvent.addSource(dbLiveData) { event ->
                    _activeChargeEvent.value = event
                }
            } else {
                _activeChargeEvent.value = null
            }
        }
    }

    /**
     * Abort the current charge event, deleting the row from the ChargeEvent table, and clearing the
     * CURRENT_CHARGE_EVENT setting, and canceling the notification
     */
    fun abortCharging() {
        Log.i("ChargeEventViewModel", "Aborting charge event ${_activeChargeEvent.value}")
        viewModelScope.launch {
            _activeChargeEvent.value?.apply {
                chargeEventRepository.deleteChargeEvent(this.id)
                settingsRepository.clear(SettingsKey.CURRENT_CHARGE_EVENT)
                notificationHelper.cancelChargingNotification()
                _activeChargeEvent.value = null
            }
        }
    }

    /**
     * Load the currently selected vehicle. There's additional logic to handle error conditions, such as the selected vehicle
     * not existing in the database. In which case, it just tries to load the first vehicle in the database.
     * If it does, it will update the SELECTED_VEHICLE setting to that vehicle, or NULL if there are no vehicles at all.
     */
    private suspend fun getSelectedVehicle(id: Long?): Vehicle? {
        if (id == null) {
            return null
        }
        var vehicle: Vehicle?
        try {
            vehicle = vehicleRepository.getVehicleById(id)
            Log.i("MainMenuViewModel", "looked for vehicle with id=$id, found $vehicle")
        } catch (ise: IllegalStateException) {
            Log.e(
                "MainMenuViewModel",
                "Tried to load vehicle $id but the vehicle was not found. Finding the first vehicle."
            )
            Log.e("MainMenuViewModel", ise.message ?: "")
            val vehicleCount = vehicleRepository.getVehicleCount().value
            try {
                vehicle = vehicleRepository.getAllVehicles().first().first()
            } catch (nse: NoSuchFileException) {
                Log.e(
                    "MainMenuViewModel",
                    "Tried to find any active vehicle but none found. This is unexpected as there are $vehicleCount vehicles."
                )
                vehicle = null
            }
        }
        // Update the SELECTED_VEHICLE value as it might have changed in the error conditions above
        vehicle?.let {
            settingsRepository.update(Setting(key = SettingsKey.SELECTED_VEHICLE, lValue = it.id))
        }
        return vehicle
    }

}

class MainMenuViewModelFactory(private val application: AmberApplication) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainMenuViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return MainMenuViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class ${modelClass.canonicalName}")
    }
}