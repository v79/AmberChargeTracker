package org.liamjd.amber.db.repositories

import android.util.Log
import org.liamjd.amber.db.entities.Setting
import org.liamjd.amber.db.entities.SettingsDao
import org.liamjd.amber.db.entities.SettingsKey

class SettingsRepository(private val settingsDao: SettingsDao) {

    suspend fun insert(setting: Setting) = settingsDao.insert(setting)

    fun delete(setting: Setting) = settingsDao.delete(setting)

    suspend fun getSetting(key: SettingsKey): Setting? = settingsDao.getSetting(key.keyString)

    /**
     * Update or insert ("upsert")
     */
    suspend fun update(setting: Setting) {
        Log.i("SettingsRepository","Updating setting: $setting")
        val existingSetting = settingsDao.getSetting(setting.settingsKey)
        if (existingSetting == null) {
            Log.i("SetttingsRepository","Inserting new ${setting.settingsKey} as none found")
            settingsDao.insert(setting)
        } else {
            Log.i("SetttingsRepository","Updating ${setting.settingsKey} with new values ($setting)")
            settingsDao.clearSetting(setting.settingsKey)
            // urgh
            settingsDao.updateLongValue(setting.settingsKey,setting.lValue)
            settingsDao.updateIntValue(setting.settingsKey,setting.iValue)
            settingsDao.updateStringValue(setting.settingsKey,setting.sValue)
        }
    }

    suspend fun clear(key: SettingsKey) = settingsDao.clearSetting(key.keyString)
}