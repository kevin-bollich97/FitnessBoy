package de.bollich.fitnessboy.ui.weight

import de.bollich.fitnessboy.domain.GoalProgress
import de.bollich.fitnessboy.domain.WeightTrend
import de.bollich.fitnessboy.model.UserProfile
import de.bollich.fitnessboy.model.WeightEntry
import java.time.LocalDate

data class WeightFormState(
    val weightInput: String = "",
    val selectedDate: LocalDate = LocalDate.now(),
    val errorText: String? = null,
)

data class WeightUiState(
    val isLoading: Boolean = true,
    val entries: List<WeightEntry> = emptyList(),
    val profile: UserProfile = UserProfile(),
    val latestEntry: WeightEntry? = null,
    val primaryTrend: WeightTrend? = null,
    val trends: List<WeightTrend> = emptyList(),
    val goalProgress: GoalProgress? = null,
    val bmi: Double? = null,
    val bmiCategory: String? = null,
    val healthyWeightRangeText: String? = null,
    val optimalWeightText: String? = null,
    val form: WeightFormState = WeightFormState(),
)
