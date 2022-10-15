package org.liamjd.amber.viewModels

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import org.liamjd.amber.AmberApplication
import org.liamjd.amber.db.entities.Vehicle
import org.liamjd.amber.db.repositories.VehicleRepository

class VehicleDetailsViewModel(application: AmberApplication) : ViewModel() {

    private val repository: VehicleRepository = application.vehicleRepo

    fun insert(vehicle: Vehicle) = viewModelScope.launch {
        repository.insert(vehicle)
    }

    fun getVehicleCount(): LiveData<Int> {
        val count = MutableLiveData<Int>()
        viewModelScope.launch {
            count.postValue(repository.getVehicleCount())
        }
        return count
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