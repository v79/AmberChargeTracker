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

    private val preferences = application.applicationContext.getSharedPreferences(
        application.applicationContext.resources.getString(R.string.CONFIG), 0
    )
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

    var chargingStatus by mutableStateOf(RecordChargingStatus.NOT_STARTED)
    var chargingSeconds = mutableStateOf(0)

    var startModel = MutableLiveData(
        StartingChargeEventModel(
            LocalDateTime.now(),
            odo.value ?: 0,
            0,
            0
        )
    )
    var endModel = MutableLiveData(EndingChargeEventModel(LocalDateTime.now(), 0, 0, 0f, 0))

    /**
     * Launching a new coroutine to insert data in a non-blocking way
     * Inserts a new ChargeEvent record, and if the new odometer reading is higher than that stored for the vehicle,
     * update the value on the vehicle too
     */
    @Deprecated("for old model", replaceWith = ReplaceWith("saveCharge"))
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
    fun startCharging(startModel: StartingChargeEventModel) {
        chargingStatus = RecordChargingStatus.CHARGING

        viewModelScope.launch {
            _uiState.value = UIState.Saving
            val vehicleCurrentOdo = vehicleRepository.getCurrentOdometer(_selectedVehicle)
//            if (startModel.odometer > vehicleCurrentOdo) {
            Log.i(
                "ChargeEventViewModel",
                "Updating odometer reading for vehicle $_selectedVehicle from $vehicleCurrentOdo to ${startModel.odometer}"
            )
            vehicleRepository.updateOdometer(_selectedVehicle, startModel.odometer)
//            }
            val eventId = chargeEventRepository.startChargeEvent(
                vehicleId = _selectedVehicle,
                startOdo = startModel.odometer,
                startTime = startModel.dateTime,
                startBatteryPct = startModel.percentage,
                startBatteryRange = startModel.range
            )
            with(preferences.edit()) {
                putLong("org.liamjd.amber.CURRENT_CHARGE_EVENT", eventId)
                apply()
            }
            _uiState.value = UIState.Active
        }
    }

    /**
     * When the user clicks the stop button:
     * - set chargingActive to false (maybe?)
     * - stop the timer
     * - set chargingEnded to true
     */
    fun stopCharging() {
        chargingStatus = RecordChargingStatus.FINISHED
    }

    /**
     * Save the final charge event into the database
     */
    fun saveCharge(endModel: EndingChargeEventModel) = viewModelScope.launch {
        _uiState.value = UIState.Saving
        viewModelScope.launch {
            val currentEvent = preferences.getLong("org.liamjd.amber.CURRENT_CHARGE_EVENT", -1)
            if (currentEvent == -1L) {
                Log.e(
                    "ChargeEventViewModel",
                    "stopCharging found invalid CURRENT_CHARGE_EVENT value in SharedPreferences"
                )
                return@launch
            }
            chargeEventRepository.completeChargeEvent(
                id = currentEvent,
                endTime = endModel.dateTime,
                endBatteryPct = endModel.percentage,
                endBatteryRange = endModel.range,
                kw = endModel.kw,
                cost = endModel.cost
            )
            with(preferences.edit()) {
                putLong("org.liamjd.amber.CURRENT_CHARGE_EVENT", -1)
                apply()
            }
        }


        _uiState.value = UIState.Navigating(Screen.ChargeHistoryScreen)
    }
}

enum class RecordChargingStatus {
    NOT_STARTED,
    CHARGING,
    FINISHED,
    CANCELLED;
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

data class StartingChargeEventModel(
    val dateTime: LocalDateTime,
    val odometer: Int,
    val range: Int,
    val percentage: Int
)

data class EndingChargeEventModel(
    val dateTime: LocalDateTime,
    val range: Int,
    val percentage: Int,
    val kw: Float,
    val cost: Int,
)

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
