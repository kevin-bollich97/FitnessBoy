package de.bollich.fitnessboy.domain

/** WHO BMI classification for adults. */
enum class BmiCategory {
    UNDERWEIGHT,
    NORMAL_WEIGHT,
    OVERWEIGHT,
    OBESITY_CLASS_I,
    OBESITY_CLASS_II,
    OBESITY_CLASS_III,
}

fun classifyBmi(bmi: Double?): BmiCategory? {
    if (bmi == null) return null

    return when {
        bmi < 18.5 -> BmiCategory.UNDERWEIGHT
        bmi < 25.0 -> BmiCategory.NORMAL_WEIGHT
        bmi < 30.0 -> BmiCategory.OVERWEIGHT
        bmi < 35.0 -> BmiCategory.OBESITY_CLASS_I
        bmi < 40.0 -> BmiCategory.OBESITY_CLASS_II
        else -> BmiCategory.OBESITY_CLASS_III
    }
}

fun calculateBmi(weightInKg: Double?, heightInCm: Double?): Double? {
    if (weightInKg == null || heightInCm == null) {
        return null
    }

    val heightInMeters = heightInCm / 100.0
    if (heightInMeters <= 0.0) return null

    return weightInKg / (heightInMeters * heightInMeters)
}
