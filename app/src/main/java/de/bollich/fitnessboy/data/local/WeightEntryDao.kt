package de.bollich.fitnessboy.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WeightEntryDao {
    @Query("SELECT * FROM weight_entries ORDER BY date_epoch_day DESC, id DESC")
    fun getAll(): List<WeightEntryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(entries: List<WeightEntryEntity>)

    @Query("DELETE FROM weight_entries")
    fun deleteAll()

    @Query("SELECT COUNT(*) FROM weight_entries")
    fun count(): Int
}
