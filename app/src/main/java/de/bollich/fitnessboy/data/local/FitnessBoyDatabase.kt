package de.bollich.fitnessboy.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [WeightEntryEntity::class, BodyMeasurementsEntity::class],
    version = 2,
    exportSchema = false,
)
abstract class FitnessBoyDatabase : RoomDatabase() {
    abstract fun weightEntryDao(): WeightEntryDao
    abstract fun bodyMeasurementsDao(): BodyMeasurementsDao

    companion object {
        private val migration1To2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `body_measurements` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `date_epoch_day` INTEGER NOT NULL,
                        `waist_in_cm` REAL NOT NULL,
                        `hips_in_cm` REAL NOT NULL,
                        `shoulders_in_cm` REAL NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        @Volatile
        private var instance: FitnessBoyDatabase? = null

        fun getInstance(context: Context): FitnessBoyDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    FitnessBoyDatabase::class.java,
                    "fitnessboy.db",
                )
                    .addMigrations(migration1To2)
                    .fallbackToDestructiveMigration(false)
                    .allowMainThreadQueries()
                    .build()
                    .also { instance = it }
            }
    }
}
