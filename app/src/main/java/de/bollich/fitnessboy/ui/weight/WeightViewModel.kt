package de.bollich.fitnessboy.ui.weight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import de.bollich.fitnessboy.R
import de.bollich.fitnessboy.domain.calculateBmi
import de.bollich.fitnessboy.domain.repository.ProfileRepository
import de.bollich.fitnessboy.domain.repository.WeightRepository
import de.bollich.fitnessboy.domain.usecase.AddWeightEntry
import de.bollich.fitnessboy.model.WeightEntry
import de.bollich.fitnessboy.ui.appContainer
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/** Weight chart, entry list and the add-weight sheet. */
class WeightViewModel(
    private val weightRepository: WeightRepository,
    profileRepository: ProfileRepository,
    private val addWeightEntry: AddWeightEntry,
) : ViewModel() {
    private val form = MutableStateFlow(WeightFormState())
    private val selection = MutableStateFlow(Selection())

    val uiState: StateFlow<WeightUiState> =
        combine(
            weightRepository.observeEntries(),
            profileRepository.observeProfile(),
            form,
            selection,
        ) { entries, profile, form, selection ->
            val items = entries.map { WeightListItem(it, calculateBmi(it.weightInKg, profile.heightInCm)) }
            WeightUiState(
                isLoading = false,
                items = items,
                targetWeightInKg = profile.targetWeightInKg,
                selectedItem = items.firstOrNull { it.entry.id == selection.selectedEntryId } ?: items.firstOrNull(),
                entryPendingDeletion = selection.entryPendingDeletion,
                form = form,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = WeightUiState(),
        )

    fun onAddWeightSheetVisibilityChange(isVisible: Boolean) {
        form.update {
            if (isVisible) {
                it.copy(isSheetVisible = true)
            } else {
                // Keep the typed value, but start fresh on date and errors next time.
                it.copy(isSheetVisible = false, selectedDate = LocalDate.now(), errorRes = null)
            }
        }
    }

    fun onWeightValueChange(value: String) {
        form.update { it.copy(weightInput = value, errorRes = null) }
    }

    fun onSelectedDateChange(date: LocalDate) {
        form.update { it.copy(selectedDate = date) }
    }

    fun onAddWeightClick() {
        val current = form.value
        viewModelScope.launch {
            when (addWeightEntry(current.weightInput, current.selectedDate)) {
                AddWeightEntry.Result.Success -> {
                    form.value = WeightFormState()
                    // Show the newest entry, which is usually the one just added.
                    selection.update { it.copy(selectedEntryId = null) }
                }
                AddWeightEntry.Result.InvalidWeight -> form.update { it.copy(errorRes = R.string.error_invalid_weight) }
            }
        }
    }

    fun onChartEntrySelected(entry: WeightEntry) {
        selection.update { it.copy(selectedEntryId = entry.id) }
    }

    fun onDeleteRequest(entry: WeightEntry?) {
        selection.update { it.copy(entryPendingDeletion = entry) }
    }

    fun onDeleteConfirm() {
        val entry = selection.value.entryPendingDeletion ?: return
        selection.update { it.copy(entryPendingDeletion = null) }
        viewModelScope.launch { weightRepository.delete(entry) }
    }

    private data class Selection(
        val selectedEntryId: Long? = null,
        val entryPendingDeletion: WeightEntry? = null,
    )

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val container = appContainer()
                WeightViewModel(
                    weightRepository = container.weightRepository,
                    profileRepository = container.profileRepository,
                    addWeightEntry = AddWeightEntry(container.weightRepository),
                )
            }
        }
    }
}
