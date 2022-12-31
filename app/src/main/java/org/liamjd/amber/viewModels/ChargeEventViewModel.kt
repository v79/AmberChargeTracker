package org.liamjd.amber.viewModels

import android.content.Context
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.*
import kotlinx.coroutines.*
import org.liamjd.amber.AmberApplication
import org.liamjd.amber.R
import org.liamjd.amber.db.entities.ChargeEvent
import org.liamjd.amber.db.entities.Setting
import org.liamjd.amber.db.entities.SettingsKey
import org.liamjd.amber.db.repositories.ChargeEventRepository
import org.liamjd.amber.db.repositories.SettingsRepository
import org.liamjd.amber.db.repositories.VehicleRepository
import org.liamjd.amber.screens.Screen
import org.liamjd.amber.screens.state.UIState
import java.time.LocalDateTime
import java.time.ZoneOffset

class ChargeEventViewModel(application: AmberApplication, private val activeChargeId: Long?) :
    ViewModel() {

    private val preferences = application.applicationContext.getSharedPreferences(
        application.applicationContext.resources.getString(R.string.CONFIG), Context.MODE_PRIVATE
    )
    private val chargeEventRepository: ChargeEventRepository = application.chargeEventRepo
    private val vehicleRepository: VehicleRepository = application.vehicleRepo
    private val settingsRepository: SettingsRepository = application.settingsRepo


    private var _selectedVehicleId: Long? = -1L


    private val _uiState = mutableStateOf<UIState>(UIState.Loading)
    val uiState: State<UIState>
        get() = _uiState

    val odo = mutableStateOf(0)

    var chargingStatus by mutableStateOf(RecordChargingStatus.NOT_STARTED)
    var chargingSeconds = mutableStateOf(0L)

    var startModel = MutableLiveData(
        StartingChargeEventModel(
            LocalDateTime.now(),
            odo.value,
            0,
            0
        )
    )
    var endModel = MutableLiveData(EndingChargeEventModel(LocalDateTime.now(), 0, 0, 0f, 0))

    /**
     * On init, fetch the currently selected vehicle from the database
     * It should have a value and not be -1 because we shouldn't be able to get to this screen if it is -1
     */
    init {

        Log.i("ChargeEventViewModel", "init{} Arrived with Active Charge ID = $activeChargeId")

        viewModelScope.launch {
            _selectedVehicleId =
                settingsRepository.getSettingLongValue(SettingsKey.SELECTED_VEHICLE)
            Log.i(
                "ChargeEventViewModel",
                "init{} Selected vehicle is $_selectedVehicleId"
            )

            _selectedVehicleId?.let {
                odo.value = vehicleRepository.getCurrentOdometer(it)
            }

            if (activeChargeId != null) {
                Log.i(
                    "ChargeEventViewModel init",
                    "Looking for charge event with ID $activeChargeId"
                )
                val existingChargeEvent = chargeEventRepository.getChargeEventWithId(activeChargeId)
                Log.i("ChargeEventViewModel init", existingChargeEvent.toString())
                chargingStatus = RecordChargingStatus.CHARGING
                val nowSeconds = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
                existingChargeEvent.apply {
                    startModel.value = StartingChargeEventModel(
                        dateTime = this.startDateTime,
                        this.odometer,
                        this.batteryStartingRange,
                        this.batteryStartingPct
                    )
                    chargingSeconds.value =
                        nowSeconds - this.startDateTime.toEpochSecond(ZoneOffset.UTC)
                }
            } else {
                Log.i(
                    "ChargeEventViewModel",
                    "{init} Building UI model for ${_selectedVehicleId}, odo reading is ${odo.value}"
                )
                startModel.value = StartingChargeEventModel(
                    dateTime = LocalDateTime.now(),
                    odometer = odo.value,
                    range = 50,
                    percentage = 25
                )
            }

            _uiState.value = UIState.Active
        }
    }

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
            _selectedVehicleId?.let { vehicleId ->
                _uiState.value = UIState.Saving
                val vehicleCurrentOdo = vehicleRepository.getCurrentOdometer(vehicleId)
//            if (startModel.odometer > vehicleCurrentOdo) {
                Log.i(
                    "ChargeEventViewModel",
                    "Updating odometer reading for vehicle $vehicleId from $vehicleCurrentOdo to ${startModel.odometer}"
                )
                vehicleRepository.updateOdometer(vehicleId, startModel.odometer)
//            }
                val eventId = chargeEventRepository.startChargeEvent(
                    vehicleId = vehicleId,
                    startOdo = startModel.odometer,
                    startTime = startModel.dateTime,
                    startBatteryPct = startModel.percentage,
                    startBatteryRange = startModel.range
                )
                settingsRepository.update(
                    Setting(
                        SettingsKey.CURRENT_CHARGE_EVENT,
                        lValue = eventId
                    )
                )
                _uiState.value = UIState.Active
            }
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

        val currentEvent: Long? =
            settingsRepository.getSettingLongValue(SettingsKey.CURRENT_CHARGE_EVENT)
        if (currentEvent == null) {
            Log.e(
                "ChargeEventViewModel",
                "stopCharging found invalid CURRENT_CHARGE_EVENT value in database"
            )
            return@launch
        } else {
            chargeEventRepository.completeChargeEvent(
                id = currentEvent,
                endTime = endModel.dateTime,
                endBatteryPct = endModel.percentage,
                endBatteryRange = endModel.range,
                kw = endModel.kw,
                cost = endModel.cost
            )
            settingsRepository.clear(SettingsKey.CURRENT_CHARGE_EVENT)
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
class ChargeEventVMFactory(
    private val application: AmberApplication,
    private val activeChargeId: String?
) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val chargeId = activeChargeId?.toLongOrNull()
        if (modelClass.isAssignableFrom(ChargeEventViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChargeEventViewModel(application, chargeId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class ${modelClass.canonicalName}")
    }
}

data class ChargeEventModel(
    val batteryStartPct: MutableState<String>,
    val batteryStartRange: MutableState<String>
)


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
