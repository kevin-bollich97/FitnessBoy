package de.bollich.fitnessboy.ui

import de.bollich.fitnessboy.domain.ChartPeriod
import de.bollich.fitnessboy.domain.usecase.AddWeightEntry
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
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

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
            today = { today },
        )
    }
    private val today = LocalDate.of(2026, 3, 31)

    private fun TestScope.collectUiState() {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
    }

    private suspend fun addWeight(date: LocalDate, weightInKg: Double) {
        weightRepository.add(WeightEntry(date = date, weightInKg = weightInKg))
    }

    @Test
    fun items_areNewestFirstWithBmiFromCurrentHeight() = runTest {
        collectUiState()
        profileRepository.save(UserProfile(heightInCm = 180.0, targetWeightInKg = 75.0))
        addWeight(LocalDate.of(2026, 3, 1), 81.0)
        addWeight(LocalDate.of(2026, 3, 8), 77.76)

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals(listOf(77.76, 81.0), state.items.map { it.entry.weightInKg })
        assertEquals(24.0, state.items[0].bmi!!, 0.01)
        assertEquals(25.0, state.items[1].bmi!!, 0.01)
        assertEquals(75.0, state.targetWeightInKg!!, 0.0)
    }

    @Test
    fun items_haveNoBmiWithoutHeight() = runTest {
        collectUiState()
        addWeight(LocalDate.of(2026, 3, 1), 81.0)

        assertNull(viewModel.uiState.value.items.single().bmi)
    }

    @Test
    fun chart_defaultsToAllAndSelectsNewestPoint() = runTest {
        collectUiState()
        addWeight(LocalDate.of(2026, 3, 1), 81.0)
        addWeight(LocalDate.of(2026, 3, 8), 80.0)

        val state = viewModel.uiState.value
        assertEquals(ChartPeriod.ALL, state.chartPeriod)
        assertEquals(listOf(81.0, 80.0), state.chartGroups.map { it.average })
        assertEquals(state.chartGroups.last(), state.selectedGroup)
    }

    @Test
    fun chartTap_selectsThatPoint() = runTest {
        collectUiState()
        addWeight(LocalDate.of(2026, 3, 1), 81.0)
        addWeight(LocalDate.of(2026, 3, 8), 80.0)
        val oldest = viewModel.uiState.value.chartGroups.first()

        viewModel.onChartGroupSelected(oldest)

        assertEquals(oldest, viewModel.uiState.value.selectedGroup)
    }

    @Test
    fun selection_fallsBackToNewestWhenSelectedPointDisappears() = runTest {
        collectUiState()
        addWeight(LocalDate.of(2026, 3, 1), 81.0)
        addWeight(LocalDate.of(2026, 3, 8), 80.0)
        viewModel.onChartGroupSelected(viewModel.uiState.value.chartGroups.first())

        viewModel.onDeleteRequest(viewModel.uiState.value.items.last().entry)
        viewModel.onDeleteConfirm()

        val state = viewModel.uiState.value
        assertEquals(listOf(LocalDate.of(2026, 3, 8)), state.chartGroups.map { it.start })
        assertEquals(state.chartGroups.single(), state.selectedGroup)
    }

    @Test
    fun chartPeriod_filtersPointsAndResetsSelection() = runTest {
        collectUiState()
        addWeight(LocalDate.of(2025, 12, 1), 85.0)
        addWeight(LocalDate.of(2026, 3, 20), 81.0)
        addWeight(LocalDate.of(2026, 3, 30), 80.0)
        viewModel.onChartGroupSelected(viewModel.uiState.value.chartGroups.first())

        viewModel.onChartPeriodChange(ChartPeriod.ONE_MONTH)

        val state = viewModel.uiState.value
        assertEquals(listOf(LocalDate.of(2026, 3, 20), LocalDate.of(2026, 3, 30)), state.chartGroups.map { it.start })
        assertEquals(state.chartGroups.last(), state.selectedGroup)
        assertEquals(listOf(80.0, 81.0), state.items.map { it.entry.weightInKg })
        assertTrue(state.hasEntries)
    }

    @Test
    fun emptyPeriod_keepsHasEntries() = runTest {
        collectUiState()
        addWeight(LocalDate.of(2025, 12, 1), 85.0)

        viewModel.onChartPeriodChange(ChartPeriod.ONE_MONTH)

        val state = viewModel.uiState.value
        assertTrue(state.hasEntries)
        assertTrue(state.items.isEmpty())
        assertTrue(state.chartGroups.isEmpty())
    }

    @Test
    fun entrySections_areGroupedByMonthAndShortenedUntilExpanded() = runTest {
        collectUiState()
        addWeight(LocalDate.of(2026, 2, 20), 83.0)
        addWeight(LocalDate.of(2026, 3, 1), 82.0)
        addWeight(LocalDate.of(2026, 3, 10), 81.0)
        addWeight(LocalDate.of(2026, 3, 20), 80.0)

        var sections = viewModel.uiState.value.entrySections
        assertEquals(listOf(YearMonth.of(2026, 3)), sections.map { it.month })
        assertEquals(3, sections.single().items.size)

        viewModel.onShowAllEntriesToggle()

        sections = viewModel.uiState.value.entrySections
        assertEquals(listOf(YearMonth.of(2026, 3), YearMonth.of(2026, 2)), sections.map { it.month })
        assertEquals(listOf(3, 1), sections.map { it.items.size })
    }

    @Test
    fun selectedGroupBmi_onlyForPointsFromASingleEntry() = runTest {
        collectUiState()
        profileRepository.save(UserProfile(heightInCm = 180.0))
        addWeight(LocalDate.of(2026, 3, 1), 81.0)
        addWeight(LocalDate.of(2026, 3, 8), 80.0)
        addWeight(LocalDate.of(2026, 3, 8), 78.0)

        assertNull(viewModel.uiState.value.selectedGroupBmi)

        viewModel.onChartGroupSelected(viewModel.uiState.value.chartGroups.first())
        assertEquals(25.0, viewModel.uiState.value.selectedGroupBmi!!, 0.01)
    }

    @Test
    fun showAllEntries_toggles() = runTest {
        collectUiState()
        assertFalse(viewModel.uiState.value.showAllEntries)

        viewModel.onShowAllEntriesToggle()
        assertTrue(viewModel.uiState.value.showAllEntries)

        viewModel.onShowAllEntriesToggle()
        assertFalse(viewModel.uiState.value.showAllEntries)
    }

    @Test
    fun addValidWeight_savesEntryResetsFormAndClosesSheet() = runTest {
        collectUiState()
        val date = LocalDate.of(2026, 3, 1)
        viewModel.onAddWeightSheetVisibilityChange(true)
        viewModel.onWeightValueChange("80,5")
        viewModel.onSelectedDateChange(date)

        viewModel.onAddWeightClick()

        val state = viewModel.uiState.value
        assertEquals(80.5, state.items.single().entry.weightInKg, 0.0)
        assertEquals(date, state.items.single().entry.date)
        assertEquals("", state.form.weightInput)
        assertNull(state.form.errorRes)
        assertFalse(state.form.isSheetVisible)
    }

    @Test
    fun addInvalidWeight_showsErrorAndKeepsInput() = runTest {
        collectUiState()
        viewModel.onWeightValueChange("abc")

        viewModel.onAddWeightClick()

        val state = viewModel.uiState.value
        assertEquals("abc", state.form.weightInput)
        assertNotNull(state.form.errorRes)
        assertEquals(emptyList<Any>(), state.items)
    }

    @Test
    fun closingSheet_keepsInputButResetsDateAndError() = runTest {
        collectUiState()
        viewModel.onAddWeightSheetVisibilityChange(true)
        viewModel.onWeightValueChange("abc")
        viewModel.onSelectedDateChange(LocalDate.of(2026, 3, 1))
        viewModel.onAddWeightClick()

        viewModel.onAddWeightSheetVisibilityChange(false)

        val form = viewModel.uiState.value.form
        assertFalse(form.isSheetVisible)
        assertEquals("abc", form.weightInput)
        assertEquals(LocalDate.now(), form.selectedDate)
        assertNull(form.errorRes)
    }

    @Test
    fun editingInput_clearsError() = runTest {
        collectUiState()
        viewModel.onAddWeightClick()

        viewModel.onWeightValueChange("8")

        assertNull(viewModel.uiState.value.form.errorRes)
    }

    @Test
    fun delete_requiresConfirmationAndRemovesOnlyThatEntry() = runTest {
        collectUiState()
        addWeight(LocalDate.of(2026, 3, 1), 81.0)
        addWeight(LocalDate.of(2026, 3, 1), 81.0)
        val (first, second) = viewModel.uiState.value.items

        viewModel.onDeleteRequest(first.entry)
        assertEquals(first.entry, viewModel.uiState.value.entryPendingDeletion)
        assertEquals(2, viewModel.uiState.value.items.size)

        viewModel.onDeleteConfirm()

        assertNull(viewModel.uiState.value.entryPendingDeletion)
        assertEquals(listOf(second), viewModel.uiState.value.items)
    }

    @Test
    fun cancellingDelete_keepsEntry() = runTest {
        collectUiState()
        addWeight(LocalDate.of(2026, 3, 1), 81.0)
        val item = viewModel.uiState.value.items.single()

        viewModel.onDeleteRequest(item.entry)
        viewModel.onDeleteRequest(null)

        assertNull(viewModel.uiState.value.entryPendingDeletion)
        assertEquals(listOf(item), viewModel.uiState.value.items)
    }
}
