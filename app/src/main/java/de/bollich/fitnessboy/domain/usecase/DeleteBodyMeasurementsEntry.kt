package de.bollich.fitnessboy.domain.usecase

import de.bollich.fitnessboy.domain.repository.BodyMeasurementsRepository
import de.bollich.fitnessboy.model.BodyMeasurementsEntry

class DeleteBodyMeasurementsEntry(
    private val repository: BodyMeasurementsRepository,
) {
    suspend operator fun invoke(entry: BodyMeasurementsEntry) {
        repository.delete(entry)
    }
}
