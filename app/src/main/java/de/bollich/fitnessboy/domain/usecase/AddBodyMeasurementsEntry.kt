package de.bollich.fitnessboy.domain.usecase

import de.bollich.fitnessboy.domain.parseBodyMeasurement
import de.bollich.fitnessboy.domain.repository.BodyMeasurementsRepository
import de.bollich.fitnessboy.model.BodyMeasurementsEntry
import java.time.LocalDate

class AddBodyMeasurementsEntry(
    private val repository: BodyMeasurementsRepository,
) {
    operator fun invoke(
        date: LocalDate,
        waistInput: String,
        hipsInput: String,
        shouldersInput: String,
    ): Result {
        val waist = parseBodyMeasurement(waistInput)
            ?: return Result.InvalidWaist("Bitte einen gültigen Bauchumfang eingeben.")
        val hips = parseBodyMeasurement(hipsInput)
            ?: return Result.InvalidHips("Bitte einen gültigen Hüftumfang eingeben.")
        val shoulders = parseBodyMeasurement(shouldersInput)
            ?: return Result.InvalidShoulders("Bitte eine gültige Schulterbreite eingeben.")

        val updatedEntries = repository.add(
            BodyMeasurementsEntry(
                date = date,
                waistInCm = waist,
                hipsInCm = hips,
                shouldersInCm = shoulders,
            )
        )

        return Result.Success(updatedEntries)
    }

    sealed interface Result {
        data class Success(val entries: List<BodyMeasurementsEntry>) : Result
        data class InvalidWaist(val message: String) : Result
        data class InvalidHips(val message: String) : Result
        data class InvalidShoulders(val message: String) : Result
    }
}
