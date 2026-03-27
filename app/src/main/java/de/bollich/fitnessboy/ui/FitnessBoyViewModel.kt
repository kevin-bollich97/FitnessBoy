package de.bollich.fitnessboy.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import de.bollich.fitnessboy.data.RoomWeightRepository
import de.bollich.fitnessboy.data.ProfileStore
import de.bollich.fitnessboy.data.WeightStore
import de.bollich.fitnessboy.data.local.FitnessBoyDatabase
import de.bollich.fitnessboy.domain.usecase.AddWeightEntry
import de.bollich.fitnessboy.domain.usecase.DeleteWeightEntry
import de.bollich.fitnessboy.domain.usecase.GetFitnessBoySnapshot
import de.bollich.fitnessboy.domain.usecase.GetHealthOverview
import de.bollich.fitnessboy.domain.usecase.SaveProfile
import de.bollich.fitnessboy.format.formatNumber
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate

class FitnessBoyViewModel(
    private val getFitnessBoySnapshot: GetFitnessBoySnapshot,
    private val getHealthOverview: GetHealthOverview,
    private val saveProfile: SaveProfile,
    private val addWeightEntry: AddWeightEntry,
    private val deleteWeightEntry: DeleteWeightEntry,
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
        when (val result = saveProfile(
            profile = currentState.profile,
            heightInput = currentState.heightInput,
            targetWeightInput = currentState.targetWeightInput,
        )) {
            is SaveProfile.Result.Success -> updateState {
                copy(
                    profile = result.profile,
                    heightInput = result.formattedHeight,
                    heightErrorText = null,
                    targetWeightInput = result.formattedTargetWeight,
                    targetWeightErrorText = null,
                )
            }

            is SaveProfile.Result.InvalidHeight -> updateState {
                copy(
                    heightErrorText = result.message,
                    targetWeightErrorText = null,
                )
            }

            is SaveProfile.Result.InvalidTargetWeight -> updateState {
                copy(targetWeightErrorText = result.message)
            }
        }
    }

    fun onAddWeightClick() {
        val currentState = _uiState.value
        when (val result = addWeightEntry(
            entries = currentState.entries,
            weightInput = currentState.weightInput,
            selectedDate = currentState.selectedWeightDate,
        )) {
            is AddWeightEntry.Result.Success -> updateState {
                copy(
                    entries = result.entries,
                    weightInput = "",
                    weightErrorText = null,
                    selectedWeightDate = LocalDate.now(),
                )
            }

            is AddWeightEntry.Result.InvalidWeight -> updateState {
                copy(weightErrorText = result.message)
            }
        }
    }

    fun onDeleteEntry(entry: de.bollich.fitnessboy.model.WeightEntry) {
        val updatedEntries = deleteWeightEntry(_uiState.value.entries, entry)
        updateState { copy(entries = updatedEntries) }
    }

    private fun loadInitialState(): FitnessBoyUiState {
        val snapshot = getFitnessBoySnapshot()
        return deriveState(
            FitnessBoyUiState(
                entries = snapshot.entries,
                profile = snapshot.profile,
                heightInput = snapshot.profile.heightInCm?.let(::formatNumber).orEmpty(),
                targetWeightInput = snapshot.profile.targetWeightInKg?.let(::formatNumber).orEmpty(),
            )
        )
    }

    private fun updateState(transform: FitnessBoyUiState.() -> FitnessBoyUiState) {
        _uiState.update { currentState ->
            deriveState(currentState.transform())
        }
    }

    private fun deriveState(state: FitnessBoyUiState): FitnessBoyUiState {
        val overview = getHealthOverview(
            entries = state.entries,
            profile = state.profile,
        )

        return state.copy(
            latestEntry = overview.latestEntry,
            trend = overview.trend,
            bmi = overview.bmi,
            bmiCategory = overview.bmiCategory,
            healthyWeightRangeText = overview.healthyWeightRangeText,
            optimalWeightText = overview.optimalWeightText,
        )
    }

    companion object {
        fun factory(context: Context): ViewModelProvider.Factory {
            val appContext = context.applicationContext
            val database = FitnessBoyDatabase.getInstance(appContext)
            val weightRepository = RoomWeightRepository(
                weightEntryDao = database.weightEntryDao(),
                legacyWeightStore = WeightStore(appContext),
            )
            val profileRepository = ProfileStore(appContext)
            return viewModelFactory {
                initializer {
                    FitnessBoyViewModel(
                        getFitnessBoySnapshot = GetFitnessBoySnapshot(
                            weightRepository = weightRepository,
                            profileRepository = profileRepository,
                        ),
                        getHealthOverview = GetHealthOverview(),
                        saveProfile = SaveProfile(profileRepository),
                        addWeightEntry = AddWeightEntry(weightRepository),
                        deleteWeightEntry = DeleteWeightEntry(weightRepository),
                    )
                }
            }
        }
    }
}
