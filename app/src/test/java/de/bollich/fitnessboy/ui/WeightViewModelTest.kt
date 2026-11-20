package de.bollich.fitnessboy.ui

import de.bollich.fitnessboy.domain.usecase.AddWeightEntry
import de.bollich.fitnessboy.domain.usecase.GetHealthOverview
import de.bollich.fitnessboy.model.UserProfile
import de.bollich.fitnessboy.model.WeightEntry
import de.bollich.fitnessboy.testing.FakeProfileRepository
import de.bollich.fitnessboy.testing.FakeWeightRepository
import de.bollich.fitnessboy.testing.MainDispatcherRule
import de.bollich.fitnessboy.ui.weight.WeightViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class WeightViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val weightRepository = FakeWeightRepository()
    private val profileRepository = FakeProfileRepository()
    private val viewModel by lazy {
        WeightViewModel(
            weightRepository = weightRepository,
            profileRepository = profileRepository,
            addWeightEntry = AddWeightEntry(weightRepository),
            getHealthOverview = GetHealthOverview(),
        )
    }

    private fun TestScope.collectUiState() {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
    }

    @Test
    fun uiState_derivesOverviewFromRepositories() = runTest {
        collectUiState()
        profileRepository.save(UserProfile(heightInCm = 180.0))
        weightRepository.add(WeightEntry(date = LocalDate.of(2026, 3, 1), weightInKg = 81.0))

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(81.0, state.latestEntry!!.weightInKg, 0.0)
        assertEquals(25.0, state.bmi!!, 0.01)
        assertEquals("Übergewicht", state.bmiCategory)
    }

    @Test
    fun addValidWeight_savesEntryAndResetsForm() = runTest {
        collectUiState()
        val date = LocalDate.of(2026, 3, 1)
        viewModel.onWeightValueChange("80,5")
        viewModel.onSelectedDateChange(date)

        viewModel.onAddWeightClick()

        val state = viewModel.uiState.value
        assertEquals(80.5, state.entries.single().weightInKg, 0.0)
        assertEquals(date, state.entries.single().date)
        assertEquals("", state.form.weightInput)
        assertNull(state.form.errorText)
    }

    @Test
    fun addInvalidWeight_showsErrorAndKeepsInput() = runTest {
        collectUiState()
        viewModel.onWeightValueChange("abc")

        viewModel.onAddWeightClick()

        val state = viewModel.uiState.value
        assertEquals("abc", state.form.weightInput)
        assertNotNull(state.form.errorText)
        assertEquals(emptyList<Any>(), state.entries)
    }

    @Test
    fun editingInput_clearsError() = runTest {
        collectUiState()
        viewModel.onAddWeightClick()

        viewModel.onWeightValueChange("8")

        assertNull(viewModel.uiState.value.form.errorText)
    }

    @Test
    fun deleteEntry_removesOnlyThatEntry() = runTest {
        collectUiState()
        repeat(2) {
            viewModel.onWeightValueChange("80")
            viewModel.onAddWeightClick()
        }
        val (first, second) = viewModel.uiState.value.entries

        viewModel.onDeleteEntry(first)

        assertEquals(listOf(second), viewModel.uiState.value.entries)
    }
}
