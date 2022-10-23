package org.liamjd.amber.viewModels

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.liamjd.amber.AmberApplication
import org.liamjd.amber.R
import org.liamjd.amber.db.entities.Vehicle
import org.liamjd.amber.db.repositories.VehicleRepository

class VehicleDetailsViewModel(application: AmberApplication) : ViewModel() {

    private val repository: VehicleRepository = application.vehicleRepo
    private val preferences = application.applicationContext.getSharedPreferences(
        application.applicationContext.resources.getString(R.string.CONFIG), 0
    )
    lateinit var selectedVehicle: LiveData<Vehicle?>

    private var _selectedVehicleId: MutableLiveData<Long> = MutableLiveData<Long>()


    init {
        viewModelScope.launch(Dispatchers.IO) {
            val mostRecentVehicleId = repository.getMostRecentVehicleId()
            mostRecentVehicleId?.let {
                _selectedVehicleId.postValue(it)
                selectedVehicle = repository.getVehicleById(it)
            }
        }
        _selectedVehicleId.postValue(
            preferences.getLong(
                application.resources.getString(R.string.CONFIG_selected_vehicle_id),
                -1L
            )
        )
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
            with(preferences.edit()) {
                putLong("org.liamjd.amber.SELECTED_VEHICLE", primaryKey)
                apply()
            }
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