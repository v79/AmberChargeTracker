package org.liamjd.amber.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.liamjd.amber.db.entities.ChargeEvent
import org.liamjd.amber.db.entities.ChargeEventDao

@Database(entities = arrayOf(ChargeEvent::class), version = 1, exportSchema = false)
abstract class AmberDatabase : RoomDatabase() {

    abstract fun chargeEventDao(): ChargeEventDao

    companion object {
        @Volatile
        private var INSTANCE: AmberDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope) : AmberDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AmberDatabase::class.java,
                    "amber_database")
                    .addCallback(AmberDatabaseCallback(scope)
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AmberDatabaseCallback(private val scope: CoroutineScope) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    // populate database with fake data? Or other data setup tasks
                }
            }
        }
    }
}