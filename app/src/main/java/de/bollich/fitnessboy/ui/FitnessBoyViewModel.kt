package de.bollich.fitnessboy.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import de.bollich.fitnessboy.data.AppSettingsStore
import de.bollich.fitnessboy.data.ProfileDataStore
import de.bollich.fitnessboy.data.RoomBodyMeasurementsRepository
import de.bollich.fitnessboy.data.RoomWeightRepository
import de.bollich.fitnessboy.data.WeightStore
import de.bollich.fitnessboy.data.local.FitnessBoyDatabase
import de.bollich.fitnessboy.domain.usecase.AddBodyMeasurementsEntry
import de.bollich.fitnessboy.domain.usecase.AddWeightEntry
import de.bollich.fitnessboy.domain.usecase.DeleteWeightEntry
import de.bollich.fitnessboy.domain.usecase.DeleteBodyMeasurementsEntry
import de.bollich.fitnessboy.domain.usecase.GetAppSettings
import de.bollich.fitnessboy.domain.usecase.GetFitnessBoySnapshot
import de.bollich.fitnessboy.domain.usecase.GetHealthOverview
import de.bollich.fitnessboy.domain.usecase.SaveAppSettings
import de.bollich.fitnessboy.domain.usecase.SaveProfile
import de.bollich.fitnessboy.format.formatNumber
import de.bollich.fitnessboy.model.AppSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate

class FitnessBoyViewModel(
    private val getFitnessBoySnapshot: GetFitnessBoySnapshot,
    private val getAppSettings: GetAppSettings,
    private val getHealthOverview: GetHealthOverview,
    private val saveProfile: SaveProfile,
    private val saveAppSettings: SaveAppSettings,
    private val addBodyMeasurementsEntry: AddBodyMeasurementsEntry,
    private val deleteBodyMeasurementsEntry: DeleteBodyMeasurementsEntry,
    private val addWeightEntry: AddWeightEntry,
    private val deleteWeightEntry: DeleteWeightEntry,
) : ViewModel() {
    private val _uiState = MutableStateFlow(loadInitialState())
    val uiState: StateFlow<FitnessBoyUiState> = _uiState.asStateFlow()

    fun onTabSelected(tab: AppTab) {
        updateState {
            copy(selectedTab = tab).persistSettings()
        }
    }

    fun onWeightPageChange(page: WeightPage) {
        updateState {
            copy(selectedWeightPage = page).persistSettings()
        }
    }

    fun onProfilePageChange(page: ProfilePage) {
        updateState { copy(selectedProfilePage = page) }
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

    fun onBodyMeasurementsDialogChange(isVisible: Boolean) {
        updateState {
            copy(
                isBodyMeasurementsDialogVisible = isVisible,
                selectedBodyMeasurementsDate = if (isVisible) selectedBodyMeasurementsDate else LocalDate.now(),
                waistErrorText = null,
                hipsErrorText = null,
                shouldersErrorText = null,
            )
        }
    }

    fun onBodyMeasurementsDateChange(date: LocalDate) {
        updateState { copy(selectedBodyMeasurementsDate = date) }
    }

    fun onWaistValueChange(value: String) {
        updateState { copy(waistInput = value, waistErrorText = null) }
    }

    fun onHipsValueChange(value: String) {
        updateState { copy(hipsInput = value, hipsErrorText = null) }
    }

    fun onShouldersValueChange(value: String) {
        updateState { copy(shouldersInput = value, shouldersErrorText = null) }
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

    fun onAddBodyMeasurementsClick() {
        val currentState = _uiState.value
        when (val result = addBodyMeasurementsEntry(
            date = currentState.selectedBodyMeasurementsDate,
            waistInput = currentState.waistInput,
            hipsInput = currentState.hipsInput,
            shouldersInput = currentState.shouldersInput,
        )) {
            is AddBodyMeasurementsEntry.Result.Success -> updateState {
                copy(
                    bodyMeasurementsEntries = result.entries,
                    isBodyMeasurementsDialogVisible = false,
                    selectedBodyMeasurementsDate = LocalDate.now(),
                    waistInput = "",
                    waistErrorText = null,
                    hipsInput = "",
                    hipsErrorText = null,
                    shouldersInput = "",
                    shouldersErrorText = null,
                )
            }

            is AddBodyMeasurementsEntry.Result.InvalidWaist -> updateState {
                copy(waistErrorText = result.message)
            }

            is AddBodyMeasurementsEntry.Result.InvalidHips -> updateState {
                copy(hipsErrorText = result.message)
            }

            is AddBodyMeasurementsEntry.Result.InvalidShoulders -> updateState {
                copy(shouldersErrorText = result.message)
            }
        }
    }

    fun onDeleteEntry(entry: de.bollich.fitnessboy.model.WeightEntry) {
        val updatedEntries = deleteWeightEntry(_uiState.value.entries, entry)
        updateState { copy(entries = updatedEntries) }
    }

    fun onDeleteBodyMeasurementsEntry(entry: de.bollich.fitnessboy.model.BodyMeasurementsEntry) {
        val updatedEntries = deleteBodyMeasurementsEntry(entry)
        updateState { copy(bodyMeasurementsEntries = updatedEntries) }
    }

    private fun loadInitialState(): FitnessBoyUiState {
        val snapshot = getFitnessBoySnapshot()
        val settings = getAppSettings()
        return deriveState(
            FitnessBoyUiState(
                selectedTab = settings.selectedTabName.toAppTab(),
                selectedWeightPage = settings.selectedWeightPageName.toWeightPage(),
                entries = snapshot.entries,
                bodyMeasurementsEntries = snapshot.bodyMeasurementsEntries,
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

    private fun FitnessBoyUiState.persistSettings(): FitnessBoyUiState {
        saveAppSettings(
            AppSettings(
                selectedTabName = selectedTab.name,
                selectedWeightPageName = selectedWeightPage.name,
            )
        )
        return this
    }

    private fun deriveState(state: FitnessBoyUiState): FitnessBoyUiState {
        val overview = getHealthOverview(
            entries = state.entries,
            profile = state.profile,
        )

        return state.copy(
            latestEntry = overview.latestEntry,
            latestBodyMeasurementsEntry = state.bodyMeasurementsEntries.firstOrNull(),
            primaryTrend = overview.primaryTrend,
            trends = overview.trends,
            goalProgress = overview.goalProgress,
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
            val bodyMeasurementsRepository = RoomBodyMeasurementsRepository(
                bodyMeasurementsDao = database.bodyMeasurementsDao(),
            )
            val profileRepository = ProfileDataStore.getInstance(appContext)
            val settingsRepository = AppSettingsStore.getInstance(appContext)
            return viewModelFactory {
                initializer {
                    FitnessBoyViewModel(
                        getFitnessBoySnapshot = GetFitnessBoySnapshot(
                            weightRepository = weightRepository,
                            profileRepository = profileRepository,
                            bodyMeasurementsRepository = bodyMeasurementsRepository,
                        ),
                        getAppSettings = GetAppSettings(settingsRepository),
                        getHealthOverview = GetHealthOverview(),
                        saveProfile = SaveProfile(profileRepository),
                        saveAppSettings = SaveAppSettings(settingsRepository),
                        addBodyMeasurementsEntry = AddBodyMeasurementsEntry(bodyMeasurementsRepository),
                        deleteBodyMeasurementsEntry = DeleteBodyMeasurementsEntry(bodyMeasurementsRepository),
                        addWeightEntry = AddWeightEntry(weightRepository),
                        deleteWeightEntry = DeleteWeightEntry(weightRepository),
                    )
                }
            }
        }
    }
}

private fun String.toAppTab(): AppTab =
    runCatching { AppTab.valueOf(this) }.getOrDefault(AppTab.WEIGHT)

private fun String.toWeightPage(): WeightPage =
    runCatching { WeightPage.valueOf(this) }.getOrDefault(WeightPage.DASHBOARD)
