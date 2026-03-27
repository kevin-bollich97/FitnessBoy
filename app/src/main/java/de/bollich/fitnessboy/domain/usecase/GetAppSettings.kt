package de.bollich.fitnessboy.domain.usecase

import de.bollich.fitnessboy.domain.repository.SettingsRepository
import de.bollich.fitnessboy.model.AppSettings

class GetAppSettings(
    private val settingsRepository: SettingsRepository,
) {
    operator fun invoke(): AppSettings = settingsRepository.load()
}
