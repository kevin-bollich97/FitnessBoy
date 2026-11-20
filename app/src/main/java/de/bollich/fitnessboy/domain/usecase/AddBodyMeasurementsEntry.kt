package de.bollich.fitnessboy.domain.usecase

import de.bollich.fitnessboy.domain.parseBodyMeasurement
import de.bollich.fitnessboy.domain.repository.BodyMeasurementsRepository
import de.bollich.fitnessboy.model.BodyMeasurementsEntry
import java.time.LocalDate

class AddBodyMeasurementsEntry(
    private val repository: BodyMeasurementsRepository,
) {
    suspend operator fun invoke(
        date: LocalDate,
        waistInput: String,
        hipsInput: String,
        shouldersInput: String,
    ): Result {
        val waist = parseBodyMeasurement(waistInput)
            ?: return Result.InvalidWaist
        val hips = parseBodyMeasurement(hipsInput)
            ?: return Result.InvalidHips
        val shoulders = parseBodyMeasurement(shouldersInput)
            ?: return Result.InvalidShoulders

        repository.add(
            BodyMeasurementsEntry(
                date = date,
                waistInCm = waist,
                hipsInCm = hips,
                shouldersInCm = shoulders,
            )
        )

        return Result.Success
    }

    sealed interface Result {
        data object Success : Result
        data object InvalidWaist : Result
        data object InvalidHips : Result
        data object InvalidShoulders : Result
    }
}
