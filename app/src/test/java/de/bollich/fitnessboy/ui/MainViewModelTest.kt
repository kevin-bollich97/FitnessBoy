package de.bollich.fitnessboy.ui

import de.bollich.fitnessboy.model.AppSettings
import de.bollich.fitnessboy.testing.FakeSettingsRepository
import de.bollich.fitnessboy.testing.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun restoresPersistedTab() = runTest {
        val repository = FakeSettingsRepository(AppSettings(selectedTabName = "PROFILE"))

        val state = MainViewModel(repository).uiState.value

        assertFalse(state.isLoading)
        assertEquals(AppTab.PROFILE, state.selectedTab)
    }

    @Test
    fun unknownPersistedValues_fallBackToDefaults() = runTest {
        val repository = FakeSettingsRepository(
            AppSettings(selectedTabName = "REMOVED_TAB", selectedWeightPageName = "???"),
        )

        val state = MainViewModel(repository).uiState.value

        assertEquals(AppTab.WEIGHT, state.selectedTab)
        assertEquals(WeightPage.DASHBOARD, state.selectedWeightPage)
    }

    @Test
    fun selectingTab_persistsIt() = runTest {
        val repository = FakeSettingsRepository()
        val viewModel = MainViewModel(repository)

        viewModel.onTabSelected(AppTab.MEASUREMENTS)

        assertEquals(AppTab.MEASUREMENTS, viewModel.uiState.value.selectedTab)
        assertEquals("MEASUREMENTS", repository.settings.value.selectedTabName)
    }
}
