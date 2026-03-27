package de.bollich.fitnessboy.domain.usecase

import de.bollich.fitnessboy.domain.repository.BodyMeasurementsRepository
import de.bollich.fitnessboy.model.BodyMeasurementsEntry

class DeleteBodyMeasurementsEntry(
    private val repository: BodyMeasurementsRepository,
) {
    operator fun invoke(entry: BodyMeasurementsEntry): List<BodyMeasurementsEntry> =
        repository.delete(entry)
}
