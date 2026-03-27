package de.bollich.fitnessboy.domain.usecase

import de.bollich.fitnessboy.domain.parseHeight
import de.bollich.fitnessboy.domain.parseOptionalWeight
import de.bollich.fitnessboy.domain.repository.ProfileRepository
import de.bollich.fitnessboy.format.formatNumber
import de.bollich.fitnessboy.model.UserProfile

class SaveProfile(
    private val profileRepository: ProfileRepository,
) {
    operator fun invoke(
        profile: UserProfile,
        heightInput: String,
        targetWeightInput: String,
    ): Result {
        val parsedHeight = parseHeight(heightInput)
            ?: return Result.InvalidHeight("Bitte eine gültige Größe in cm eingeben.")

        val parsedTargetWeight = parseOptionalWeight(targetWeightInput)
        if (targetWeightInput.isNotBlank() && parsedTargetWeight == null) {
            return Result.InvalidTargetWeight("Bitte ein gültiges Zielgewicht eingeben.")
        }

        val updatedProfile = profile.copy(
            heightInCm = parsedHeight,
            targetWeightInKg = parsedTargetWeight,
        )
        profileRepository.save(updatedProfile)

        return Result.Success(
            profile = updatedProfile,
            formattedHeight = formatNumber(parsedHeight),
            formattedTargetWeight = parsedTargetWeight?.let(::formatNumber).orEmpty(),
        )
    }

    sealed interface Result {
        data class Success(
            val profile: UserProfile,
            val formattedHeight: String,
            val formattedTargetWeight: String,
        ) : Result

        data class InvalidHeight(val message: String) : Result
        data class InvalidTargetWeight(val message: String) : Result
    }
}
