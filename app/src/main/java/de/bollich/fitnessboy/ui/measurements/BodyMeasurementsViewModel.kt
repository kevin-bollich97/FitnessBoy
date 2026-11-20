package de.bollich.fitnessboy.ui.measurements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import de.bollich.fitnessboy.domain.repository.BodyMeasurementsRepository
import de.bollich.fitnessboy.domain.usecase.AddBodyMeasurementsEntry
import de.bollich.fitnessboy.model.BodyMeasurementsEntry
import de.bollich.fitnessboy.ui.appContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

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

    fun onDialogVisibilityChange(isVisible: Boolean) {
        form.update {
            if (isVisible) {
                it.copy(isDialogVisible = true)
            } else {
                // Keep typed values, but start fresh on date and errors next time.
                it.copy(
                    isDialogVisible = false,
                    selectedDate = LocalDate.now(),
                    waistErrorText = null,
                    hipsErrorText = null,
                    shouldersErrorText = null,
                )
            }
        }
    }

    fun onDateChange(date: LocalDate) {
        form.update { it.copy(selectedDate = date) }
    }

    fun onWaistValueChange(value: String) {
        form.update { it.copy(waistInput = value, waistErrorText = null) }
    }

    fun onHipsValueChange(value: String) {
        form.update { it.copy(hipsInput = value, hipsErrorText = null) }
    }

    fun onShouldersValueChange(value: String) {
        form.update { it.copy(shouldersInput = value, shouldersErrorText = null) }
    }

    fun onSaveClick() {
        val current = form.value
        viewModelScope.launch {
            val result = addBodyMeasurementsEntry(
                date = current.selectedDate,
                waistInput = current.waistInput,
                hipsInput = current.hipsInput,
                shouldersInput = current.shouldersInput,
            )
            when (result) {
                AddBodyMeasurementsEntry.Result.Success -> form.value = BodyMeasurementsFormState()
                is AddBodyMeasurementsEntry.Result.InvalidWaist ->
                    form.update { it.copy(waistErrorText = result.message) }
                is AddBodyMeasurementsEntry.Result.InvalidHips ->
                    form.update { it.copy(hipsErrorText = result.message) }
                is AddBodyMeasurementsEntry.Result.InvalidShoulders ->
                    form.update { it.copy(shouldersErrorText = result.message) }
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
