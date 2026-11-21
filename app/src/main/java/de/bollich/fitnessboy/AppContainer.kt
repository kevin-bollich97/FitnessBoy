package de.bollich.fitnessboy

import android.content.Context
import de.bollich.fitnessboy.data.AppSettingsStore
import de.bollich.fitnessboy.data.ProfileDataStore
import de.bollich.fitnessboy.data.RoomBodyMeasurementsRepository
import de.bollich.fitnessboy.data.RoomWeightRepository
import de.bollich.fitnessboy.data.local.FitnessBoyDatabase
import de.bollich.fitnessboy.domain.repository.BodyMeasurementsRepository
import de.bollich.fitnessboy.domain.repository.ProfileRepository
import de.bollich.fitnessboy.domain.repository.SettingsRepository
import de.bollich.fitnessboy.domain.repository.WeightRepository

/** Holds the app-wide dependency graph. Created once by [FitnessBoyApplication]. */
class AppContainer(context: Context) {
    private val appContext = context.applicationContext
    private val database by lazy { FitnessBoyDatabase.create(appContext) }

    val weightRepository: WeightRepository by lazy {
        RoomWeightRepository(database.weightEntryDao())
    }

    val bodyMeasurementsRepository: BodyMeasurementsRepository by lazy {
        RoomBodyMeasurementsRepository(database.bodyMeasurementsDao())
    }

    val profileRepository: ProfileRepository by lazy { ProfileDataStore.create(appContext) }

    val settingsRepository: SettingsRepository by lazy { AppSettingsStore.create(appContext) }
}
