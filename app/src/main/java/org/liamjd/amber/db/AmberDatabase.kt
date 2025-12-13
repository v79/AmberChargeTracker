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
    version = 20,
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
                    .addMigrations(
                        MIGRATION_15_16_addVehicleReg,
                        MIGRATION_16_17_addVehicleUpdateDateTime,
                        MIGRATION_17_18_addVehiclePhotoPath,
                        MIGRATION_18_19_addCostPerKwH,
                        MIGRATION_19_20_addCostPerKwHPence
                    )
                    .addCallback(AmberDatabaseCallback(scope))
                    .build()
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
        private val MIGRATION_16_17_addVehicleUpdateDateTime = object : Migration(16, 17) {
            override fun migrate(database: SupportSQLiteDatabase) {
                val now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
                database.execSQL("ALTER TABLE Vehicle ADD COLUMN lastUpdated INTEGER NOT NULL DEFAULT $now")
            }
        }
        private val MIGRATION_17_18_addVehiclePhotoPath = object : Migration(17, 18) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE Vehicle ADD COLUMN photoPath TEXT DEFAULT NULL")
            }
        }
        // 18 -> 19: add costPerKwH REAL column
        private val MIGRATION_18_19_addCostPerKwH = object : Migration(18, 19) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE ChargeEvent ADD COLUMN costPerKwH REAL DEFAULT NULL")
            }
        }
        // 19 -> 20: move from costPerKwH REAL to costPerKwHPence INTEGER and drop old column
        private val MIGRATION_19_20_addCostPerKwHPence = object : Migration(19, 20) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 1. Create new table with the exact schema Room expects at version 20
                database.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `ChargeEvent_new` (
                      `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                      `odometer` INTEGER NOT NULL,
                      `startDateTime` INTEGER NOT NULL,
                      `endDateTime` INTEGER,
                      `batteryStartingRange` INTEGER NOT NULL,
                      `batteryEndingRange` INTEGER,
                      `batteryStartingPct` INTEGER NOT NULL,
                      `batteryEndingPct` INTEGER,
                      `vehicleId` INTEGER NOT NULL,
                      `kilowatt` REAL,
                      `costPerKwHPence` INTEGER,
                      `totalCost` INTEGER
                    )
                    """.trimIndent()
                )

                // 2. Copy data from old table into new, computing pence from costPerKwH
                // Live devices at v19 only have costPerKwH (REAL) and no costPerKwHPence column
                database.execSQL(
                    """
                    INSERT INTO ChargeEvent_new (
                        id,
                        odometer,
                        startDateTime,
                        endDateTime,
                        batteryStartingRange,
                        batteryEndingRange,
                        batteryStartingPct,
                        batteryEndingPct,
                        vehicleId,
                        kilowatt,
                        costPerKwHPence,
                        totalCost
                    )
                    SELECT
                        id,
                        odometer,
                        startDateTime,
                        endDateTime,
                        batteryStartingRange,
                        batteryEndingRange,
                        batteryStartingPct,
                        batteryEndingPct,
                        vehicleId,
                        kilowatt,
                        CASE
                            WHEN costPerKwH IS NOT NULL THEN CAST(ROUND(costPerKwH * 100.0) AS INTEGER)
                            ELSE NULL
                        END AS costPerKwHPence,
                        totalCost
                    FROM ChargeEvent;
                    """.trimIndent()
                )

                // 3. Drop old table and rename new to original name
                database.execSQL("DROP TABLE ChargeEvent")
                database.execSQL("ALTER TABLE ChargeEvent_new RENAME TO ChargeEvent")
            }
        }
    }

    private class AmberDatabaseCallback(private val scope: CoroutineScope) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    // Initial data setup can go here if needed
                    // database.vehicleDao().insert(Vehicle("Volkswagen", "iD.3", 275))
                }
            }
        }
    }
}
