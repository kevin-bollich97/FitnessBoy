package de.bollich.fitnessboy.domain.repository

import de.bollich.fitnessboy.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun observeProfile(): Flow<UserProfile>
    suspend fun save(profile: UserProfile)
}
