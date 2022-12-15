package org.liamjd.amber.db.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.sqlite.db.SimpleSQLiteQuery
import kotlinx.coroutines.flow.Flow
import org.liamjd.amber.db.entities.ChargeEvent
import org.liamjd.amber.db.entities.ChargeEventDao
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.logging.Logger

class ChargeEventRepository(private val dao: ChargeEventDao) {

    val allChargeEvents: Flow<List<ChargeEvent>> = dao.getAll()

    /**
     * Get all the charge events for the currently selected vehicle, narrowed by the date range
     */
    fun getEventsWithin(days: Int, vehicleId: Long): Flow<List<ChargeEvent>> {
        if (days <= 0) {
            return dao.getAll()
        }
        val now = LocalDateTime.now()
        val xDaysAgo = now.minusDays(days.toLong())
        val xDaysAgoAsLong = xDaysAgo.toEpochSecond(ZoneOffset.UTC)
        Log.i("ChargeEventRepo","getEventsWithin($days,$vehicleId)")

        return dao.getEventsSince(xDaysAgoAsLong,vehicleId)
    }

    fun getAllEventsForVehicle(vehicleId: Long): Flow<List<ChargeEvent>> {
        return dao.getAllForVehicle(vehicleId)
    }

    suspend fun insert(chargeEvent: ChargeEvent) {
        dao.insert(chargeEvent)
    }

    fun getLiveChargeEventWithId(id: Long): LiveData<ChargeEvent?> = dao.getChargeEventWithId(id)

    suspend fun getChargeEventWithId(id: Long) = dao.getExistingChargeEventWithId(id)

    suspend fun startChargeEvent(
        vehicleId: Long, startOdo: Int,
        startTime: LocalDateTime,
        startBatteryPct: Int,
        startBatteryRange: Int
    ): Long {
        return dao.startChargeRecord(
            vehicleId,
            startOdo,
            startTime,
            startBatteryPct,
            startBatteryRange
        )
    }

    suspend fun completeChargeEvent(
        id: Long,
        endTime: LocalDateTime,
        endBatteryPct: Int,
        endBatteryRange: Int,
        kw: Float,
        cost: Int
    ) {
        dao.updateChargeRecord(id, endTime, endBatteryPct, endBatteryRange, kw, cost)
    }

    suspend fun deleteChargeEvent(id: Long) {
        dao.delete(dao.getExistingChargeEventWithId(id))
    }
}