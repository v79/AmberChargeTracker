package org.liamjd.amber.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import org.liamjd.amber.AmberApplication
import org.liamjd.amber.db.repositories.ChargeEventRepository

class ChargeHistoryViewModel(application: AmberApplication) : ViewModel() {

    private val repository: ChargeEventRepository = application.chargeEventRepo

    public val allEvents = repository.allChargeEvents.asLiveData()
}

class ChargeHistoryViewModelFactory(private val application: AmberApplication) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ChargeHistoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ChargeHistoryViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class ${modelClass.canonicalName}")
    }
}