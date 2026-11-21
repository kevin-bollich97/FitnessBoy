package de.bollich.fitnessboy.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import de.bollich.fitnessboy.model.BodyMeasurementsEntry
import java.time.LocalDate

@Entity(tableName = "body_measurements")
data class BodyMeasurementsEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "date_epoch_day")
    val dateEpochDay: Long,
    @ColumnInfo(name = "waist_in_cm")
    val waistInCm: Double,
    @ColumnInfo(name = "hips_in_cm")
    val hipsInCm: Double,
    @ColumnInfo(name = "shoulders_in_cm")
    val shouldersInCm: Double,
    @ColumnInfo(name = "upper_arm_in_cm")
    val upperArmInCm: Double? = null,
    @ColumnInfo(name = "chest_in_cm")
    val chestInCm: Double? = null,
    @ColumnInfo(name = "thigh_in_cm")
    val thighInCm: Double? = null,
)

fun BodyMeasurementsEntity.toModel(): BodyMeasurementsEntry =
    BodyMeasurementsEntry(
        id = id,
        date = LocalDate.ofEpochDay(dateEpochDay),
        waistInCm = waistInCm,
        hipsInCm = hipsInCm,
        shouldersInCm = shouldersInCm,
        upperArmInCm = upperArmInCm,
        chestInCm = chestInCm,
        thighInCm = thighInCm,
    )

fun BodyMeasurementsEntry.toEntity(): BodyMeasurementsEntity =
    BodyMeasurementsEntity(
        id = id,
        dateEpochDay = date.toEpochDay(),
        waistInCm = waistInCm,
        hipsInCm = hipsInCm,
        shouldersInCm = shouldersInCm,
        upperArmInCm = upperArmInCm,
        chestInCm = chestInCm,
        thighInCm = thighInCm,
    )
