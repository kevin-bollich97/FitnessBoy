package de.bollich.fitnessboy.domain.usecase

import de.bollich.fitnessboy.domain.parseWeight
import de.bollich.fitnessboy.domain.repository.WeightRepository
import de.bollich.fitnessboy.model.WeightEntry
import java.time.LocalDate

class AddWeightEntry(
    private val weightRepository: WeightRepository,
) {
    suspend operator fun invoke(
        weightInput: String,
        selectedDate: LocalDate,
    ): Result {
        val parsedWeight = parseWeight(weightInput)
            ?: return Result.InvalidWeight

        weightRepository.add(
            WeightEntry(
                date = selectedDate,
                weightInKg = parsedWeight,
            )
        )
        return Result.Success
    }

    sealed interface Result {
        data object Success : Result
        data object InvalidWeight : Result
    }
}
