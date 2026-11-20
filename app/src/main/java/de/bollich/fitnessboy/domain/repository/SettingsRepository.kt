package de.bollich.fitnessboy.domain.repository

import de.bollich.fitnessboy.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun observeSettings(): Flow<AppSettings>
    suspend fun save(settings: AppSettings)
}
