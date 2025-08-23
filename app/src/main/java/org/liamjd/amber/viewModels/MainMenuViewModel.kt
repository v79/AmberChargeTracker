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

            selectedVehicleId?.let {
                vehicle.value = vehicleRepository.getVehicleById(it)
                Log.i("MainMenuViewModel","looked for vehicle with id=$it, found $vehicle")
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
     * CURRENT_CHARGE_EVENT setting
     */
    fun abortCharging() {
        Log.i("ChargeEventViewModel", "Aborting charge event ${_activeChargeEvent.value}")
        viewModelScope.launch {
            _activeChargeEvent.value?.apply {
                chargeEventRepository.deleteChargeEvent(this.id)
                settingsRepository.clear(SettingsKey.CURRENT_CHARGE_EVENT)
                _activeChargeEvent.value = null
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