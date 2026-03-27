package de.bollich.fitnessboy.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface BodyMeasurementsDao {
    @Query("SELECT * FROM body_measurements ORDER BY date_epoch_day DESC, id DESC")
    fun getAll(): List<BodyMeasurementsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(entry: BodyMeasurementsEntity)
}
