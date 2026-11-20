package de.bollich.fitnessboy.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BodyMeasurementsDao {
    @Query("SELECT * FROM body_measurements ORDER BY date_epoch_day DESC, id DESC")
    fun observeAll(): Flow<List<BodyMeasurementsEntity>>

    @Insert
    suspend fun insert(entry: BodyMeasurementsEntity)

    @Delete
    suspend fun delete(entry: BodyMeasurementsEntity)
}
