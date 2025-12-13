package org.liamjd.amber

import android.app.Application
import android.content.Context
import androidx.annotation.StringRes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.liamjd.amber.db.AmberDatabase
import org.liamjd.amber.db.entities.SettingsKey
import org.liamjd.amber.db.repositories.ChargeEventRepository
import org.liamjd.amber.db.repositories.SettingsRepository
import org.liamjd.amber.db.repositories.VehicleRepository
import org.liamjd.amber.notifications.ChargingNotificationHelper

class AmberApplication : Application() {

    val applicationScope = CoroutineScope(SupervisorJob())

    // Not using DI yet, so lazy injection of repos and daos
    private val database by lazy { AmberDatabase.getDatabase(this, applicationScope) }
    val chargeEventRepo by lazy { ChargeEventRepository(database.chargeEventDao()) }
    val vehicleRepo by lazy { VehicleRepository(database.vehicleDao()) }
    val settingsRepo by lazy { SettingsRepository(database.settingsDao()) }

    override fun onCreate() {
        super.onCreate()
        checkAndShowActiveChargingNotification()
    }

    /**
     * Check if there's an active charging session and show notification if needed
     */
    private fun checkAndShowActiveChargingNotification() {
        applicationScope.launch {
            val activeChargeId = settingsRepo.getSettingLongValue(SettingsKey.CURRENT_CHARGE_EVENT)
            if (activeChargeId != null) {
                val notificationHelper = ChargingNotificationHelper(applicationContext)
                notificationHelper.showChargingNotification()
            }
        }
    }

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