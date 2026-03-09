package de.bollich.fitnessboy.data

import android.content.Context
import de.bollich.fitnessboy.model.UserProfile

class ProfileStore(private val context: Context) {
    fun load(): UserProfile {
        val preferences = preferences
        val heightInCm = preferences.getString(heightKey, null)?.toDoubleOrNull()
        val targetWeightInKg = preferences.getString(targetWeightKey, null)?.toDoubleOrNull()
        return UserProfile(
            heightInCm = heightInCm,
            targetWeightInKg = targetWeightInKg,
        )
    }

    fun save(profile: UserProfile) {
        preferences
            .edit()
            .putString(heightKey, profile.heightInCm?.toString())
            .putString(targetWeightKey, profile.targetWeightInKg?.toString())
            .apply()
    }

    private val preferences
        get() = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)

    private companion object {
        const val preferencesName = "user_profile"
        const val heightKey = "height_in_cm"
        const val targetWeightKey = "target_weight_in_kg"
    }
}
