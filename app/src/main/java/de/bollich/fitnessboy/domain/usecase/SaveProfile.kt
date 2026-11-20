package de.bollich.fitnessboy.domain.usecase

import de.bollich.fitnessboy.domain.parseHeight
import de.bollich.fitnessboy.domain.parseOptionalWeight
import de.bollich.fitnessboy.domain.repository.ProfileRepository
import de.bollich.fitnessboy.model.UserProfile

class SaveProfile(
    private val profileRepository: ProfileRepository,
) {
    suspend operator fun invoke(
        profile: UserProfile,
        heightInput: String,
        targetWeightInput: String,
    ): Result {
        val parsedHeight = parseHeight(heightInput)
            ?: return Result.InvalidHeight

        val parsedTargetWeight = parseOptionalWeight(targetWeightInput)
        if (targetWeightInput.isNotBlank() && parsedTargetWeight == null) {
            return Result.InvalidTargetWeight
        }

        val updatedProfile = profile.copy(
            heightInCm = parsedHeight,
            targetWeightInKg = parsedTargetWeight,
        )
        profileRepository.save(updatedProfile)

        return Result.Success(updatedProfile)
    }

    sealed interface Result {
        data class Success(val profile: UserProfile) : Result

        data object InvalidHeight : Result
        data object InvalidTargetWeight : Result
    }
}
