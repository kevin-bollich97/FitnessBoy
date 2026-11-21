package de.bollich.fitnessboy.data.local

import androidx.room.testing.MigrationTestHelper
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Runs the Room migrations against the exported schemas (`app/schemas`) on Robolectric, so a
 * broken migration fails `./gradlew test` instead of deleting user data on the device.
 */
@RunWith(RobolectricTestRunner::class)
class FitnessBoyDatabaseMigrationTest {
    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        FitnessBoyDatabase::class.java,
    )

    @Test
    fun migrate2To3_keepsExistingDataAndLeavesNewMeasurementsEmpty() {
        helper.createDatabase(TEST_DB, 2).use { db ->
            db.execSQL("INSERT INTO weight_entries (date_epoch_day, weight_in_kg) VALUES (20500, 82.5)")
            db.execSQL(
                "INSERT INTO body_measurements (date_epoch_day, waist_in_cm, hips_in_cm, shoulders_in_cm) " +
                    "VALUES (20500, 90.0, 100.0, 50.0)"
            )
        }

        helper.runMigrationsAndValidate(TEST_DB, 3, true, FitnessBoyDatabase.migration2To3).use { db ->
            db.query("SELECT date_epoch_day, weight_in_kg FROM weight_entries").use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals(20500L, cursor.getLong(0))
                assertEquals(82.5, cursor.getDouble(1), 0.0)
            }
            db.query(
                "SELECT waist_in_cm, hips_in_cm, shoulders_in_cm, upper_arm_in_cm, chest_in_cm, thigh_in_cm " +
                    "FROM body_measurements"
            ).use { cursor ->
                assertTrue(cursor.moveToFirst())
                assertEquals(90.0, cursor.getDouble(0), 0.0)
                assertEquals(100.0, cursor.getDouble(1), 0.0)
                assertEquals(50.0, cursor.getDouble(2), 0.0)
                assertTrue(cursor.isNull(3))
                assertTrue(cursor.isNull(4))
                assertTrue(cursor.isNull(5))
            }
        }
    }

    private companion object {
        const val TEST_DB = "migration-test.db"
    }
}
