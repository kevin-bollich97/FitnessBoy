package de.bollich.fitnessboy.ui

import de.bollich.fitnessboy.domain.BodyMeasurement.CHEST
import de.bollich.fitnessboy.domain.BodyMeasurement.HIPS
import de.bollich.fitnessboy.domain.BodyMeasurement.SHOULDERS
import de.bollich.fitnessboy.domain.BodyMeasurement.UPPER_ARM
import de.bollich.fitnessboy.domain.BodyMeasurement.WAIST
import de.bollich.fitnessboy.domain.ChartGrouping
import de.bollich.fitnessboy.domain.ChartPeriod
import de.bollich.fitnessboy.domain.usecase.AddBodyMeasurementsEntry
import de.bollich.fitnessboy.model.BodyMeasurementsEntry
import de.bollich.fitnessboy.model.UserProfile
import de.bollich.fitnessboy.model.WeightEntry
import de.bollich.fitnessboy.testing.FakeBodyMeasurementsRepository
import de.bollich.fitnessboy.testing.FakeProfileRepository
import de.bollich.fitnessboy.testing.FakeWeightRepository
import de.bollich.fitnessboy.testing.MainDispatcherRule
import de.bollich.fitnessboy.ui.measurements.BodyMeasurementsViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.LocalDate

class BodyMeasurementsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeBodyMeasurementsRepository()
    private val weightRepository = FakeWeightRepository()
    private val profileRepository = FakeProfileRepository()
    private val viewModel by lazy {
        BodyMeasurementsViewModel(
            repository = repository,
            weightRepository = weightRepository,
            profileRepository = profileRepository,
            addBodyMeasurementsEntry = AddBodyMeasurementsEntry(repository),
            today = { today },
        )
    }
    private val today = LocalDate.of(2026, 3, 31)

    private suspend fun addMeasurements(date: LocalDate, waistInCm: Double, upperArmInCm: Double? = null) {
        repository.add(
            BodyMeasurementsEntry(
                date = date,
                waistInCm = waistInCm,
                hipsInCm = 100.0,
                shouldersInCm = 50.0,
                upperArmInCm = upperArmInCm,
            )
        )
    }

    private fun TestScope.collectUiState() {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
    }

    private fun enterRequiredMeasurements() {
        viewModel.onValueChange(WAIST, "88")
        viewModel.onValueChange(HIPS, "96")
        viewModel.onValueChange(SHOULDERS, "48")
    }

    @Test
    fun saveValidMeasurements_addsEntryAndClosesSheet() = runTest {
        collectUiState()
        viewModel.onSheetVisibilityChange(true)
        enterRequiredMeasurements()
        viewModel.onValueChange(UPPER_ARM, "35")

        viewModel.onSaveClick()

        val state = viewModel.uiState.value
        assertEquals(88.0, state.latestEntry!!.waistInCm, 0.0)
        assertEquals(35.0, state.latestEntry!!.upperArmInCm!!, 0.0)
        assertNull(state.latestEntry!!.chestInCm)
        assertFalse(state.form.isSheetVisible)
        assertTrue(state.form.inputs.isEmpty())
    }

    @Test
    fun saveInvalidMeasurements_keepsSheetOpenAndMarksAllInvalidFields() = runTest {
        collectUiState()
        viewModel.onSheetVisibilityChange(true)
        viewModel.onValueChange(WAIST, "88")
        viewModel.onValueChange(CHEST, "abc")

        viewModel.onSaveClick()

        val state = viewModel.uiState.value
        assertTrue(state.form.isSheetVisible)
        assertEquals(setOf(HIPS, SHOULDERS, CHEST), state.form.invalidMeasurements)
        assertTrue(state.items.isEmpty())
    }

    @Test
    fun editingField_clearsOnlyItsError() = runTest {
        collectUiState()
        viewModel.onSaveClick()

        viewModel.onValueChange(WAIST, "8")

        assertEquals(setOf(HIPS, SHOULDERS), viewModel.uiState.value.form.invalidMeasurements)
    }

    @Test
    fun dismissSheet_clearsErrorsAndDateButKeepsInput() = runTest {
        collectUiState()
        viewModel.onSheetVisibilityChange(true)
        viewModel.onValueChange(WAIST, "x")
        viewModel.onDateChange(LocalDate.of(2026, 3, 1))
        viewModel.onSaveClick()

        viewModel.onSheetVisibilityChange(false)

        val form = viewModel.uiState.value.form
        assertFalse(form.isSheetVisible)
        assertTrue(form.invalidMeasurements.isEmpty())
        assertEquals(LocalDate.now(), form.selectedDate)
        assertEquals("x", form.inputs[WAIST])
    }

    @Test
    fun items_haveBmiFromLatestWeightUpToTheirDate() = runTest {
        collectUiState()
        profileRepository.save(UserProfile(heightInCm = 180.0))
        weightRepository.add(WeightEntry(date = LocalDate.of(2026, 3, 1), weightInKg = 81.0))
        weightRepository.add(WeightEntry(date = LocalDate.of(2026, 3, 20), weightInKg = 77.76))
        addMeasurements(LocalDate.of(2026, 2, 1), waistInCm = 92.0)
        addMeasurements(LocalDate.of(2026, 3, 10), waistInCm = 90.0)
        addMeasurements(LocalDate.of(2026, 3, 25), waistInCm = 88.0)

        val bmis = viewModel.uiState.value.items.map { it.bmi }
        assertEquals(24.0, bmis[0]!!, 0.01)
        assertEquals(25.0, bmis[1]!!, 0.01)
        assertNull(bmis[2])
    }

    @Test
    fun chart_showsChosenMeasurementAndSelectsNewestPoint() = runTest {
        collectUiState()
        addMeasurements(LocalDate.of(2026, 3, 1), waistInCm = 90.0, upperArmInCm = 35.0)
        addMeasurements(LocalDate.of(2026, 3, 10), waistInCm = 88.0)

        var state = viewModel.uiState.value
        assertEquals(WAIST, state.chartMeasurement)
        assertEquals(listOf(90.0, 88.0), state.chartGroups.map { it.average })
        assertEquals(state.chartGroups.last(), state.selectedGroup)

        viewModel.onChartMeasurementChange(UPPER_ARM)

        state = viewModel.uiState.value
        assertTrue(state.chartMeasurementHasValues)
        assertEquals(listOf(35.0), state.chartGroups.map { it.average })
        assertEquals(state.chartGroups.single(), state.selectedGroup)
    }

    @Test
    fun chart_reportsMeasurementWithoutValues() = runTest {
        collectUiState()
        addMeasurements(LocalDate.of(2026, 3, 1), waistInCm = 90.0)

        viewModel.onChartMeasurementChange(CHEST)

        assertFalse(viewModel.uiState.value.chartMeasurementHasValues)
        assertTrue(viewModel.uiState.value.chartGroups.isEmpty())
    }

    @Test
    fun chartTapAndPeriod_controlSelection() = runTest {
        collectUiState()
        addMeasurements(LocalDate.of(2025, 12, 1), waistInCm = 94.0)
        addMeasurements(LocalDate.of(2026, 3, 10), waistInCm = 90.0)
        addMeasurements(LocalDate.of(2026, 3, 25), waistInCm = 88.0)

        val first = viewModel.uiState.value.chartGroups.first()
        viewModel.onChartGroupSelected(first)
        assertEquals(first, viewModel.uiState.value.selectedGroup)

        viewModel.onChartPeriodChange(ChartPeriod.ONE_MONTH)
        val state = viewModel.uiState.value
        assertEquals(listOf(90.0, 88.0), state.chartGroups.map { it.average })
        assertEquals(state.chartGroups.last(), state.selectedGroup)
    }

    @Test
    fun selectedGroupBmi_usesWeightOnThatDate() = runTest {
        collectUiState()
        profileRepository.save(UserProfile(heightInCm = 180.0))
        weightRepository.add(WeightEntry(date = LocalDate.of(2026, 3, 1), weightInKg = 81.0))
        addMeasurements(LocalDate.of(2026, 3, 10), waistInCm = 90.0)

        assertEquals(25.0, viewModel.uiState.value.selectedGroupBmi!!, 0.01)
    }

    @Test
    fun selectedGroupBmi_usesEntryDateInsideWeeklyGroup() = runTest {
        collectUiState()
        profileRepository.save(UserProfile(heightInCm = 180.0))
        weightRepository.add(WeightEntry(date = LocalDate.of(2025, 6, 1), weightInKg = 90.0))
        weightRepository.add(WeightEntry(date = LocalDate.of(2026, 3, 27), weightInKg = 81.0)) // Friday
        addMeasurements(LocalDate.of(2025, 9, 1), waistInCm = 95.0)
        addMeasurements(LocalDate.of(2026, 3, 28), waistInCm = 90.0) // Saturday, week starts 23.03.

        val selected = viewModel.uiState.value.selectedGroup!!
        assertEquals(ChartGrouping.WEEK, selected.grouping)
        assertEquals(LocalDate.of(2026, 3, 28), selected.lastDate)
        assertEquals(25.0, viewModel.uiState.value.selectedGroupBmi!!, 0.01)
    }

    @Test
    fun period_filtersListButKeepsOverallLatestEntry() = runTest {
        collectUiState()
        addMeasurements(LocalDate.of(2025, 12, 1), waistInCm = 94.0)
        addMeasurements(LocalDate.of(2026, 1, 15), waistInCm = 92.0)

        viewModel.onChartPeriodChange(ChartPeriod.ONE_MONTH)

        val state = viewModel.uiState.value
        assertTrue(state.items.isEmpty())
        assertTrue(state.entrySections.isEmpty())
        assertEquals(LocalDate.of(2026, 1, 15), state.latestEntry!!.date)
    }

    @Test
    fun showAllEntries_toggles() = runTest {
        collectUiState()

        viewModel.onShowAllEntriesToggle()
        assertTrue(viewModel.uiState.value.showAllEntries)

        viewModel.onShowAllEntriesToggle()
        assertFalse(viewModel.uiState.value.showAllEntries)
    }

    @Test
    fun delete_requiresConfirmation() = runTest {
        collectUiState()
        addMeasurements(LocalDate.of(2026, 3, 1), waistInCm = 90.0)
        val entry = viewModel.uiState.value.latestEntry!!

        viewModel.onDeleteRequest(entry)
        assertEquals(entry, viewModel.uiState.value.entryPendingDeletion)
        assertEquals(1, viewModel.uiState.value.items.size)

        viewModel.onDeleteConfirm()

        assertNull(viewModel.uiState.value.entryPendingDeletion)
        assertTrue(viewModel.uiState.value.items.isEmpty())
    }

    @Test
    fun cancellingDelete_keepsEntry() = runTest {
        collectUiState()
        addMeasurements(LocalDate.of(2026, 3, 1), waistInCm = 90.0)

        viewModel.onDeleteRequest(viewModel.uiState.value.latestEntry)
        viewModel.onDeleteRequest(null)

        assertNull(viewModel.uiState.value.entryPendingDeletion)
        assertEquals(1, viewModel.uiState.value.items.size)
    }
}
