package de.bollich.fitnessboy.ui.overview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import de.bollich.fitnessboy.domain.WeightTrendPeriod
import de.bollich.fitnessboy.domain.repository.BodyMeasurementsRepository
import de.bollich.fitnessboy.domain.repository.ProfileRepository
import de.bollich.fitnessboy.domain.repository.WeightRepository
import de.bollich.fitnessboy.domain.usecase.GetHealthOverview
import de.bollich.fitnessboy.ui.appContainer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/** Dashboard: current weight, goal, BMI and latest body measurements at a glance. */
class OverviewViewModel(
    weightRepository: WeightRepository,
    profileRepository: ProfileRepository,
    bodyMeasurementsRepository: BodyMeasurementsRepository,
    private val getHealthOverview: GetHealthOverview,
) : ViewModel() {
    private val isBmiInfoVisible = MutableStateFlow(false)

    val uiState: StateFlow<OverviewUiState> =
        combine(
            weightRepository.observeEntries(),
            profileRepository.observeProfile(),
            bodyMeasurementsRepository.observeEntries(),
            isBmiInfoVisible,
        ) { weightEntries, profile, measurements, isBmiInfoVisible ->
            val overview = getHealthOverview(weightEntries, profile)
            val totalTrend = overview.trends.firstOrNull { it.period == WeightTrendPeriod.SINCE_START }
            val recentTrend = overview.trends
                .firstOrNull { it.period == WeightTrendPeriod.SEVEN_DAYS }
                ?.takeUnless { it.referenceDate == totalTrend?.referenceDate }

            OverviewUiState(
                isLoading = false,
                latestWeight = overview.latestEntry,
                recentTrend = recentTrend,
                totalTrend = totalTrend,
                hasTargetWeight = profile.targetWeightInKg != null,
                goalProgress = overview.goalProgress,
                heightInCm = profile.heightInCm,
                bmi = overview.bmi,
                bmiCategory = overview.bmiCategory,
                healthyWeightRange = overview.healthyWeightRange,
                optimalWeightInKg = overview.optimalWeightInKg,
                latestMeasurements = measurements.firstOrNull(),
                isBmiInfoVisible = isBmiInfoVisible,
            )
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = OverviewUiState(),
        )

    fun onBmiInfoVisibilityChange(isVisible: Boolean) {
        isBmiInfoVisible.value = isVisible
    }

    companion object {
        val Factory = viewModelFactory {
            initializer {
                val container = appContainer()
                OverviewViewModel(
                    weightRepository = container.weightRepository,
                    profileRepository = container.profileRepository,
                    bodyMeasurementsRepository = container.bodyMeasurementsRepository,
                    getHealthOverview = GetHealthOverview(),
                )
            }
        }
    }
}
