package de.bollich.fitnessboy.domain

data class WeightRange(
    val minimumInKg: Double,
    val maximumInKg: Double,
)

fun calculateHealthyWeightRange(heightInCm: Double?): WeightRange? {
    val heightInMeters = toHeightInMeters(heightInCm) ?: return null
    return WeightRange(
        minimumInKg = 18.5 * heightInMeters * heightInMeters,
        maximumInKg = 24.9 * heightInMeters * heightInMeters,
    )
}

fun calculateOptimalWeight(heightInCm: Double?): Double? {
    val heightInMeters = toHeightInMeters(heightInCm) ?: return null
    return 22.0 * heightInMeters * heightInMeters
}

private fun toHeightInMeters(heightInCm: Double?): Double? {
    if (heightInCm == null) return null
    val heightInMeters = heightInCm / 100.0
    return heightInMeters.takeIf { it > 0.0 }
}
