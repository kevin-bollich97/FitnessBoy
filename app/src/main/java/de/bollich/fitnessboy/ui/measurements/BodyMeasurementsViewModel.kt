package de.bollich.fitnessboy.ui.measurements

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import de.bollich.fitnessboy.domain.BodyMeasurement
import de.bollich.fitnessboy.domain.calculateBmiOn
import de.bollich.fitnessboy.domain.repository.BodyMeasurementsRepository
import de.bollich.fitnessboy.domain.repository.ProfileRepository
import de.bollich.fitnessboy.domain.repository.WeightRepository
import de.bollich.fitnessboy.domain.usecase.AddBodyMeasurementsEntry
import de.bollich.fitnessboy.domain.valueOf
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

/** Measurement figure, chart, entry list and the add-measurements sheet. */
class BodyMeasurementsViewModel(
    private val repository: BodyMeasurementsRepository,
    weightRepository: WeightRepository,
    profileRepository: ProfileRepository,
    private val addBodyMeasurementsEntry: AddBodyMeasurementsEntry,
) : ViewModel() {
    private val form = MutableStateFlow(BodyMeasurementsFormState())
    private val selection = MutableStateFlow(Selection())

    val uiState: StateFlow<BodyMeasurementsUiState> =
        combine(
            repository.observeEntries(),
            weightRepository.observeEntries(),
            profileRepository.observeProfile(),
            form,
            selection,
        ) { entries, weights, profile, form, selection ->
            val items = entries.map { MeasurementListItem(it, calculateBmiOn(it.date, weights, profile.heightInCm)) }
            val itemsWithValue = items.filter { it.entry.valueOf(selection.chartMeasurement) != null }
            BodyMeasurementsUiState(
                isLoading = false,
                items = items,
                chartMeasurement = selection.chartMeasurement,
                selectedItem = itemsWithValue.firstOrNull { it.entry.id == selection.selectedEntryId }
                    ?: itemsWithValue.firstOrNull(),
                entryPendingDeletion = selection.entryPendingDeletion,
                form = form,
            )
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
                AddBodyMeasurementsEntry.Result.Success -> {
                    form.value = BodyMeasurementsFormState()
                    // Show the newest entry, which is usually the one just added.
                    selection.update { it.copy(selectedEntryId = null) }
                }
                is AddBodyMeasurementsEntry.Result.Invalid ->
                    form.update { it.copy(invalidMeasurements = result.measurements) }
            }
        }
    }

    fun onChartMeasurementChange(measurement: BodyMeasurement) {
        selection.update { it.copy(chartMeasurement = measurement) }
    }

    fun onChartEntrySelected(entry: BodyMeasurementsEntry) {
        selection.update { it.copy(selectedEntryId = entry.id) }
    }

    fun onDeleteRequest(entry: BodyMeasurementsEntry?) {
        selection.update { it.copy(entryPendingDeletion = entry) }
    }

    fun onDeleteConfirm() {
        val entry = selection.value.entryPendingDeletion ?: return
        selection.update { it.copy(entryPendingDeletion = null) }
        viewModelScope.launch { repository.delete(entry) }
    }

    private data class Selection(
        val chartMeasurement: BodyMeasurement = BodyMeasurement.WAIST,
        val selectedEntryId: Long? = null,
        val entryPendingDeletion: BodyMeasurementsEntry? = null,
    )

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val container = appContainer()
                BodyMeasurementsViewModel(
                    repository = container.bodyMeasurementsRepository,
                    weightRepository = container.weightRepository,
                    profileRepository = container.profileRepository,
                    addBodyMeasurementsEntry = AddBodyMeasurementsEntry(container.bodyMeasurementsRepository),
                )
            }
        }
    }
}
