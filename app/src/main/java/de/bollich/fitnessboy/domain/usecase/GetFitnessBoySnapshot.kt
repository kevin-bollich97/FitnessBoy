package de.bollich.fitnessboy.domain.usecase

import de.bollich.fitnessboy.domain.repository.BodyMeasurementsRepository
import de.bollich.fitnessboy.domain.repository.ProfileRepository
import de.bollich.fitnessboy.domain.repository.WeightRepository
import de.bollich.fitnessboy.model.BodyMeasurementsEntry
import de.bollich.fitnessboy.model.UserProfile
import de.bollich.fitnessboy.model.WeightEntry

class GetFitnessBoySnapshot(
    private val weightRepository: WeightRepository,
    private val profileRepository: ProfileRepository,
    private val bodyMeasurementsRepository: BodyMeasurementsRepository,
) {
    operator fun invoke(): Snapshot =
        Snapshot(
            entries = weightRepository.load(),
            profile = profileRepository.load(),
            bodyMeasurementsEntries = bodyMeasurementsRepository.load(),
        )

    data class Snapshot(
        val entries: List<WeightEntry>,
        val profile: UserProfile,
        val bodyMeasurementsEntries: List<BodyMeasurementsEntry>,
    )
}
