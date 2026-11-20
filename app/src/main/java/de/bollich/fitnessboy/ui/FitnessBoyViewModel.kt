package de.bollich.fitnessboy.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
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
import de.bollich.fitnessboy.domain.usecase.GetHealthOverview
import de.bollich.fitnessboy.domain.usecase.ObserveFitnessBoySnapshot
import de.bollich.fitnessboy.domain.usecase.SaveAppSettings
import de.bollich.fitnessboy.domain.usecase.SaveProfile
import de.bollich.fitnessboy.format.formatNumber
import de.bollich.fitnessboy.model.AppSettings
import de.bollich.fitnessboy.model.BodyMeasurementsEntry
import de.bollich.fitnessboy.model.WeightEntry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

class FitnessBoyViewModel(
    private val observeFitnessBoySnapshot: ObserveFitnessBoySnapshot,
    private val getAppSettings: GetAppSettings,
    private val getHealthOverview: GetHealthOverview,
    private val saveProfile: SaveProfile,
    private val saveAppSettings: SaveAppSettings,
    private val addBodyMeasurementsEntry: AddBodyMeasurementsEntry,
    private val deleteBodyMeasurementsEntry: DeleteBodyMeasurementsEntry,
    private val addWeightEntry: AddWeightEntry,
    private val deleteWeightEntry: DeleteWeightEntry,
) : ViewModel() {
    private val _uiState = MutableStateFlow(FitnessBoyUiState())
    val uiState: StateFlow<FitnessBoyUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val settings = getAppSettings()
            updateState {
                copy(
                    selectedTab = settings.selectedTabName.toAppTab(),
                    selectedWeightPage = settings.selectedWeightPageName.toWeightPage(),
                )
            }

            observeFitnessBoySnapshot().collect { snapshot ->
                updateState {
                    copy(
                        isLoading = false,
                        entries = snapshot.entries,
                        bodyMeasurementsEntries = snapshot.bodyMeasurementsEntries,
                        profile = snapshot.profile,
                        // Prefill the profile form once; afterwards it belongs to the user.
                        heightInput = if (isLoading) {
                            snapshot.profile.heightInCm?.let(::formatNumber).orEmpty()
                        } else {
                            heightInput
                        },
                        targetWeightInput = if (isLoading) {
                            snapshot.profile.targetWeightInKg?.let(::formatNumber).orEmpty()
                        } else {
                            targetWeightInput
                        },
                    )
                }
            }
        }
    }

    fun onTabSelected(tab: AppTab) {
        updateState { copy(selectedTab = tab) }
        persistSettings()
    }

    fun onWeightPageChange(page: WeightPage) {
        updateState { copy(selectedWeightPage = page) }
        persistSettings()
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
        viewModelScope.launch {
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
    }

    fun onAddWeightClick() {
        viewModelScope.launch {
            val currentState = _uiState.value
            when (val result = addWeightEntry(
                weightInput = currentState.weightInput,
                selectedDate = currentState.selectedWeightDate,
            )) {
                AddWeightEntry.Result.Success -> updateState {
                    copy(
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
    }

    fun onAddBodyMeasurementsClick() {
        viewModelScope.launch {
            val currentState = _uiState.value
            when (val result = addBodyMeasurementsEntry(
                date = currentState.selectedBodyMeasurementsDate,
                waistInput = currentState.waistInput,
                hipsInput = currentState.hipsInput,
                shouldersInput = currentState.shouldersInput,
            )) {
                AddBodyMeasurementsEntry.Result.Success -> updateState {
                    copy(
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
    }

    fun onDeleteEntry(entry: WeightEntry) {
        viewModelScope.launch { deleteWeightEntry(entry) }
    }

    fun onDeleteBodyMeasurementsEntry(entry: BodyMeasurementsEntry) {
        viewModelScope.launch { deleteBodyMeasurementsEntry(entry) }
    }

    private fun updateState(transform: FitnessBoyUiState.() -> FitnessBoyUiState) {
        _uiState.update { currentState ->
            deriveState(currentState.transform())
        }
    }

    private fun persistSettings() {
        val state = _uiState.value
        val settings = AppSettings(
            selectedTabName = state.selectedTab.name,
            selectedWeightPageName = state.selectedWeightPage.name,
        )
        viewModelScope.launch { saveAppSettings(settings) }
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
                        observeFitnessBoySnapshot = ObserveFitnessBoySnapshot(
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
