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
import de.bollich.fitnessboy.domain.repository.ProfileRepository
import de.bollich.fitnessboy.model.UserProfile
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.io.IOException

class ProfileDataStore private constructor(
    private val dataStore: DataStore<Preferences>,
) : ProfileRepository {
    override fun load(): UserProfile = runBlocking {
        val preferences = dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .first()

        UserProfile(
            heightInCm = preferences[heightKey]?.toDoubleOrNull(),
            targetWeightInKg = preferences[targetWeightKey]?.toDoubleOrNull(),
        )
    }

    override fun save(profile: UserProfile): Unit = runBlocking {
        dataStore.edit { preferences ->
            updateStringPreference(preferences, heightKey, profile.heightInCm?.toString())
            updateStringPreference(preferences, targetWeightKey, profile.targetWeightInKg?.toString())
        }
    }

    private fun updateStringPreference(
        preferences: androidx.datastore.preferences.core.MutablePreferences,
        key: Preferences.Key<String>,
        value: String?,
    ) {
        if (value == null) {
            preferences.remove(key)
        } else {
            preferences[key] = value
        }
    }

    companion object {
        private val heightKey = stringPreferencesKey("height_in_cm")
        private val targetWeightKey = stringPreferencesKey("target_weight_in_kg")

        @Volatile
        private var instance: ProfileDataStore? = null

        fun getInstance(context: Context): ProfileDataStore =
            instance ?: synchronized(this) {
                instance ?: ProfileDataStore(
                    dataStore = PreferenceDataStoreFactory.create(
                        migrations = listOf(
                            SharedPreferencesMigration(
                                context = context.applicationContext,
                                sharedPreferencesName = "user_profile",
                            )
                        ),
                        produceFile = {
                            context.applicationContext.preferencesDataStoreFile("user_profile")
                        },
                    )
                ).also { instance = it }
            }
    }
}
