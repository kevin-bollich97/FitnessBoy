package de.bollich.fitnessboy.domain.usecase

import de.bollich.fitnessboy.domain.repository.WeightRepository
import de.bollich.fitnessboy.model.WeightEntry

class DeleteWeightEntry(
    private val weightRepository: WeightRepository,
) {
    suspend operator fun invoke(entry: WeightEntry) {
        weightRepository.delete(entry)
    }
}
