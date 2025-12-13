package org.liamjd.amber.db

import androidx.room.migration.Migration
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MigrationTest {

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AmberDatabase::class.java.canonicalName,
        FrameworkSQLiteOpenHelperFactory()
    )

    private val MIGRATION_18_19 = object : Migration(18, 19) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE ChargeEvent ADD COLUMN costPerKwH REAL DEFAULT NULL")
        }
    }

    @Test
    fun migrate18To19() {
        // Create database with version 18 schema and insert a row
        val dbName = "amber_database"
        helper.createDatabase(dbName, 18).apply {
            // create minimal table schema for ChargeEvent as per v18
            execSQL(
                """
                CREATE TABLE IF NOT EXISTS `ChargeEvent` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `odometer` INTEGER NOT NULL, `startDateTime` INTEGER NOT NULL, `endDateTime` INTEGER, `batteryStartingRange` INTEGER NOT NULL, `batteryEndingRange` INTEGER, `batteryStartingPct` INTEGER NOT NULL, `batteryEndingPct` INTEGER, `vehicleId` INTEGER NOT NULL, `kilowatt` REAL, `totalCost` INTEGER)
                """.trimIndent()
            )
            // insert a row
            execSQL("INSERT INTO ChargeEvent(odometer, startDateTime, batteryStartingRange, batteryStartingPct, vehicleId, kilowatt, totalCost) VALUES (100, 0, 50, 25, 1, 22.0, 1234)")
            close()
        }

        // Run migration and get migrated DB
        val migrated = helper.runMigrationsAndValidate(dbName, 19, true, MIGRATION_18_19)

        // verify new column exists and original data present
        val cursor = migrated.query("SELECT id, odometer, totalCost, costPerKwH FROM ChargeEvent")
        assertNotNull(cursor)
        cursor.use {
            assertEquals(1, cursor.count)
            cursor.moveToFirst()
            val odometer = cursor.getInt(cursor.getColumnIndex("odometer"))
            val totalCost = if (!cursor.isNull(cursor.getColumnIndex("totalCost"))) cursor.getInt(cursor.getColumnIndex("totalCost")) else null
            val costPerKwHIndex = cursor.getColumnIndex("costPerKwH")
            val costPerKwH = if (costPerKwHIndex >= 0 && !cursor.isNull(costPerKwHIndex)) cursor.getFloat(costPerKwHIndex) else null

            assertEquals(100, odometer)
            assertEquals(1234, totalCost)
            assertEquals(null, costPerKwH)
        }
    }
}
