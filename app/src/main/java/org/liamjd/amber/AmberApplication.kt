package org.liamjd.amber

import android.app.Application
import android.content.Context
import androidx.annotation.StringRes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.liamjd.amber.db.AmberDatabase
import org.liamjd.amber.db.repositories.ChargeEventRepository
import org.liamjd.amber.db.repositories.SettingsRepository
import org.liamjd.amber.db.repositories.VehicleRepository

class AmberApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    // Not using DI yet, so lazy injection of repos and daos
    private val database by lazy { AmberDatabase.getDatabase(this, applicationScope) }
    val chargeEventRepo by lazy { ChargeEventRepository(database.chargeEventDao()) }
    val vehicleRepo by lazy { VehicleRepository(database.vehicleDao()) }
    val settingsRepo by lazy { SettingsRepository(database.settingsDao()) }

    /**
     * Get a Long value from the shared preferences with the given key
     * @param key String resource key to find
     * @return long value of the key, or -1 if not found
     */
    fun getConfigLong(@StringRes key: Int): Long {
        return this.applicationContext.getSharedPreferences(
            this.applicationContext.resources.getString(
                R.string.CONFIG
            ), Context.MODE_PRIVATE
        ).getLong(this.applicationContext.resources.getString(key), -1L)
    }

}