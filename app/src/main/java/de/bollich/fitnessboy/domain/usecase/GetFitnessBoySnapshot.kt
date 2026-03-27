package de.bollich.fitnessboy.domain.usecase

import de.bollich.fitnessboy.domain.repository.ProfileRepository
import de.bollich.fitnessboy.domain.repository.WeightRepository
import de.bollich.fitnessboy.model.UserProfile
import de.bollich.fitnessboy.model.WeightEntry

class GetFitnessBoySnapshot(
    private val weightRepository: WeightRepository,
    private val profileRepository: ProfileRepository,
) {
    operator fun invoke(): Snapshot =
        Snapshot(
            entries = weightRepository.load(),
            profile = profileRepository.load(),
        )

    data class Snapshot(
        val entries: List<WeightEntry>,
        val profile: UserProfile,
    )
}
