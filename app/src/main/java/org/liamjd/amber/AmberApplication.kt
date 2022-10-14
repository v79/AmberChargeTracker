package org.liamjd.amber

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.liamjd.amber.db.AmberDatabase
import org.liamjd.amber.db.repositories.ChargeEventRepository

class AmberApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    // Not using DI yet, so lazy injection of repos and daos
    val database by lazy { AmberDatabase.getDatabase(this, applicationScope) }
    val chargeEventRepo by lazy { ChargeEventRepository(database.chargeEventDao()) }

}