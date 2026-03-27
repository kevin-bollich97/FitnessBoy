package de.bollich.fitnessboy.domain.usecase

import de.bollich.fitnessboy.domain.repository.WeightRepository
import de.bollich.fitnessboy.model.WeightEntry

class DeleteWeightEntry(
    private val weightRepository: WeightRepository,
) {
    operator fun invoke(
        entries: List<WeightEntry>,
        entry: WeightEntry,
    ): List<WeightEntry> {
        val updatedEntries = entries - entry
        weightRepository.save(updatedEntries)
        return updatedEntries
    }
}
