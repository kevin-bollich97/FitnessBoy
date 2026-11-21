package de.bollich.fitnessboy.ui

import de.bollich.fitnessboy.domain.BodyMeasurement.CHEST
import de.bollich.fitnessboy.domain.BodyMeasurement.HIPS
import de.bollich.fitnessboy.domain.BodyMeasurement.SHOULDERS
import de.bollich.fitnessboy.domain.BodyMeasurement.UPPER_ARM
import de.bollich.fitnessboy.domain.BodyMeasurement.WAIST
import de.bollich.fitnessboy.domain.usecase.AddBodyMeasurementsEntry
import de.bollich.fitnessboy.testing.FakeBodyMeasurementsRepository
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
    private val viewModel by lazy {
        BodyMeasurementsViewModel(
            repository = repository,
            addBodyMeasurementsEntry = AddBodyMeasurementsEntry(repository),
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
        assertTrue(state.entries.isEmpty())
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
    fun deleteEntry_removesIt() = runTest {
        collectUiState()
        enterRequiredMeasurements()
        viewModel.onSaveClick()

        viewModel.onDeleteEntry(viewModel.uiState.value.latestEntry!!)

        assertTrue(viewModel.uiState.value.entries.isEmpty())
    }
}
