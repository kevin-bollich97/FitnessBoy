package de.bollich.fitnessboy.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [WeightEntryEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class FitnessBoyDatabase : RoomDatabase() {
    abstract fun weightEntryDao(): WeightEntryDao

    companion object {
        @Volatile
        private var instance: FitnessBoyDatabase? = null

        fun getInstance(context: Context): FitnessBoyDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    FitnessBoyDatabase::class.java,
                    "fitnessboy.db",
                )
                    .fallbackToDestructiveMigration(false)
                    .allowMainThreadQueries()
                    .build()
                    .also { instance = it }
            }
    }
}
