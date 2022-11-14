package org.liamjd.amber.db.entities

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update

/**
 * For example Setting(key = "selectedVehicleId", sValue = null, iValue = null, lValue = 12L)
 */
@Entity
data class Setting(
    val settingsKey: String,
    val sValue: String? = null,
    val iValue: Int? = null,
    val lValue: Long? = null
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    constructor(
        key: SettingsKey,
        sValue: String? = null,
        iValue: Int? = null,
        lValue: Long? = null
    ) : this(settingsKey = key.keyString, sValue, iValue, lValue)
}

/**
 * Type-safe way of referring to settings strings, without the overhead of the DB enum lookup?
 */
enum class SettingsKey(val keyString: String) {
    SELECTED_VEHICLE("SELECTED_VEHICLE"),
    CURRENT_CHARGE_EVENT("CURRENT_CHARGE_EVENT")
}

@Dao
interface SettingsDao {

    @Insert
    suspend fun insert(setting: Setting)

    @Delete
    fun delete(setting: Setting)

    @Update
    suspend fun update(setting: Setting)

    @Query("SELECT * FROM Setting WHERE settingsKey = :key LIMIT 1")
    suspend fun getSetting(key: String): Setting?

    @Query("UPDATE Setting SET sValue = NULL, iValue = NULL, lValue = NULL WHERE settingsKey = :key")
    suspend fun clearSetting(key: String)

    // urgh
    @Query("UPDATE Setting SET lValue = :lValue WHERE settingsKey = :key")
    suspend fun updateLongValue(key: String, lValue: Long?)
    @Query("UPDATE Setting SET sValue = :sValue WHERE settingsKey = :key")
    suspend fun updateStringValue(key: String, sValue: String?)
    @Query("UPDATE Setting SET iValue = :iValue WHERE settingsKey = :key")
    suspend fun updateIntValue(key: String, iValue: Int?)

}