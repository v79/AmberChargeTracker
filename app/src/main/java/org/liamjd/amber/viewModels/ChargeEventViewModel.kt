package org.liamjd.amber.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.liamjd.amber.AmberApplication
import org.liamjd.amber.db.entities.ChargeEvent
import org.liamjd.amber.db.repositories.ChargeEventRepository

class ChargeEventViewModel(application: AmberApplication) : ViewModel() {

    private val repository: ChargeEventRepository

    init {
        repository = application.chargeEventRepo
    }
    /**
     * Launching a new coroutine to insert data in a non-blocking way
     */
    fun insert(chargeEvent: ChargeEvent) = viewModelScope.launch {
        repository.insert(chargeEvent)
    }
}

/**
 * Because the ViewModel has a depedency on the repository, and I am not using Hilt for DI, I need this factory
 * https://developer.android.com/topic/libraries/architecture/viewmodel#kotlin
 */
class ChargeEventVMFactory(private val application: AmberApplication) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ChargeEventViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChargeEventViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
