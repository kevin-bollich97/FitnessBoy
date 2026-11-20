package de.bollich.fitnessboy.model

import java.time.LocalDate

data class WeightEntry(
    val id: Long = 0,
    val date: LocalDate,
    val weightInKg: Double,
)
