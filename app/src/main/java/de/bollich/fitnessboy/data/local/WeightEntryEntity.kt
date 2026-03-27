package de.bollich.fitnessboy.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.bollich.fitnessboy.model.WeightEntry
import java.time.LocalDate

@Entity(tableName = "weight_entries")
data class WeightEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "date_epoch_day")
    val dateEpochDay: Long,
    @ColumnInfo(name = "weight_in_kg")
    val weightInKg: Double,
)

fun WeightEntryEntity.toModel(): WeightEntry =
    WeightEntry(
        date = LocalDate.ofEpochDay(dateEpochDay),
        weightInKg = weightInKg,
    )

fun WeightEntry.toEntity(): WeightEntryEntity =
    WeightEntryEntity(
        dateEpochDay = date.toEpochDay(),
        weightInKg = weightInKg,
    )
