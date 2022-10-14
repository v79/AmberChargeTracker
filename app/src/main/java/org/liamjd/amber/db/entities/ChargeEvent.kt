package org.liamjd.amber.db.entities

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.liamjd.amber.toIntOrZero


/*
val chargeRecordId = 123
        var chargeTime by remember {
            mutableStateOf(LocalDateTime.now())
        }
        var odometer by remember {
            mutableStateOf("0")
        }
        var batteryStartRange by remember {
            mutableStateOf("100")
        }
        var batteryStartPct by remember {
            mutableStateOf("50")
        }
        var batteryEndRange by remember {
            mutableStateOf("200")
        }
        var batteryEndPct by remember {
            mutableStateOf("80")
        }
        var chargeDuration by remember {
            mutableStateOf("30")
        }
        var minimumFee by remember {
            mutableStateOf("100")
        }
        var costPerKWH by remember {
            mutableStateOf("15")
        }
        var totalCost by remember {
            mutableStateOf("0")
        }
 */
@Entity
data class ChargeEvent(
    val odometer: Int,
    val batteryStartingRange: Int,
    val batteryEndingRange: Int,
    val batteryStartingPct: Int,
    val batteryEndingPct: Int
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    constructor(
        odometer: String,
        batteryStartingRange: String,
        batteryEndingRange: String,
        batteryStartingPct: String,
        batteryEndingPct: String
    ) : this(
        odometer = odometer.toIntOrZero(),
        batteryStartingRange = batteryStartingRange.toIntOrZero(),
        batteryEndingRange = batteryEndingRange.toIntOrZero(),
        batteryStartingPct = batteryStartingPct.toIntOrZero(),
        batteryEndingPct = batteryEndingPct.toIntOrZero()
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