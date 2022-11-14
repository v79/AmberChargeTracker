package org.liamjd.amber.viewModels

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
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

    lateinit var selectedVehicle: LiveData<Vehicle>
    private var _selectedVehicleId: MutableLiveData<Long> = MutableLiveData<Long>()


    init {
        viewModelScope.launch(Dispatchers.IO) {
            val mostRecentVehicleId = repository.getMostRecentVehicleId()
            mostRecentVehicleId?.let {
                _selectedVehicleId.postValue(it)
                selectedVehicle = repository.getVehicleById(it)
            }

            _selectedVehicleId.postValue(
                settingsRepository.getSetting(SettingsKey.SELECTED_VEHICLE)?.lValue ?: -1L
            )

        }

    }

    var vehicleCount: LiveData<Int> = repository.getVehicleCount()

    /**
     * Insert the new vehicle and immediately set it as the currently selected vehicle, storing that value in SharedPreferences
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
        }
    }

    private fun getMostRecentVehicleId() {
        viewModelScope.launch {
            repository.getMostRecentVehicleId()  // TODO: really this should be stored elsewhere
        }
    }
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