package org.liamjd.amber.viewModels

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.*
import kotlinx.coroutines.*
import org.liamjd.amber.AmberApplication
import org.liamjd.amber.R
import org.liamjd.amber.db.entities.ChargeEvent
import org.liamjd.amber.db.repositories.ChargeEventRepository
import org.liamjd.amber.db.repositories.VehicleRepository
import org.liamjd.amber.screens.Screen
import org.liamjd.amber.screens.state.UIState
import java.time.LocalDateTime

class ChargeEventViewModel(application: AmberApplication) : ViewModel() {

    private val chargeEventRepository: ChargeEventRepository = application.chargeEventRepo
    private val vehicleRepository: VehicleRepository = application.vehicleRepo
    private val _selectedVehicle = application.getConfigLong(R.string.CONFIG_selected_vehicle_id)
    private val _uiState = mutableStateOf<UIState>(UIState.Loading)
    val uiState: State<UIState>
        get() = _uiState

    val odo = liveData {
        val initOdo = vehicleRepository.getCurrentOdometer(_selectedVehicle)
        emit(initOdo)
        _uiState.value = UIState.Active
    }

    val chargingStatus = mutableStateOf(RecordChargingStatus.NOT_STARTED)
    private var chargeStartTime = LocalDateTime.now()
    private var chargeEndTime = LocalDateTime.now()

    /**
     * Launching a new coroutine to insert data in a non-blocking way
     * Inserts a new ChargeEvent record, and if the new odometer reading is higher than that stored for the vehicle,
     * update the value on the vehicle too
     */
    fun insert(chargeEvent: ChargeEvent) = viewModelScope.launch {
        _uiState.value = UIState.Saving
        chargeEventRepository.insert(chargeEvent)
        val vehicleCurrentOdo = vehicleRepository.getCurrentOdometer(chargeEvent.vehicleId)
        if (chargeEvent.odometer > vehicleCurrentOdo) {
            Log.i(
                "ChargeEventViewModel",
                "Updating odometer reading for vehicle ${chargeEvent.vehicleId} from $vehicleCurrentOdo to ${chargeEvent.odometer}"
            )
            vehicleRepository.updateOdometer(chargeEvent.vehicleId, chargeEvent.odometer)
        }
        _uiState.value = UIState.Navigating(Screen.ChargeHistoryScreen)
    }

    /**
     * When the user clicks the start button:
     * - set chargingActive to true
     * - save the interim charging event to the database
     * - display timer
     */
    fun startCharging() {
        chargingStatus.value = RecordChargingStatus.CHARGING
        chargeStartTime = LocalDateTime.now()
    }

    /**
     * When the user clicks the stop button:
     * - set chargingActive to false (maybe?)
     * - stop the timer
     * - set chargingEnded to true
     */
    fun stopCharging() {
        chargingStatus.value = RecordChargingStatus.FINISHED
        chargeEndTime = LocalDateTime.now()
    }
}

enum class RecordChargingStatus {
    NOT_STARTED {
        override fun isActive(): Boolean = false
    },
    CHARGING {
        override fun isActive(): Boolean = true
    },
    FINISHED {
        override fun isActive(): Boolean = true
    },
    CANCELLED {
        override fun isActive(): Boolean = false
    };

    abstract fun isActive(): Boolean
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

//data class ChargeEventVM(
//    val chargeDateTime: LocalDateTime = LocalDateTime.now(),
//    var odometer: String = "",
//    var batteryStartRange: String = "100",
//    var batteryStartPct: String = "50",
//    var batteryEndRange: String = "200",
//    var batteryEndPct: String = "80",
//    var chargeDuration: String = "30",
//    var minimumFee: String = "1.00",
//    var costPerKWH: String = "0.15",
//    var totalCost: String = "0",
//    var kw: Int = 22,
//    var hasError: Boolean = false
//)
