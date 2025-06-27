package org.liamjd.amber.viewModels

import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TimerViewModel : ViewModel() {
    private val _timer = MutableStateFlow(0L)
    val timer = _timer.asStateFlow()

    private var timerJob: Job? = null
    private var startTime: Long? = null
    private var pausedTime: Long? = null
    private var offsetSeconds: Long = 0L

    fun startTimer(startingSeconds: Long) {
        timerJob?.cancel()
        offsetSeconds = startingSeconds
        startTime = SystemClock.elapsedRealtime()
        timerJob = viewModelScope.launch {
            while (true) {
                val elapsed = ((SystemClock.elapsedRealtime() - (startTime ?: SystemClock.elapsedRealtime())) / 1000) + offsetSeconds
                _timer.value = elapsed
                delay(1000)
            }
        }
    }

    fun pauseTimer() {
        timerJob?.cancel()
        pausedTime = SystemClock.elapsedRealtime()
        // Save the current timer value as offset
        offsetSeconds = _timer.value
    }

    fun stopTimer() {
        _timer.value = 0
        timerJob?.cancel()
        startTime = null
        pausedTime = null
        offsetSeconds = 0L
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

class TimerViewModelFactory :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TimerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TimerViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class ${modelClass.canonicalName}")
    }
}