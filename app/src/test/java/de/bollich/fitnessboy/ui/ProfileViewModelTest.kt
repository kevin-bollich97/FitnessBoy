package de.bollich.fitnessboy.ui

import de.bollich.fitnessboy.domain.usecase.SaveProfile
import de.bollich.fitnessboy.model.UserProfile
import de.bollich.fitnessboy.testing.FakeProfileRepository
import de.bollich.fitnessboy.testing.FakeWeightRepository
import de.bollich.fitnessboy.testing.MainDispatcherRule
import de.bollich.fitnessboy.ui.profile.ProfileViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test

class ProfileViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val profileRepository = FakeProfileRepository().apply {
        profile.value = UserProfile(heightInCm = 180.0, targetWeightInKg = 75.5)
    }
    private val viewModel by lazy {
        ProfileViewModel(
            profileRepository = profileRepository,
            weightRepository = FakeWeightRepository(),
            saveProfile = SaveProfile(profileRepository),
        )
    }

    private fun TestScope.collectUiState() {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
    }

    @Test
    fun form_isPrefilledFromStoredProfile() = runTest {
        collectUiState()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("180", state.form.heightInput)
    }

    @Test
    fun profileUpdates_doNotOverwriteUserInput() = runTest {
        collectUiState()
        viewModel.onHeightValueChange("17")

        profileRepository.profile.value = UserProfile(heightInCm = 190.0)

        val state = viewModel.uiState.value
        assertEquals("17", state.form.heightInput)
        assertEquals(190.0, state.profile.heightInCm!!, 0.0)
    }

    @Test
    fun saveValidProfile_persistsAndNormalizesInput() = runTest {
        collectUiState()
        viewModel.onHeightValueChange("175,0")
        viewModel.onTargetWeightValueChange("")

        viewModel.onSaveClick()

        assertEquals(UserProfile(heightInCm = 175.0), profileRepository.profile.value)
        assertEquals("175", viewModel.uiState.value.form.heightInput)
    }

    @Test
    fun saveInvalidHeight_showsErrorWithoutSaving() = runTest {
        collectUiState()
        viewModel.onHeightValueChange("abc")

        viewModel.onSaveClick()

        assertNotNull(viewModel.uiState.value.form.heightErrorRes)
        assertEquals(180.0, profileRepository.profile.value.heightInCm!!, 0.0)
    }
}
