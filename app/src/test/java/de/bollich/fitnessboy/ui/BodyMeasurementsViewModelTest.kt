package de.bollich.fitnessboy.ui

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
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

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

    private fun enterValidMeasurements() {
        viewModel.onWaistValueChange("88")
        viewModel.onHipsValueChange("96")
        viewModel.onShouldersValueChange("48")
    }

    @Test
    fun saveValidMeasurements_addsEntryAndClosesDialog() = runTest {
        collectUiState()
        viewModel.onDialogVisibilityChange(true)
        enterValidMeasurements()

        viewModel.onSaveClick()

        val state = viewModel.uiState.value
        assertEquals(88.0, state.latestEntry!!.waistInCm, 0.0)
        assertFalse(state.form.isDialogVisible)
        assertEquals("", state.form.waistInput)
    }

    @Test
    fun saveInvalidMeasurements_keepsDialogOpenWithError() = runTest {
        collectUiState()
        viewModel.onDialogVisibilityChange(true)
        enterValidMeasurements()
        viewModel.onHipsValueChange("x")

        viewModel.onSaveClick()

        val state = viewModel.uiState.value
        assertTrue(state.form.isDialogVisible)
        assertNotNull(state.form.hipsErrorRes)
        assertTrue(state.entries.isEmpty())
    }

    @Test
    fun dismissDialog_clearsErrorsButKeepsInput() = runTest {
        collectUiState()
        viewModel.onDialogVisibilityChange(true)
        viewModel.onWaistValueChange("x")
        viewModel.onSaveClick()

        viewModel.onDialogVisibilityChange(false)

        val form = viewModel.uiState.value.form
        assertFalse(form.isDialogVisible)
        assertNull(form.waistErrorRes)
        assertEquals("x", form.waistInput)
    }

    @Test
    fun deleteEntry_removesIt() = runTest {
        collectUiState()
        enterValidMeasurements()
        viewModel.onSaveClick()

        viewModel.onDeleteEntry(viewModel.uiState.value.latestEntry!!)

        assertTrue(viewModel.uiState.value.entries.isEmpty())
    }
}
