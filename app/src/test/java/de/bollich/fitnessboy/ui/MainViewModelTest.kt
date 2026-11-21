package de.bollich.fitnessboy.ui

import de.bollich.fitnessboy.model.AppSettings
import de.bollich.fitnessboy.testing.FakeSettingsRepository
import de.bollich.fitnessboy.testing.MainDispatcherRule
import de.bollich.fitnessboy.ui.navigation.AppTab
import de.bollich.fitnessboy.ui.navigation.Destination
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val overviewTab = Destination.Tab(AppTab.OVERVIEW)
    private val profileTab = Destination.Tab(AppTab.PROFILE)

    @Test
    fun restoresPersistedTabOnTopOfStartDestination() = runTest {
        val repository = FakeSettingsRepository(AppSettings(selectedTabName = "PROFILE"))

        val state = MainViewModel(repository).uiState.value

        assertFalse(state.isLoading)
        assertEquals(AppTab.PROFILE, state.selectedTab)
        assertEquals(listOf(overviewTab, profileTab), state.backStack)
    }

    @Test
    fun removedPersistedTab_fallsBackToOverview() = runTest {
        val repository = FakeSettingsRepository(AppSettings(selectedTabName = "BMI"))

        val state = MainViewModel(repository).uiState.value

        assertEquals(listOf(overviewTab), state.backStack)
    }

    @Test
    fun selectingTab_replacesStackAndPersistsIt() = runTest {
        val repository = FakeSettingsRepository()
        val viewModel = MainViewModel(repository)
        viewModel.onTabSelected(AppTab.PROFILE)
        viewModel.navigateTo(Destination.WeightHistory)

        viewModel.onTabSelected(AppTab.MEASUREMENTS)

        assertEquals(
            listOf(overviewTab, Destination.Tab(AppTab.MEASUREMENTS)),
            viewModel.uiState.value.backStack,
        )
        assertEquals("MEASUREMENTS", repository.settings.value.selectedTabName)
    }

    @Test
    fun subPage_keepsTabSelectedAndBackReturnsToIt() = runTest {
        val viewModel = MainViewModel(FakeSettingsRepository())
        viewModel.onTabSelected(AppTab.PROFILE)

        viewModel.navigateTo(Destination.MeasurementsHistory)
        assertEquals(AppTab.PROFILE, viewModel.uiState.value.selectedTab)

        viewModel.onBack()
        assertEquals(listOf(overviewTab, profileTab), viewModel.uiState.value.backStack)
    }

    @Test
    fun backFromOtherTab_returnsToOverviewAndPersistsIt() = runTest {
        val repository = FakeSettingsRepository()
        val viewModel = MainViewModel(repository)
        viewModel.onTabSelected(AppTab.WEIGHT)

        viewModel.onBack()

        assertEquals(listOf(overviewTab), viewModel.uiState.value.backStack)
        assertEquals("OVERVIEW", repository.settings.value.selectedTabName)
    }

    @Test
    fun backOnStartDestination_keepsStack() = runTest {
        val viewModel = MainViewModel(FakeSettingsRepository())

        viewModel.onBack()

        assertEquals(listOf(overviewTab), viewModel.uiState.value.backStack)
    }
}
