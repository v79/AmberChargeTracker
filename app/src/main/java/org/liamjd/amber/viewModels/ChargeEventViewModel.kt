package org.liamjd.amber.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.liamjd.amber.db.entities.ChargeEvent
import org.liamjd.amber.db.repositories.ChargeEventRepository

class ChargeEventViewModel(private val repository: ChargeEventRepository) : ViewModel() {

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
class ChargeEventVMFactory(private val repository: ChargeEventRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ChargeEventViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChargeEventViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}