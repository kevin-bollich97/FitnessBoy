package de.bollich.fitnessboy.domain

fun classifyBmi(bmi: Double?): String? {
    if (bmi == null) return null

    return when {
        bmi < 18.5 -> "Untergewicht"
        bmi < 25.0 -> "Normalgewicht"
        bmi < 30.0 -> "Übergewicht"
        bmi < 35.0 -> "Adipositas Grad I"
        bmi < 40.0 -> "Adipositas Grad II"
        else -> "Adipositas Grad III"
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
