package de.bollich.fitnessboy.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.SharedPreferencesMigration
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import de.bollich.fitnessboy.domain.repository.SettingsRepository
import de.bollich.fitnessboy.model.AppSettings
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.io.IOException

class AppSettingsStore private constructor(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {
    override fun load(): AppSettings = runBlocking {
        val preferences = dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .first()

        AppSettings(
            selectedTabName = preferences[selectedTabKey] ?: AppSettings().selectedTabName,
            selectedWeightPageName = preferences[selectedWeightPageKey] ?: AppSettings().selectedWeightPageName,
        )
    }

    override fun save(settings: AppSettings): Unit = runBlocking {
        dataStore.edit { preferences ->
            preferences[selectedTabKey] = settings.selectedTabName
            preferences[selectedWeightPageKey] = settings.selectedWeightPageName
        }
    }

    companion object {
        private val selectedTabKey = stringPreferencesKey("selected_tab")
        private val selectedWeightPageKey = stringPreferencesKey("selected_weight_page")

        @Volatile
        private var instance: AppSettingsStore? = null

        fun getInstance(context: Context): AppSettingsStore =
            instance ?: synchronized(this) {
                instance ?: AppSettingsStore(
                    dataStore = PreferenceDataStoreFactory.create(
                        migrations = listOf(
                            SharedPreferencesMigration(
                                context = context.applicationContext,
                                sharedPreferencesName = "app_settings",
                            )
                        ),
                        produceFile = {
                            context.applicationContext.preferencesDataStoreFile("app_settings")
                        },
                    )
                ).also { instance = it }
            }
    }
}
