package de.bollich.fitnessboy.domain.repository

import de.bollich.fitnessboy.model.UserProfile

interface ProfileRepository {
    fun load(): UserProfile
    fun save(profile: UserProfile)
}
