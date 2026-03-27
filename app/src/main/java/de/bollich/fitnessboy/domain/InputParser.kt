package de.bollich.fitnessboy.domain

fun parseWeight(input: String): Double? = parseBoundedNumber(input, min = 20.0, max = 500.0)

fun parseOptionalWeight(input: String): Double? =
    if (input.isBlank()) null else parseWeight(input)

fun parseHeight(input: String): Double? = parseBoundedNumber(input, min = 80.0, max = 260.0)

fun parseBodyMeasurement(input: String): Double? = parseBoundedNumber(input, min = 20.0, max = 250.0)

private fun parseBoundedNumber(input: String, min: Double, max: Double): Double? {
    val normalized = input.trim().replace(",", ".")
    val parsed = normalized.toDoubleOrNull() ?: return null
    return parsed.takeIf { it in min..max }
}
