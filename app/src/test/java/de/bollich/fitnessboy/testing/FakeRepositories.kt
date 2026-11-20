package de.bollich.fitnessboy.testing

import de.bollich.fitnessboy.domain.repository.BodyMeasurementsRepository
import de.bollich.fitnessboy.domain.repository.ProfileRepository
import de.bollich.fitnessboy.domain.repository.SettingsRepository
import de.bollich.fitnessboy.domain.repository.WeightRepository
import de.bollich.fitnessboy.model.AppSettings
import de.bollich.fitnessboy.model.BodyMeasurementsEntry
import de.bollich.fitnessboy.model.UserProfile
import de.bollich.fitnessboy.model.WeightEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FakeWeightRepository : WeightRepository {
    private var nextId = 1L
    val entries = MutableStateFlow<List<WeightEntry>>(emptyList())

    override fun observeEntries(): Flow<List<WeightEntry>> = entries

    override suspend fun add(entry: WeightEntry) {
        entries.update { current ->
            (current + entry.copy(id = nextId++))
                .sortedWith(compareByDescending<WeightEntry> { it.date }.thenByDescending { it.id })
        }
    }

    override suspend fun delete(entry: WeightEntry) {
        entries.update { current -> current.filterNot { it.id == entry.id } }
    }
}

class FakeBodyMeasurementsRepository : BodyMeasurementsRepository {
    private var nextId = 1L
    val entries = MutableStateFlow<List<BodyMeasurementsEntry>>(emptyList())

    override fun observeEntries(): Flow<List<BodyMeasurementsEntry>> = entries

    override suspend fun add(entry: BodyMeasurementsEntry) {
        entries.update { current ->
            (current + entry.copy(id = nextId++)).sortedByDescending { it.date }
        }
    }

    override suspend fun delete(entry: BodyMeasurementsEntry) {
        entries.update { current -> current.filterNot { it.id == entry.id } }
    }
}

class FakeProfileRepository : ProfileRepository {
    val profile = MutableStateFlow(UserProfile())

    override fun observeProfile(): Flow<UserProfile> = profile

    override suspend fun save(profile: UserProfile) {
        this.profile.value = profile
    }
}

class FakeSettingsRepository(initial: AppSettings = AppSettings()) : SettingsRepository {
    val settings = MutableStateFlow(initial)

    override fun observeSettings(): Flow<AppSettings> = settings

    override suspend fun save(settings: AppSettings) {
        this.settings.value = settings
    }
}
