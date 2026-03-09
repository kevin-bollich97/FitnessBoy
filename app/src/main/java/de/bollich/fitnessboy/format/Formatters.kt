package de.bollich.fitnessboy.format

import de.bollich.fitnessboy.model.WeightEntry
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
private val shortDateFormatter = DateTimeFormatter.ofPattern("dd.MM.")

fun WeightEntry.formattedDate(): String = date.format(dateFormatter)

fun WeightEntry.formattedShortDate(): String = date.format(shortDateFormatter)

fun WeightEntry.formattedWeight(): String = "${formatNumber(weightInKg)} kg"

fun formatNumber(value: Double): String =
    if (value % 1.0 == 0.0) value.toInt().toString() else "%.1f".format(value)

fun LocalDate.formattedDate(): String = format(dateFormatter)
