package de.bollich.fitnessboy.ui.weight

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import de.bollich.fitnessboy.R
import de.bollich.fitnessboy.domain.repository.ProfileRepository
import de.bollich.fitnessboy.domain.repository.WeightRepository
import de.bollich.fitnessboy.domain.usecase.AddWeightEntry
import de.bollich.fitnessboy.domain.usecase.GetHealthOverview
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

/** Weight entries, trends and BMI insights. Shared by the weight, BMI and history screens. */
class WeightViewModel(
    private val weightRepository: WeightRepository,
    profileRepository: ProfileRepository,
    private val addWeightEntry: AddWeightEntry,
    private val getHealthOverview: GetHealthOverview,
) : ViewModel() {
    private val form = MutableStateFlow(WeightFormState())

    val uiState: StateFlow<WeightUiState> =
        combine(
            weightRepository.observeEntries(),
            profileRepository.observeProfile(),
            form,
        ) { entries, profile, form ->
            val overview = getHealthOverview(entries, profile)
            WeightUiState(
                isLoading = false,
                entries = entries,
                profile = profile,
                latestEntry = overview.latestEntry,
                trends = overview.trends,
                goalProgress = overview.goalProgress,
                bmi = overview.bmi,
                bmiCategory = overview.bmiCategory,
                healthyWeightRange = overview.healthyWeightRange,
                optimalWeightInKg = overview.optimalWeightInKg,
                form = form,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = WeightUiState(),
        )

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
                AddWeightEntry.Result.Success -> form.value = WeightFormState()
                AddWeightEntry.Result.InvalidWeight -> form.update { it.copy(errorRes = R.string.error_invalid_weight) }
            }
        }
    }

    fun onDeleteEntry(entry: WeightEntry) {
        viewModelScope.launch { weightRepository.delete(entry) }
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val container = appContainer()
                WeightViewModel(
                    weightRepository = container.weightRepository,
                    profileRepository = container.profileRepository,
                    addWeightEntry = AddWeightEntry(container.weightRepository),
                    getHealthOverview = GetHealthOverview(),
                )
            }
        }
    }
}
