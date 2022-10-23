package org.liamjd.amber.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.liamjd.amber.AmberApplication
import org.liamjd.amber.R
import org.liamjd.amber.db.repositories.VehicleRepository

class MainMenuViewModel(application: AmberApplication) : ViewModel() {

    private val vehicleRepository: VehicleRepository = application.vehicleRepo

    private val _selectedVehicle = application.getConfigLong(R.string.CONFIG_selected_vehicle_id)

    private lateinit var _vehicleCount: LiveData<Int>

    val vehicleCount: LiveData<Int>
        get() = _vehicleCount

    init {
        viewModelScope.launch {
            _vehicleCount = vehicleRepository.getVehicleCount()
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