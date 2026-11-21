package de.bollich.fitnessboy.ui.overview

import de.bollich.fitnessboy.domain.BmiCategory
import de.bollich.fitnessboy.domain.GoalProgress
import de.bollich.fitnessboy.domain.WeightRange
import de.bollich.fitnessboy.domain.WeightTrend
import de.bollich.fitnessboy.model.BodyMeasurementsEntry
import de.bollich.fitnessboy.model.WeightEntry

data class OverviewUiState(
    val isLoading: Boolean = true,
    val latestWeight: WeightEntry? = null,
    /** Change over roughly the last week; `null` if it would just repeat [totalTrend]. */
    val recentTrend: WeightTrend? = null,
    /** Change since the first entry. */
    val totalTrend: WeightTrend? = null,
    val hasTargetWeight: Boolean = false,
    val goalProgress: GoalProgress? = null,
    val heightInCm: Double? = null,
    val bmi: Double? = null,
    val bmiCategory: BmiCategory? = null,
    val healthyWeightRange: WeightRange? = null,
    val optimalWeightInKg: Double? = null,
    val latestMeasurements: BodyMeasurementsEntry? = null,
    val isBmiInfoVisible: Boolean = false,
)
