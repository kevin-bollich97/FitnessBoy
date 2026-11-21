package de.bollich.fitnessboy.ui.measurements

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import de.bollich.fitnessboy.R
import de.bollich.fitnessboy.domain.BodyMeasurement
import de.bollich.fitnessboy.domain.measuredValues
import de.bollich.fitnessboy.model.BodyMeasurementsEntry
import de.bollich.fitnessboy.ui.components.formatCm

/** "Bauch: 88 cm" */
@Composable
fun measurementValueText(measurement: BodyMeasurement, valueInCm: Double): String =
    stringResource(R.string.measurement_value, stringResource(measurement.labelRes), formatCm(valueInCm))

/** "Bauch: 88 cm · Hüfte: 96 cm · …" with all measured values of [entry]. */
@Composable
fun measuredValuesText(entry: BodyMeasurementsEntry): String =
    entry.measuredValues()
        .map { (measurement, valueInCm) -> measurementValueText(measurement, valueInCm) }
        .joinToString(separator = stringResource(R.string.list_separator))
