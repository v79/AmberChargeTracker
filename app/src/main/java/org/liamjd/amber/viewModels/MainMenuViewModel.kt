package org.liamjd.amber.viewModels

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.liamjd.amber.AmberApplication
import org.liamjd.amber.R
import org.liamjd.amber.db.entities.ChargeEvent
import org.liamjd.amber.db.entities.SettingsKey
import org.liamjd.amber.db.repositories.ChargeEventRepository
import org.liamjd.amber.db.repositories.SettingsRepository
import org.liamjd.amber.db.repositories.VehicleRepository

class MainMenuViewModel(application: AmberApplication) : ViewModel() {

    private val vehicleRepository: VehicleRepository = application.vehicleRepo
    private val settingsRepository: SettingsRepository = application.settingsRepo
    private val chargeEventRepository: ChargeEventRepository = application.chargeEventRepo

    private var _selectedVehicle: Long? = null

    private lateinit var _vehicleCount: LiveData<Int>

    val vehicleCount: LiveData<Int>
        get() = _vehicleCount
    val selectedVehicle: Long?
        get() = _selectedVehicle

    private var _activeChargeEvent = mutableStateOf<ChargeEvent?>(null)
    val activeChargeEvent
        get() = _activeChargeEvent

    init {
        viewModelScope.launch {
            _vehicleCount = vehicleRepository.getVehicleCount()
            _selectedVehicle = settingsRepository.getSetting(SettingsKey.SELECTED_VEHICLE)?.lValue
           val activeChargeId  = settingsRepository.getSetting(SettingsKey.CURRENT_CHARGE_EVENT)?.lValue
            if(activeChargeId != null) {
                _activeChargeEvent.value = chargeEventRepository.getChargeEventWithId(activeChargeId)
            }
        }
    }

    fun abortCharging() {
        Log.e("ChargeEventViewModel","Abort Charging not written yet")
        TODO("Abort charging not written yet")
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