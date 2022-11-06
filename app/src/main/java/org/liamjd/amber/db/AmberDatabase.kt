package org.liamjd.amber.db

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.liamjd.amber.db.entities.ChargeEvent
import org.liamjd.amber.db.entities.ChargeEventDao
import org.liamjd.amber.db.entities.Vehicle
import org.liamjd.amber.db.entities.VehicleDao

@Database(
    entities = [ChargeEvent::class, Vehicle::class], version = 12, exportSchema = true
)
@TypeConverters(DBConverters::class)
abstract class AmberDatabase : RoomDatabase() {

    abstract fun chargeEventDao(): ChargeEventDao
    abstract fun vehicleDao(): VehicleDao

    companion object {
        @Volatile
        private var INSTANCE: AmberDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AmberDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AmberDatabase::class.java,
                    "amber_database"
                )
                    .addCallback(
                        AmberDatabaseCallback(scope)
                    ).fallbackToDestructiveMigration().build()
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
                    database.vehicleDao().insert(Vehicle("Volkswagen", "iD.3", 275))
                    // populate database with fake data? Or other data setup tasks
                }
            }
        }
    }
}