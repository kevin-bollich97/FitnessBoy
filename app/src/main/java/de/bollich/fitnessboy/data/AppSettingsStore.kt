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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.IOException

class AppSettingsStore(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {
    override fun observeSettings(): Flow<AppSettings> =
        dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                AppSettings(
                    selectedTabName = preferences[selectedTabKey] ?: AppSettings().selectedTabName,
                    selectedWeightPageName = preferences[selectedWeightPageKey] ?: AppSettings().selectedWeightPageName,
                )
            }

    override suspend fun save(settings: AppSettings) {
        dataStore.edit { preferences ->
            preferences[selectedTabKey] = settings.selectedTabName
            preferences[selectedWeightPageKey] = settings.selectedWeightPageName
        }
    }

    companion object {
        private val selectedTabKey = stringPreferencesKey("selected_tab")
        private val selectedWeightPageKey = stringPreferencesKey("selected_weight_page")

        /** Must be called only once per process; DataStore forbids multiple instances per file. */
        fun create(context: Context): AppSettingsStore =
            AppSettingsStore(
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
                ),
            )
    }
}
