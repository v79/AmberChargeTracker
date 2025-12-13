package org.liamjd.amber.db.entities

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow
import org.liamjd.amber.toIntOrZero
import java.time.LocalDateTime

@Entity
data class ChargeEvent(
    val odometer: Int,
    val startDateTime: LocalDateTime,
    val endDateTime: LocalDateTime?,
    val batteryStartingRange: Int,
    val batteryEndingRange: Int?,
    val batteryStartingPct: Int,
    val batteryEndingPct: Int?,
    val vehicleId: Long,
    val kilowatt: Float?,
    var costPerKwH: Float?,
    var totalCost: Int? = null // in pence, or null if no value saved
) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    constructor(
        odometer: Int,
        batteryStartingRange: String,
        batteryEndingRange: String,
        batteryStartingPct: String,
        batteryEndingPct: String,
        vehicleId: Long,
        kilowatt: Float,
        totalCost: Int? = null
    ) : this(
        odometer = odometer,
        batteryStartingRange = batteryStartingRange.toIntOrZero(),
        batteryEndingRange = batteryEndingRange.toIntOrZero(),
        batteryStartingPct = batteryStartingPct.toIntOrZero(),
        batteryEndingPct = batteryEndingPct.toIntOrZero(),
        vehicleId = vehicleId,
        startDateTime = LocalDateTime.now(),
        endDateTime = LocalDateTime.now(),
        kilowatt = kilowatt,
        costPerKwH = null,
        totalCost = totalCost
    )

    /**
     * Calculate the number of miles driven since the previous charge event
     */
    fun milesSince(previousEvent: ChargeEvent): Int {
        return this.odometer - previousEvent.odometer
    }

    fun milesPerPercent(previousEvent: ChargeEvent): Float {
        val milesDriven = milesSince(previousEvent)
        val pctUsed = previousEvent.batteryEndingPct?.let {
           it - this.batteryStartingPct
        } ?: return 0f

        return if (pctUsed > 0) {
            milesDriven.toFloat() / pctUsed.toFloat()
        } else {
            999f
        }
    }
}

@Dao
interface ChargeEventDao {

    @Insert
    suspend fun insert(chargeEvent: ChargeEvent)

    @Query("INSERT INTO ChargeEvent (vehicleId, odometer, startDateTime, batteryStartingPct, batteryStartingRange) VALUES (:vehicleId, :startOdo, :startTime, :startBatteryPct, :startBatteryRange)")
    suspend fun startChargeRecord(
        vehicleId: Long,
        startOdo: Int,
        startTime: LocalDateTime,
        startBatteryPct: Int,
        startBatteryRange: Int
    ): Long

    @Query("UPDATE ChargeEvent SET endDateTime = :endDateTime, batteryEndingRange = :batteryEndingRange, batteryEndingPct = :batteryEndingPct, kilowatt = :kilowatt, totalCost = :totalCost WHERE id = :id")
    suspend fun updateChargeRecord(
        id: Long,
        endDateTime: LocalDateTime,
        batteryEndingPct: Int,
        batteryEndingRange: Int,
        kilowatt: Float,
        totalCost: Int?
    )

    @Query("UPDATE ChargeEvent SET totalCost = :totalCost WHERE id = :id")
    suspend fun updateChargeCost(
        id: Long,
        totalCost: Int?
    )

    // New: update costPerKwH
    @Query("UPDATE ChargeEvent SET costPerKwH = :costPerKwH WHERE id = :id")
    suspend fun updateChargeCostPerKwH(
        id: Long,
        costPerKwH: Float?
    )

    // New combined atomic update for both cost columns
    @Query("UPDATE ChargeEvent SET totalCost = :totalCost, costPerKwH = :costPerKwH WHERE id = :id")
    suspend fun updateChargeCosts(
        id: Long,
        totalCost: Int?,
        costPerKwH: Float?
    )

    @Delete
    suspend fun delete(chargeEvent: ChargeEvent)

    @Query("DELETE FROM ChargeEvent")
    suspend fun deleteAll()

    @Query("SELECT * FROM ChargeEvent ORDER BY startDateTime DESC")
    fun getAll(): Flow<List<ChargeEvent>>

    @Query("SELECT * FROM ChargeEvent where vehicleId = :vehicleId ORDER BY startDateTime DESC")
    fun getAllForVehicle(vehicleId: Long): Flow<List<ChargeEvent>>

    @RawQuery(observedEntities = [ChargeEvent::class])
    fun getEventsWithin(query: SupportSQLiteQuery): Flow<List<ChargeEvent>>

    @Query("SELECT * FROM ChargeEvent WHERE vehicleId = :vehicleId AND endDateTime IS NOT NULL AND startDateTime > :startDateTime ORDER BY startDateTime DESC")
    fun getEventsSince(startDateTime: Long,vehicleId: Long): Flow<List<ChargeEvent>>

    /**
     * Delete all events for the given vehicleId
     * Returns the number of deleted rows
     */
    @Query("DELETE FROM ChargeEvent WHERE vehicleId = :vehicleId")
    suspend fun deleteEventsForVehicle(vehicleId: Long): Int

    /**
     * Return a LiveData wrapper around a ChargeEvent (which may be null)
     */
    @Query("SELECT * FROM ChargeEvent WHERE id = :id")
    fun getChargeEventWithId(id: Long): LiveData<ChargeEvent?>

    /**
     * Return a charge event with the given ID
     */
    @Query("SELECT * FROM ChargeEvent WHERE id = :id")
    suspend fun getExistingChargeEventWithId(id: Long): ChargeEvent
}