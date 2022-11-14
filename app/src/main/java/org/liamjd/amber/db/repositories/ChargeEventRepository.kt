package org.liamjd.amber.db.repositories

import androidx.sqlite.db.SimpleSQLiteQuery
import kotlinx.coroutines.flow.Flow
import org.liamjd.amber.db.entities.ChargeEvent
import org.liamjd.amber.db.entities.ChargeEventDao
import java.time.LocalDateTime
import java.time.ZoneOffset

class ChargeEventRepository(private val dao: ChargeEventDao) {

    val allChargeEvents: Flow<List<ChargeEvent>> = dao.getAll()

    fun getEventsWithin(days: Int): Flow<List<ChargeEvent>> {
        if (days <= 0) {
            return dao.getAll()
        }
        val now = LocalDateTime.now()
        val xDaysAgo = now.minusDays(days.toLong())
        val xDaysAgoAsLong = xDaysAgo.toEpochSecond(ZoneOffset.UTC)
        return dao.getEventsWithin(
            SimpleSQLiteQuery(
                "SELECT * FROM ChargeEvent WHERE dateTime > ? ORDER BY dateTime DESC",
                arrayOf(xDaysAgoAsLong)
            )
        )
    }

    suspend fun insert(chargeEvent: ChargeEvent) {
        dao.insert(chargeEvent)
    }

    suspend fun getChargeEventWithId(id: Long): ChargeEvent? = dao.getChargeEventWithId(id)

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
}