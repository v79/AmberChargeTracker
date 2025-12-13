package org.liamjd.amber.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Migration19To20Test {

    private val MIGRATION_19_20 = object : Migration(19, 20) {
        override fun migrate(db: SupportSQLiteDatabase) {
            // Add integer column for pence and populate from existing REAL column
            db.execSQL("ALTER TABLE ChargeEvent ADD COLUMN costPerKwHPence INTEGER DEFAULT NULL")
            db.execSQL("UPDATE ChargeEvent SET costPerKwHPence = CAST(ROUND(costPerKwH * 100.0) AS INTEGER) WHERE costPerKwH IS NOT NULL")
        }
    }

    @Test
    fun migrate19To20_convertsRealToPence() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val dbName = "amber_database"

        // Create a SupportSQLiteOpenHelper that will create a DB with version 19
        val callback = object : SupportSQLiteOpenHelper.Callback(19) {
            override fun onCreate(db: SupportSQLiteDatabase) {
                // no-op; we'll create tables manually to match v19
            }

            override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {
                // no-op
            }
        }

        val config = SupportSQLiteOpenHelper.Configuration.builder(context)
            .name(dbName)
            .callback(callback)
            .build()

        val helper = FrameworkSQLiteOpenHelperFactory().create(config)
        val db = helper.writableDatabase

        // Create minimal ChargeEvent table matching v19 (including costPerKwH REAL)
        db.execSQL(
            """
                CREATE TABLE IF NOT EXISTS `ChargeEvent` (
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
                  `costPerKwH` REAL,
                  `totalCost` INTEGER
                );
                """.trimIndent()
        )

        // insert a row with costPerKwH = 0.79
        db.execSQL("INSERT INTO ChargeEvent(odometer, startDateTime, batteryStartingRange, batteryStartingPct, vehicleId, kilowatt, costPerKwH, totalCost) VALUES (100, 0, 50, 25, 1, 22.0, 0.79, 1234)")

        // Apply migration
        MIGRATION_19_20.migrate(db)

        // Verify costPerKwHPence exists and equals 79 (rounded)
        val cursor = db.query("SELECT id, odometer, costPerKwHPence, totalCost FROM ChargeEvent")
        assertNotNull(cursor)
        cursor.use {
            assertEquals(1, cursor.count)
            cursor.moveToFirst()
            val odometer = cursor.getInt(cursor.getColumnIndex("odometer"))
            val totalCost = if (!cursor.isNull(cursor.getColumnIndex("totalCost"))) cursor.getInt(cursor.getColumnIndex("totalCost")) else null
            val penceIndex = cursor.getColumnIndex("costPerKwHPence")
            val pence = if (penceIndex >= 0 && !cursor.isNull(penceIndex)) cursor.getInt(penceIndex) else null

            assertEquals(100, odometer)
            assertEquals(1234, totalCost)
            assertEquals(79, pence)
        }

        db.close()
        helper.close()
    }
}
