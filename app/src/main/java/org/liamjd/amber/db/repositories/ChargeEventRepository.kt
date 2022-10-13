package org.liamjd.amber.db.repositories

import kotlinx.coroutines.flow.Flow
import org.liamjd.amber.db.entities.ChargeEvent
import org.liamjd.amber.db.entities.ChargeEventDao

class ChargeEventRepository(private val dao: ChargeEventDao) {

    val allChargeEvents: Flow<List<ChargeEvent>> = dao.getAll()

    suspend fun insert(chargeEvent: ChargeEvent) {
        dao.insert(chargeEvent)
    }
}