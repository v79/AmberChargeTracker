package org.liamjd.amber.db

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.liamjd.amber.db.entities.*
import java.time.LocalDateTime
import java.time.ZoneOffset

@Database(
    entities = [ChargeEvent::class, Vehicle::class, Setting::class],
    version = 18,
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
                    .addMigrations(MIGRATION_15_16_addVehicleReg, MIGRATION_16_17_addVehicleUpdateDateTime, MIGRATION_17_18_addVehiclePhotoPath)
                    .addCallback(
                        AmberDatabaseCallback(scope)
                    ).build()
                INSTANCE = instance
                instance
            }
        }

        // migrations
        private val MIGRATION_15_16_addVehicleReg = object : Migration(15, 16) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Vehicle ADD COLUMN registration TEXT NOT NULL DEFAULT '' ")
            }
        }
        private val MIGRATION_16_17_addVehicleUpdateDateTime = object : Migration(16,17) {
            override fun migrate(database: SupportSQLiteDatabase) {
                val now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
                database.execSQL("ALTER TABLE Vehicle ADD COLUMN lastUpdated INTEGER NOT NULL DEFAULT $now")
            }
        }
        private val MIGRATION_17_18_addVehiclePhotoPath = object : Migration(17,18) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Vehicle ADD COLUMN photoPath TEXT DEFAULT NULL")
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