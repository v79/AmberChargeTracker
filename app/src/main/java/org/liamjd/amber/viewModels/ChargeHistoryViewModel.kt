package org.liamjd.amber.viewModels

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.liamjd.amber.AmberApplication
import org.liamjd.amber.db.entities.ChargeEvent
import org.liamjd.amber.db.entities.Setting
import org.liamjd.amber.db.entities.SettingsKey
import org.liamjd.amber.db.entities.Vehicle
import org.liamjd.amber.db.repositories.ChargeEventRepository
import org.liamjd.amber.db.repositories.SettingsRepository
import org.liamjd.amber.db.repositories.VehicleRepository

class ChargeHistoryViewModel(application: AmberApplication) : ViewModel() {

    private val chargeEventRepository: ChargeEventRepository = application.chargeEventRepo
    private val vehicleRepository: VehicleRepository = application.vehicleRepo
    private val settingsRepository: SettingsRepository = application.settingsRepo

    private var selectedVehicleId: Long? = null
    var vehicle = mutableStateOf<Vehicle?>(null)

    var events = mutableStateOf<List<ChargeEvent>>(
        emptyList())

    var timePeriod = mutableStateOf(0)

    init {
        refreshView()
    }

    private fun refreshView() {
        Log.i("ChargeHistoryVM","refreshView()")
        viewModelScope.launch {
            selectedVehicleId = settingsRepository.getSettingLongValue(SettingsKey.SELECTED_VEHICLE)
            selectedVehicleId?.let { id ->
                vehicle.value = vehicleRepository.getVehicleById(id)
                chargeEventRepository.getEventsWithin(timePeriod.value,id).collect {
                   response ->
                   events.value = response
                    Log.i("ChargeHistoryVM","collecting events - ${response.size} found")
               }
            }
        }
    }

    /**
     * Update the time period and requery the database to get the matching events
     */
    fun changeTimeFilter(days: Int) {
        timePeriod.value = days
        viewModelScope.launch {
            selectedVehicleId?.let { id ->
                chargeEventRepository.getEventsWithin(timePeriod.value, id).collect { response ->
                    events.value = response
                }
            }
        }
    }
}

class ChargeHistoryViewModelFactory(private val application: AmberApplication) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ChargeHistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChargeHistoryViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class ${modelClass.canonicalName}")
    }
}