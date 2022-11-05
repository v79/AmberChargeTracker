package org.liamjd.amber

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import org.liamjd.amber.db.repositories.ChargeEventRepository
import org.liamjd.amber.screens.Navigation
import org.liamjd.amber.viewModels.ChargeEventVMFactory
import org.liamjd.amber.viewModels.ChargeEventViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Navigation(application as AmberApplication)
        }
    }
}

