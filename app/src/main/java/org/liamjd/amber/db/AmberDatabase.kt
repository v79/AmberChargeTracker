package org.liamjd.amber.db

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.liamjd.amber.db.entities.*

@Database(
    entities = [ChargeEvent::class, Vehicle::class, Setting::class],
    version = 16,
    exportSchema = true
)
@TypeConverters(DBConverters::class)
abstract class AmberDatabase : RoomDatabase() {

    abstract fun chargeEventDao(): ChargeEventDao
    abstract fun vehicleDao(): VehicleDao
    abstract fun settingsDao(): SettingsDao

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
                    .addMigrations(MIGRATION_15_16_addVehicleReg)
                    .addCallback(
                        AmberDatabaseCallback(scope)
                    ).build()
                INSTANCE = instance
                instance
            }
        }

        // migrations
        val MIGRATION_15_16_addVehicleReg = object : Migration(15, 16) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Vehicle ADD COLUMN registration TEXT NOT NULL DEFAULT '' ")
            }
        }
    }

    private class AmberDatabaseCallback(private val scope: CoroutineScope) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    // populate database with fake data? Or other data setup tasks
//                    database.vehicleDao().insert(Vehicle("Volkswagen", "iD.3", 275))
                }
            }
        }
    }
}