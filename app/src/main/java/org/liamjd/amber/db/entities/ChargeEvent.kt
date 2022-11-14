package org.liamjd.amber.db.entities

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
    val totalCost: Int? // in pence
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    constructor(
        odometer: Int,
        batteryStartingRange: String,
        batteryEndingRange: String,
        batteryStartingPct: String,
        batteryEndingPct: String,
        vehicleId: Long,
        kilowatt: Float,
        totalCost: Int
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
        totalCost = totalCost
    )
}

@Dao
interface ChargeEventDao {

    @Insert
    suspend fun insert(chargeEvent: ChargeEvent)

    @Query("INSERT INTO ChargeEvent (vehicleId, odometer, startDateTime, batteryStartingPct, batteryStartingRange) VALUES (:vehicleId, :startOdo, :startTime, :startBatteryPct, :startBatterRange)")
    suspend fun startChargeRecord(
        vehicleId: Long,
        startOdo: Int,
        startTime: LocalDateTime,
        startBatteryPct: Int,
        startBatterRange: Int
    ): Long

    @Query("UPDATE ChargeEvent SET endDateTime = :endDateTime, batteryEndingRange = :batteryEndingRange, batteryEndingPct = :batteryEndingPct, kilowatt = :kilowatt, totalCost = :totalCost WHERE id = :id")
    suspend fun updateChargeRecord(
        id: Long,
        endDateTime: LocalDateTime,
        batteryEndingPct: Int,
        batteryEndingRange: Int,
        kilowatt: Float,
        totalCost: Int
    )

    @Delete
    suspend fun delete(chargeEvent: ChargeEvent)

    @Query("DELETE FROM ChargeEvent")
    suspend fun deleteAll()

    @Query("SELECT * FROM ChargeEvent ORDER BY startDateTime DESC")
    fun getAll(): Flow<List<ChargeEvent>>

    @RawQuery(observedEntities = [ChargeEvent::class])
    fun getEventsWithin(query: SupportSQLiteQuery): Flow<List<ChargeEvent>>

    @Query("SELECT * FROM ChargeEvent WHERE id = :id")
    suspend fun getChargeEventWithId(id: Long): ChargeEvent?
}