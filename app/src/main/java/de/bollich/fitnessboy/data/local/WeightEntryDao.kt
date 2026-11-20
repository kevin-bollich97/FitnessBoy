package de.bollich.fitnessboy.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface WeightEntryDao {
    @Query("SELECT * FROM weight_entries ORDER BY date_epoch_day DESC, id DESC")
    fun observeAll(): Flow<List<WeightEntryEntity>>

    @Insert
    suspend fun insert(entry: WeightEntryEntity)

    @Insert
    suspend fun insertAll(entries: List<WeightEntryEntity>)

    @Delete
    suspend fun delete(entry: WeightEntryEntity)

    @Query("SELECT COUNT(*) FROM weight_entries")
    suspend fun count(): Int

    /** Inserts [entries] only if the table is empty. Returns whether anything was inserted. */
    @Transaction
    suspend fun insertAllIfEmpty(entries: List<WeightEntryEntity>): Boolean {
        if (count() > 0) return false
        insertAll(entries)
        return true
    }
}
