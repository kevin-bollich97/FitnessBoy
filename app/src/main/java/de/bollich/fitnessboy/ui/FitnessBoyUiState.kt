package de.bollich.fitnessboy.ui

import de.bollich.fitnessboy.R
import de.bollich.fitnessboy.model.UserProfile
import de.bollich.fitnessboy.model.WeightEntry
import java.time.LocalDate

enum class AppTab(
    val label: String,
    val iconRes: Int,
) {
    WEIGHT("Gewicht", R.drawable.ic_home),
    BMI("BMI", R.drawable.ic_favorite),
    PROFILE("Profil", R.drawable.ic_account_box),
}

enum class WeightPage {
    DASHBOARD,
    HISTORY,
}

data class FitnessBoyUiState(
    val selectedTab: AppTab = AppTab.WEIGHT,
    val selectedWeightPage: WeightPage = WeightPage.DASHBOARD,
    val entries: List<WeightEntry> = emptyList(),
    val profile: UserProfile = UserProfile(),
    val latestEntry: WeightEntry? = null,
    val trend: Double? = null,
    val bmi: Double? = null,
    val bmiCategory: String? = null,
    val healthyWeightRangeText: String? = null,
    val optimalWeightText: String? = null,
    val weightInput: String = "",
    val selectedWeightDate: LocalDate = LocalDate.now(),
    val weightErrorText: String? = null,
    val heightInput: String = "",
    val heightErrorText: String? = null,
    val targetWeightInput: String = "",
    val targetWeightErrorText: String? = null,
)
