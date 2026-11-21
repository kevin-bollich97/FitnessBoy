package de.bollich.fitnessboy.domain

import de.bollich.fitnessboy.model.WeightEntry
import java.time.LocalDate

/**
 * The weight that applied on [date]: the entry of that day or the latest one before it. With
 * several entries on the same day, the one added last wins. `null` if there is none yet.
 */
fun weightOn(date: LocalDate, entries: List<WeightEntry>): WeightEntry? =
    entries
        .filter { it.date <= date }
        .maxWithOrNull(compareBy<WeightEntry> { it.date }.thenBy { it.id })

/** BMI on [date], based on [weightOn] and the current height. */
fun calculateBmiOn(date: LocalDate, entries: List<WeightEntry>, heightInCm: Double?): Double? =
    calculateBmi(weightOn(date, entries)?.weightInKg, heightInCm)
