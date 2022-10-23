package org.liamjd.amber.db.entities

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.liamjd.amber.toIntOrZero

@Entity
data class ChargeEvent(
    val odometer: Int,
    val batteryStartingRange: Int,
    val batteryEndingRange: Int,
    val batteryStartingPct: Int,
    val batteryEndingPct: Int,
    val vehicleId: Long
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    constructor(
        odometer: Int,
        batteryStartingRange: String,
        batteryEndingRange: String,
        batteryStartingPct: String,
        batteryEndingPct: String,
        vehicleId: Long
    ) : this(
        odometer = odometer,
        batteryStartingRange = batteryStartingRange.toIntOrZero(),
        batteryEndingRange = batteryEndingRange.toIntOrZero(),
        batteryStartingPct = batteryStartingPct.toIntOrZero(),
        batteryEndingPct = batteryEndingPct.toIntOrZero(),
        vehicleId = vehicleId
    )
}

@Dao
interface ChargeEventDao {

    @Insert
    suspend fun insert(chargeEvent: ChargeEvent)

    @Delete
    suspend fun delete(chargeEvent: ChargeEvent)

    @Query("DELETE FROM ChargeEvent")
    suspend fun deleteAll()

    @Query("SELECT * FROM ChargeEvent")
    fun getAll(): Flow<List<ChargeEvent>>
}