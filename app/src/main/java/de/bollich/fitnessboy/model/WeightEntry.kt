package de.bollich.fitnessboy.model

import java.time.LocalDate

data class WeightEntry(
    val date: LocalDate,
    val weightInKg: Double,
)
