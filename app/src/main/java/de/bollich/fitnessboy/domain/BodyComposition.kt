package de.bollich.fitnessboy.domain

/** BMI range that counts as healthy for adults; the basis of [calculateHealthyWeightRange]. */
const val HEALTHY_BMI_MIN = 18.5
const val HEALTHY_BMI_MAX = 24.9

/** BMI used as a rough optimum in [calculateOptimalWeight]. */
const val OPTIMAL_BMI = 22.0

data class WeightRange(
    val minimumInKg: Double,
    val maximumInKg: Double,
)

fun calculateHealthyWeightRange(heightInCm: Double?): WeightRange? {
    val heightInMeters = toHeightInMeters(heightInCm) ?: return null
    return WeightRange(
        minimumInKg = HEALTHY_BMI_MIN * heightInMeters * heightInMeters,
        maximumInKg = HEALTHY_BMI_MAX * heightInMeters * heightInMeters,
    )
}

fun calculateOptimalWeight(heightInCm: Double?): Double? {
    val heightInMeters = toHeightInMeters(heightInCm) ?: return null
    return OPTIMAL_BMI * heightInMeters * heightInMeters
}

private fun toHeightInMeters(heightInCm: Double?): Double? {
    if (heightInCm == null) return null
    val heightInMeters = heightInCm / 100.0
    return heightInMeters.takeIf { it > 0.0 }
}
