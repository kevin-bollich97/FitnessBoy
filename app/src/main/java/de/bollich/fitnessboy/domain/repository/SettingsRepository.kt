package de.bollich.fitnessboy.domain.repository

import de.bollich.fitnessboy.model.AppSettings

interface SettingsRepository {
    fun load(): AppSettings
    fun save(settings: AppSettings)
}
