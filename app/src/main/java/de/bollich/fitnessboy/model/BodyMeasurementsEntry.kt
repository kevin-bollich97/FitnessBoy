package de.bollich.fitnessboy.model

import java.time.LocalDate

data class BodyMeasurementsEntry(
    val id: Long = 0,
    val date: LocalDate,
    val waistInCm: Double,
    val hipsInCm: Double,
    val shouldersInCm: Double,
)
