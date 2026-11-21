package de.bollich.fitnessboy.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightEntryDao {
    @Query("SELECT * FROM weight_entries ORDER BY date_epoch_day DESC, id DESC")
    fun observeAll(): Flow<List<WeightEntryEntity>>

    @Insert
    suspend fun insert(entry: WeightEntryEntity)

    @Delete
    suspend fun delete(entry: WeightEntryEntity)
}
