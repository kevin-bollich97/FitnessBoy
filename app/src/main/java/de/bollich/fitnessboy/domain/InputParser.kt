package de.bollich.fitnessboy.domain

fun parseWeight(input: String): Double? = parseBoundedNumber(input, min = 20.0, max = 500.0)

fun parseOptionalWeight(input: String): Double? =
    if (input.isBlank()) null else parseWeight(input)

fun parseHeight(input: String): Double? = parseBoundedNumber(input, min = 80.0, max = 260.0)

/** Valid range for body measurements in cm. */
const val BODY_MEASUREMENT_MIN_CM = 20.0
const val BODY_MEASUREMENT_MAX_CM = 250.0

fun parseBodyMeasurement(input: String): Double? =
    parseBoundedNumber(input, min = BODY_MEASUREMENT_MIN_CM, max = BODY_MEASUREMENT_MAX_CM)

private fun parseBoundedNumber(input: String, min: Double, max: Double): Double? {
    val normalized = input.trim().replace(",", ".")
    val parsed = normalized.toDoubleOrNull() ?: return null
    return parsed.takeIf { it in min..max }
}
