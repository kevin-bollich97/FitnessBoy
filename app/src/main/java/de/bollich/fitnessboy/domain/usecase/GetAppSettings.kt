package de.bollich.fitnessboy.domain.usecase

import de.bollich.fitnessboy.domain.repository.SettingsRepository
import de.bollich.fitnessboy.model.AppSettings
import kotlinx.coroutines.flow.first

class GetAppSettings(
    private val settingsRepository: SettingsRepository,
) {
    suspend operator fun invoke(): AppSettings = settingsRepository.observeSettings().first()
}
