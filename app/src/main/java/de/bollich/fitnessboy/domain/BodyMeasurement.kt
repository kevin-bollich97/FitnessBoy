package de.bollich.fitnessboy.domain

import de.bollich.fitnessboy.model.BodyMeasurementsEntry

/** The body measurements the app tracks. Required ones must be part of every entry. */
enum class BodyMeasurement(val isRequired: Boolean) {
    WAIST(isRequired = true),
    HIPS(isRequired = true),
    SHOULDERS(isRequired = true),
    UPPER_ARM(isRequired = false),
    CHEST(isRequired = false),
    THIGH(isRequired = false),
}

/** Value of [measurement] in cm, or `null` if it was not measured. */
fun BodyMeasurementsEntry.valueOf(measurement: BodyMeasurement): Double? =
    when (measurement) {
        BodyMeasurement.WAIST -> waistInCm
        BodyMeasurement.HIPS -> hipsInCm
        BodyMeasurement.SHOULDERS -> shouldersInCm
        BodyMeasurement.UPPER_ARM -> upperArmInCm
        BodyMeasurement.CHEST -> chestInCm
        BodyMeasurement.THIGH -> thighInCm
    }

/** All measured values of this entry in the order of [BodyMeasurement], skipping missing ones. */
fun BodyMeasurementsEntry.measuredValues(): Map<BodyMeasurement, Double> =
    BodyMeasurement.entries
        .mapNotNull { measurement -> valueOf(measurement)?.let { measurement to it } }
        .toMap()
