package org.liamjd.amber.viewModels

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.liamjd.amber.AmberApplication
import org.liamjd.amber.R
import org.liamjd.amber.db.entities.ChargeEvent
import org.liamjd.amber.db.repositories.ChargeEventRepository
import org.liamjd.amber.db.repositories.VehicleRepository
import org.liamjd.amber.screens.Screen
import org.liamjd.amber.screens.state.UIState

class ChargeEventViewModel(application: AmberApplication) : ViewModel() {

    private val chargeEventRepository: ChargeEventRepository = application.chargeEventRepo
    private val vehicleRepository: VehicleRepository = application.vehicleRepo
    private val _selectedVehicle = application.getConfigLong(R.string.CONFIG_selected_vehicle_id)
    private val _uiState = mutableStateOf<UIState>(UIState.Loading)
    val uiState: State<UIState>
        get() = _uiState

    val odo = liveData {
        val initOdo = vehicleRepository.getCurrentOdometer(_selectedVehicle)
        Log.e("ChargeEventViewModel","Getting a LIVEDATA version of odo meter via emit $initOdo")
        emit(initOdo)
        delay(1_000L)
        _uiState.value = UIState.Active
    }

    /**
     * Launching a new coroutine to insert data in a non-blocking way
     * Inserts a new ChargeEvent record, and if the new odometer reading is higher than that stored for the vehicle,
     * update the value on the vehicle too
     */
    fun insert(chargeEvent: ChargeEvent) = viewModelScope.launch {
        chargeEventRepository.insert(chargeEvent)
        val vehicleCurrentOdo = vehicleRepository.getCurrentOdometer(chargeEvent.vehicleId)
        if(chargeEvent.odometer > vehicleCurrentOdo) {
            Log.i("ChargeEventViewModel","Updating odometer reading for vehicle ${chargeEvent.vehicleId} from $vehicleCurrentOdo to ${chargeEvent.odometer}")
            vehicleRepository.updateOdometer(chargeEvent.vehicleId,chargeEvent.odometer)
        }
        _uiState.value = UIState.Navigating(Screen.ChargeHistoryScreen)
    }

}

/**
 * Because the ViewModel has a dependency on the repository, and I am not using Hilt for DI, I need this factory
 * https://developer.android.com/topic/libraries/architecture/viewmodel#kotlin
 */
class ChargeEventVMFactory(private val application: AmberApplication) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ChargeEventViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChargeEventViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class ${modelClass.canonicalName}")
    }
}
