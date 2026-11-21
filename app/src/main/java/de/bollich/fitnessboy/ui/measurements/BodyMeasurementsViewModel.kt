package de.bollich.fitnessboy.ui.measurements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import de.bollich.fitnessboy.domain.BodyMeasurement
import de.bollich.fitnessboy.domain.repository.BodyMeasurementsRepository
import de.bollich.fitnessboy.domain.usecase.AddBodyMeasurementsEntry
import de.bollich.fitnessboy.model.BodyMeasurementsEntry
import de.bollich.fitnessboy.ui.appContainer
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BodyMeasurementsViewModel(
    private val repository: BodyMeasurementsRepository,
    private val addBodyMeasurementsEntry: AddBodyMeasurementsEntry,
) : ViewModel() {
    private val form = MutableStateFlow(BodyMeasurementsFormState())

    val uiState: StateFlow<BodyMeasurementsUiState> =
        combine(repository.observeEntries(), form) { entries, form ->
            BodyMeasurementsUiState(isLoading = false, entries = entries, form = form)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = BodyMeasurementsUiState(),
        )

    fun onSheetVisibilityChange(isVisible: Boolean) {
        form.update {
            if (isVisible) {
                it.copy(isSheetVisible = true)
            } else {
                // Keep typed values, but start fresh on date and errors next time.
                it.copy(isSheetVisible = false, selectedDate = LocalDate.now(), invalidMeasurements = emptySet())
            }
        }
    }

    fun onDateChange(date: LocalDate) {
        form.update { it.copy(selectedDate = date) }
    }

    fun onValueChange(measurement: BodyMeasurement, value: String) {
        form.update {
            it.copy(inputs = it.inputs + (measurement to value), invalidMeasurements = it.invalidMeasurements - measurement)
        }
    }

    fun onSaveClick() {
        val current = form.value
        viewModelScope.launch {
            when (val result = addBodyMeasurementsEntry(current.selectedDate, current.inputs)) {
                AddBodyMeasurementsEntry.Result.Success -> form.value = BodyMeasurementsFormState()
                is AddBodyMeasurementsEntry.Result.Invalid ->
                    form.update { it.copy(invalidMeasurements = result.measurements) }
            }
        }
    }

    fun onDeleteEntry(entry: BodyMeasurementsEntry) {
        viewModelScope.launch { repository.delete(entry) }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val repository = appContainer().bodyMeasurementsRepository
                BodyMeasurementsViewModel(
                    repository = repository,
                    addBodyMeasurementsEntry = AddBodyMeasurementsEntry(repository),
                )
            }
        }
    }
}
