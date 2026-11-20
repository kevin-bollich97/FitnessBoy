package de.bollich.fitnessboy.domain.usecase

import de.bollich.fitnessboy.domain.repository.BodyMeasurementsRepository
import de.bollich.fitnessboy.domain.repository.ProfileRepository
import de.bollich.fitnessboy.domain.repository.WeightRepository
import de.bollich.fitnessboy.model.BodyMeasurementsEntry
import de.bollich.fitnessboy.model.UserProfile
import de.bollich.fitnessboy.model.WeightEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class ObserveFitnessBoySnapshot(
    private val weightRepository: WeightRepository,
    private val profileRepository: ProfileRepository,
    private val bodyMeasurementsRepository: BodyMeasurementsRepository,
) {
    operator fun invoke(): Flow<Snapshot> =
        combine(
            weightRepository.observeEntries(),
            profileRepository.observeProfile(),
            bodyMeasurementsRepository.observeEntries(),
            ::Snapshot,
        )

    data class Snapshot(
        val entries: List<WeightEntry>,
        val profile: UserProfile,
        val bodyMeasurementsEntries: List<BodyMeasurementsEntry>,
    )
}
