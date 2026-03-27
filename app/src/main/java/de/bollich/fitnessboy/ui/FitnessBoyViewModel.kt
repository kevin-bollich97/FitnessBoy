package de.bollich.fitnessboy.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import de.bollich.fitnessboy.data.ProfileStore
import de.bollich.fitnessboy.data.WeightStore
import de.bollich.fitnessboy.domain.calculateBmi
import de.bollich.fitnessboy.domain.calculateHealthyWeightRange
import de.bollich.fitnessboy.domain.calculateOptimalWeight
import de.bollich.fitnessboy.domain.classifyBmi
import de.bollich.fitnessboy.domain.parseHeight
import de.bollich.fitnessboy.domain.parseOptionalWeight
import de.bollich.fitnessboy.domain.parseWeight
import de.bollich.fitnessboy.format.formatNumber
import de.bollich.fitnessboy.model.WeightEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate

class FitnessBoyViewModel(
    private val weightStore: WeightStore,
    private val profileStore: ProfileStore,
) : ViewModel() {
    private val _uiState = MutableStateFlow(loadInitialState())
    val uiState: StateFlow<FitnessBoyUiState> = _uiState.asStateFlow()

    fun onTabSelected(tab: AppTab) {
        updateState { copy(selectedTab = tab) }
    }

    fun onWeightPageChange(page: WeightPage) {
        updateState { copy(selectedWeightPage = page) }
    }

    fun onWeightValueChange(value: String) {
        updateState { copy(weightInput = value, weightErrorText = null) }
    }

    fun onSelectedWeightDateChange(date: LocalDate) {
        updateState { copy(selectedWeightDate = date) }
    }

    fun onHeightValueChange(value: String) {
        updateState { copy(heightInput = value, heightErrorText = null) }
    }

    fun onTargetWeightValueChange(value: String) {
        updateState { copy(targetWeightInput = value, targetWeightErrorText = null) }
    }

    fun onSaveProfileClick() {
        val currentState = _uiState.value
        val parsedHeight = parseHeight(currentState.heightInput)
        if (parsedHeight == null) {
            updateState {
                copy(
                    heightErrorText = "Bitte eine gültige Größe in cm eingeben.",
                    targetWeightErrorText = null,
                )
            }
            return
        }

        val parsedTargetWeight = parseOptionalWeight(currentState.targetWeightInput)
        if (currentState.targetWeightInput.isNotBlank() && parsedTargetWeight == null) {
            updateState {
                copy(targetWeightErrorText = "Bitte ein gültiges Zielgewicht eingeben.")
            }
            return
        }

        val updatedProfile = currentState.profile.copy(
            heightInCm = parsedHeight,
            targetWeightInKg = parsedTargetWeight,
        )
        profileStore.save(updatedProfile)

        updateState {
            copy(
                profile = updatedProfile,
                heightInput = formatNumber(parsedHeight),
                heightErrorText = null,
                targetWeightInput = parsedTargetWeight?.let(::formatNumber).orEmpty(),
                targetWeightErrorText = null,
            )
        }
    }

    fun onAddWeightClick() {
        val currentState = _uiState.value
        val parsedWeight = parseWeight(currentState.weightInput)
        if (parsedWeight == null) {
            updateState { copy(weightErrorText = "Bitte ein gültiges Gewicht eingeben.") }
            return
        }

        val updatedEntries = (
            currentState.entries + WeightEntry(
                date = currentState.selectedWeightDate,
                weightInKg = parsedWeight,
            )
        ).sortedByDescending(WeightEntry::date)

        weightStore.save(updatedEntries)

        updateState {
            copy(
                entries = updatedEntries,
                weightInput = "",
                weightErrorText = null,
                selectedWeightDate = LocalDate.now(),
            )
        }
    }

    fun onDeleteEntry(entry: WeightEntry) {
        val updatedEntries = _uiState.value.entries - entry
        weightStore.save(updatedEntries)
        updateState { copy(entries = updatedEntries) }
    }

    private fun loadInitialState(): FitnessBoyUiState {
        val profile = profileStore.load()
        return deriveState(
            FitnessBoyUiState(
                entries = weightStore.load(),
                profile = profile,
                heightInput = profile.heightInCm?.let(::formatNumber).orEmpty(),
                targetWeightInput = profile.targetWeightInKg?.let(::formatNumber).orEmpty(),
            )
        )
    }

    private fun updateState(transform: FitnessBoyUiState.() -> FitnessBoyUiState) {
        _uiState.update { currentState ->
            deriveState(currentState.transform())
        }
    }

    private fun deriveState(state: FitnessBoyUiState): FitnessBoyUiState {
        val latestEntry = state.entries.firstOrNull()
        val previousEntry = state.entries.getOrNull(1)
        val trend = latestEntry?.let { latest ->
            previousEntry?.let { previous -> latest.weightInKg - previous.weightInKg }
        }
        val bmi = calculateBmi(latestEntry?.weightInKg, state.profile.heightInCm)
        val healthyWeightRange = calculateHealthyWeightRange(state.profile.heightInCm)
        val optimalWeight = calculateOptimalWeight(state.profile.heightInCm)

        return state.copy(
            latestEntry = latestEntry,
            trend = trend,
            bmi = bmi,
            bmiCategory = classifyBmi(bmi),
            healthyWeightRangeText = healthyWeightRange?.let {
                "${formatNumber(it.minimumInKg)} bis ${formatNumber(it.maximumInKg)} kg"
            },
            optimalWeightText = optimalWeight?.let { "${formatNumber(it)} kg" },
        )
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory {
            val appContext = context.applicationContext
            return viewModelFactory {
                initializer {
                    FitnessBoyViewModel(
                        weightStore = WeightStore(appContext),
                        profileStore = ProfileStore(appContext),
                    )
                }
            }
        }
    }
}
