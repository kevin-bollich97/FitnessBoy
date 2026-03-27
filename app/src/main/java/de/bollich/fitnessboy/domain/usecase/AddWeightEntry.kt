package de.bollich.fitnessboy.domain.usecase

import de.bollich.fitnessboy.domain.parseWeight
import de.bollich.fitnessboy.domain.repository.WeightRepository
import de.bollich.fitnessboy.model.WeightEntry
import java.time.LocalDate

class AddWeightEntry(
    private val weightRepository: WeightRepository,
) {
    operator fun invoke(
        entries: List<WeightEntry>,
        weightInput: String,
        selectedDate: LocalDate,
    ): Result {
        val parsedWeight = parseWeight(weightInput)
            ?: return Result.InvalidWeight("Bitte ein gültiges Gewicht eingeben.")

        val updatedEntries = (
            entries + WeightEntry(
                date = selectedDate,
                weightInKg = parsedWeight,
            )
        ).sortedByDescending(WeightEntry::date)

        weightRepository.save(updatedEntries)
        return Result.Success(updatedEntries)
    }

    sealed interface Result {
        data class Success(val entries: List<WeightEntry>) : Result
        data class InvalidWeight(val message: String) : Result
    }
}
