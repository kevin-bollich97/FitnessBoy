package de.bollich.fitnessboy.domain.usecase

import de.bollich.fitnessboy.domain.BodyMeasurement
import de.bollich.fitnessboy.domain.parseBodyMeasurement
import de.bollich.fitnessboy.domain.repository.BodyMeasurementsRepository
import de.bollich.fitnessboy.model.BodyMeasurementsEntry
import java.time.LocalDate

/**
 * Validates and saves body measurements. Required measurements must be valid; optional ones may
 * be left blank but must be valid if filled in.
 */
class AddBodyMeasurementsEntry(
    private val repository: BodyMeasurementsRepository,
) {
    suspend operator fun invoke(
        date: LocalDate,
        inputs: Map<BodyMeasurement, String>,
    ): Result {
        val parsed = BodyMeasurement.entries.associateWith { parse(it, inputs[it].orEmpty()) }
        val invalid = parsed.filterValues { it is Parsed.Invalid }.keys
        if (invalid.isNotEmpty()) return Result.Invalid(invalid)

        fun valueOf(measurement: BodyMeasurement) = (parsed.getValue(measurement) as Parsed.Valid).valueInCm
        repository.add(
            BodyMeasurementsEntry(
                date = date,
                waistInCm = checkNotNull(valueOf(BodyMeasurement.WAIST)),
                hipsInCm = checkNotNull(valueOf(BodyMeasurement.HIPS)),
                shouldersInCm = checkNotNull(valueOf(BodyMeasurement.SHOULDERS)),
                upperArmInCm = valueOf(BodyMeasurement.UPPER_ARM),
                chestInCm = valueOf(BodyMeasurement.CHEST),
                thighInCm = valueOf(BodyMeasurement.THIGH),
            )
        )
        return Result.Success
    }

    private fun parse(measurement: BodyMeasurement, input: String): Parsed =
        when {
            input.isBlank() && !measurement.isRequired -> Parsed.Valid(null)
            else -> parseBodyMeasurement(input)?.let { Parsed.Valid(it) } ?: Parsed.Invalid
        }

    private sealed interface Parsed {
        data class Valid(val valueInCm: Double?) : Parsed
        data object Invalid : Parsed
    }

    sealed interface Result {
        data object Success : Result

        /** All measurements whose input is missing or out of range. */
        data class Invalid(val measurements: Set<BodyMeasurement>) : Result
    }
}
