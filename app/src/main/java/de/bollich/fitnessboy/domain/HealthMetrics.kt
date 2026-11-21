package de.bollich.fitnessboy.domain

/**
 * WHO BMI classification for adults. Each category covers [lowerBound] (inclusive) up to
 * [upperBound] (exclusive); `null` means unbounded.
 */
enum class BmiCategory(
    val lowerBound: Double?,
    val upperBound: Double?,
) {
    UNDERWEIGHT(null, 18.5),
    NORMAL_WEIGHT(18.5, 25.0),
    OVERWEIGHT(25.0, 30.0),
    OBESITY_CLASS_I(30.0, 35.0),
    OBESITY_CLASS_II(35.0, 40.0),
    OBESITY_CLASS_III(40.0, null),
}

fun classifyBmi(bmi: Double?): BmiCategory? {
    if (bmi == null) return null
    return BmiCategory.entries.first { category -> category.upperBound?.let { bmi < it } ?: true }
}

fun calculateBmi(weightInKg: Double?, heightInCm: Double?): Double? {
    if (weightInKg == null || heightInCm == null) {
        return null
    }

    val heightInMeters = heightInCm / 100.0
    if (heightInMeters <= 0.0) return null

    return weightInKg / (heightInMeters * heightInMeters)
}
