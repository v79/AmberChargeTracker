package org.liamjd.amber.db.repositories

import android.util.Log
import org.liamjd.amber.db.entities.Setting
import org.liamjd.amber.db.entities.SettingsDao
import org.liamjd.amber.db.entities.SettingsKey

class SettingsRepository(private val settingsDao: SettingsDao) {

    suspend fun insert(setting: Setting) = settingsDao.insert(setting)

    fun delete(setting: Setting) = settingsDao.delete(setting)

//    fun getSetting(key: SettingsKey): LiveData<Setting?> = settingsDao.getSetting(key.keyString)

    /**
     * Update or insert ("upsert")
     */
    suspend fun update(setting: Setting) {
        Log.i("SettingsRepository","Updating setting: $setting")
        val existingSetting = settingsDao.getSetting(setting.settingsKey)
        if (existingSetting == null) {
            Log.e("SetttingsRepository","Inserting new ${setting.settingsKey} as none found")
            settingsDao.insert(setting)
        } else {
            if(valuesChanged(setting,existingSetting)) {
                Log.i(
                    "SetttingsRepository",
                    "Updating ${setting.settingsKey} with new values ($setting)"
                )
                settingsDao.clearSetting(setting.settingsKey)
                // urgh
                settingsDao.updateLongValue(setting.settingsKey, setting.lValue)
                settingsDao.updateIntValue(setting.settingsKey, setting.iValue)
                settingsDao.updateStringValue(setting.settingsKey, setting.sValue)
            } else {
                Log.i("SettingsRepository","No changes required for $setting")
            }
        }
    }

    /**
     * Get the Long value for the given settingsKey
     * Or return null if not found
     */
    suspend fun getSettingLongValue(key: SettingsKey): Long? {
        val setting = settingsDao.getSetting(key.keyString)
        Log.i("SettingsRepository","getSettingLongValue($key) returns $setting")
        return setting?.lValue
    }

    /**
     * Compare the values of the new and old settings, and return true if any of them have changed
     */
    private fun valuesChanged(newSetting: Setting,oldSetting: Setting): Boolean {
        return (newSetting.lValue != oldSetting.lValue) || (newSetting.iValue != oldSetting.iValue) || (newSetting.sValue != oldSetting.sValue)
    }

    /**
     * Remove all the stored values for the given setting.
     * This does not delete the row in the database, but just resets the values to null
     */
    suspend fun clear(key: SettingsKey) = settingsDao.clearSetting(key.keyString)
}