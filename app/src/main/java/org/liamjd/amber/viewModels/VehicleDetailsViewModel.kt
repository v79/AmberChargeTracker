package org.liamjd.amber.viewModels

import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.liamjd.amber.AmberApplication
import org.liamjd.amber.db.entities.Vehicle
import org.liamjd.amber.db.repositories.VehicleRepository

class VehicleDetailsViewModel(application: AmberApplication) : ViewModel() {


    private val repository: VehicleRepository = application.vehicleRepo

    lateinit var selectedVehicle: LiveData<Vehicle?>

    private var _selectedVehicleId: MutableLiveData<Long> = MutableLiveData<Long>().also {
        viewModelScope.launch(Dispatchers.IO) {
            val vehicleId = repository.getMostRecentVehicleId()
            it.postValue(vehicleId)
        }
    }

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val mostRecentVehicleId = repository.getMostRecentVehicleId()
            mostRecentVehicleId?.let {
                _selectedVehicleId.postValue(mostRecentVehicleId)
                selectedVehicle = repository.getVehicleById(mostRecentVehicleId)
            }
        }
    }

    var vehicleCount: LiveData<Int> = repository.getVehicleCount()

    fun insert(vehicle: Vehicle) {
        viewModelScope.launch(Dispatchers.IO) {
            val primaryKey = repository.insert(vehicle)
            _selectedVehicleId.postValue(primaryKey)
            selectedVehicle = repository.getVehicleById(primaryKey)
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