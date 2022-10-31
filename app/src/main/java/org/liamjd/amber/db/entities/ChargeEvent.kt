package org.liamjd.amber.db.entities

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import kotlinx.coroutines.flow.Flow
import org.liamjd.amber.toIntOrZero
import java.time.LocalDateTime

@Entity
data class ChargeEvent(
    val odometer: Int,
    val batteryStartingRange: Int,
    val batteryEndingRange: Int,
    val batteryStartingPct: Int,
    val batteryEndingPct: Int,
    val vehicleId: Long,
    val dateTime: LocalDateTime,
    val kilowatt: Float,
    val totalCost: Int // in pence
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
        dateTime = LocalDateTime.now(),
        kilowatt = kilowatt,
        totalCost = totalCost
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

    @Query("SELECT * FROM ChargeEvent ORDER BY dateTime DESC")
    fun getAll(): Flow<List<ChargeEvent>>

    @RawQuery(observedEntities = [ChargeEvent::class])
    fun getEventsWithin(query: SupportSQLiteQuery): Flow<List<ChargeEvent>>
}