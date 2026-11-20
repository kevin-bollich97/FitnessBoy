package de.bollich.fitnessboy.ui.measurements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import de.bollich.fitnessboy.R
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

    fun onDialogVisibilityChange(isVisible: Boolean) {
        form.update {
            if (isVisible) {
                it.copy(isDialogVisible = true)
            } else {
                // Keep typed values, but start fresh on date and errors next time.
                it.copy(
                    isDialogVisible = false,
                    selectedDate = LocalDate.now(),
                    waistErrorRes = null,
                    hipsErrorRes = null,
                    shouldersErrorRes = null,
                )
            }
        }
    }

    fun onDateChange(date: LocalDate) {
        form.update { it.copy(selectedDate = date) }
    }

    fun onWaistValueChange(value: String) {
        form.update { it.copy(waistInput = value, waistErrorRes = null) }
    }

    fun onHipsValueChange(value: String) {
        form.update { it.copy(hipsInput = value, hipsErrorRes = null) }
    }

    fun onShouldersValueChange(value: String) {
        form.update { it.copy(shouldersInput = value, shouldersErrorRes = null) }
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
                AddBodyMeasurementsEntry.Result.InvalidWaist ->
                    form.update { it.copy(waistErrorRes = R.string.error_invalid_waist) }
                AddBodyMeasurementsEntry.Result.InvalidHips ->
                    form.update { it.copy(hipsErrorRes = R.string.error_invalid_hips) }
                AddBodyMeasurementsEntry.Result.InvalidShoulders ->
                    form.update { it.copy(shouldersErrorRes = R.string.error_invalid_shoulders) }
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
