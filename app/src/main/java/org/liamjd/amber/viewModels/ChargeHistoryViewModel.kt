package org.liamjd.amber.viewModels

import androidx.lifecycle.*
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

    var selectedVehicleId: Long? = null
    var vehicle: Vehicle? = null

    val allEvents = chargeEventRepository.allChargeEvents.asLiveData()

//    fun getEventsWithin(days: Int) = chargeEventRepository.getEventsWithin(days,selectedVehicleId).asLiveData()
    fun getEventsWithin(days: Int) = MutableLiveData<List<ChargeEvent>>() // TODO

    fun eventsForVehicle(vehicleId: Long) = chargeEventRepository.getAllEventsForVehicle(vehicleId).asLiveData()

    init {
        refreshView()
    }

    private fun refreshView() {
        viewModelScope.launch {
            selectedVehicleId = settingsRepository.getSettingLongValue(SettingsKey.SELECTED_VEHICLE)
            selectedVehicleId?.let {
                vehicle = vehicleRepository.getVehicleById(it)
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