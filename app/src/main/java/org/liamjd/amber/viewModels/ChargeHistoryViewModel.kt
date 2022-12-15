package org.liamjd.amber.viewModels

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.liamjd.amber.AmberApplication
import org.liamjd.amber.db.entities.SettingsKey
import org.liamjd.amber.db.entities.Vehicle
import org.liamjd.amber.db.repositories.ChargeEventRepository
import org.liamjd.amber.db.repositories.SettingsRepository
import org.liamjd.amber.db.repositories.VehicleRepository

class ChargeHistoryViewModel(application: AmberApplication) : ViewModel() {

    private val chargeEventRepository: ChargeEventRepository = application.chargeEventRepo
    private val vehicleRepository: VehicleRepository = application.vehicleRepo
    private val settingsRepository: SettingsRepository = application.settingsRepo

    private var _selectedVehicleId: Long = -1L
    var vehicle: LiveData<Vehicle> = MutableLiveData()

    val allEvents = chargeEventRepository.allChargeEvents.asLiveData()

    fun getEventsWithin(days: Int) = chargeEventRepository.getEventsWithin(days, _selectedVehicleId).asLiveData()

    fun eventsForVehicle(vehicleId: Long) = chargeEventRepository.getAllEventsForVehicle(vehicleId).asLiveData()

    init {
        refreshView()
    }

    private fun refreshView() {
        viewModelScope.launch {
            _selectedVehicleId = settingsRepository.getSetting(SettingsKey.SELECTED_VEHICLE)?.lValue?: -1L
            _selectedVehicleId.let {
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