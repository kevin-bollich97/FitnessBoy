package de.bollich.fitnessboy.domain.usecase

import de.bollich.fitnessboy.model.UserProfile
import de.bollich.fitnessboy.testing.FakeProfileRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SaveProfileTest {
    private val profileRepository = FakeProfileRepository()
    private val saveProfile = SaveProfile(profileRepository)

    @Test
    fun saveProfile_persistsHeightAndTargetWeight() = runTest {
        val result = saveProfile(UserProfile(), heightInput = "180", targetWeightInput = "75,5")

        assertTrue(result is SaveProfile.Result.Success)
        assertEquals(UserProfile(heightInCm = 180.0, targetWeightInKg = 75.5), profileRepository.profile.value)
    }

    @Test
    fun saveProfile_allowsBlankTargetWeight() = runTest {
        val result = saveProfile(UserProfile(targetWeightInKg = 70.0), heightInput = "180", targetWeightInput = "")

        assertTrue(result is SaveProfile.Result.Success)
        assertNull(profileRepository.profile.value.targetWeightInKg)
    }

    @Test
    fun saveProfile_rejectsInvalidInputWithoutSaving() = runTest {
        assertTrue(saveProfile(UserProfile(), "10", "") is SaveProfile.Result.InvalidHeight)
        assertTrue(saveProfile(UserProfile(), "180", "abc") is SaveProfile.Result.InvalidTargetWeight)
        assertEquals(UserProfile(), profileRepository.profile.value)
    }
}
