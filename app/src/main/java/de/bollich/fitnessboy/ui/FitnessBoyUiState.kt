package de.bollich.fitnessboy.ui

import de.bollich.fitnessboy.R
import de.bollich.fitnessboy.domain.GoalProgress
import de.bollich.fitnessboy.domain.WeightTrend
import de.bollich.fitnessboy.model.BodyMeasurementsEntry
import de.bollich.fitnessboy.model.UserProfile
import de.bollich.fitnessboy.model.WeightEntry
import java.time.LocalDate

enum class AppTab(
    val label: String,
    val iconRes: Int,
) {
    WEIGHT("Gewicht", R.drawable.ic_home),
    BMI("BMI", R.drawable.ic_favorite),
    MEASUREMENTS("Maße", R.drawable.ic_straighten),
    PROFILE("Profil", R.drawable.ic_account_box),
}

enum class WeightPage {
    DASHBOARD,
    HISTORY,
}

enum class ProfilePage {
    OVERVIEW,
    WEIGHT_HISTORY,
    MEASUREMENTS_HISTORY,
}

data class FitnessBoyUiState(
    val selectedTab: AppTab = AppTab.WEIGHT,
    val selectedWeightPage: WeightPage = WeightPage.DASHBOARD,
    val selectedProfilePage: ProfilePage = ProfilePage.OVERVIEW,
    val entries: List<WeightEntry> = emptyList(),
    val bodyMeasurementsEntries: List<BodyMeasurementsEntry> = emptyList(),
    val profile: UserProfile = UserProfile(),
    val latestEntry: WeightEntry? = null,
    val latestBodyMeasurementsEntry: BodyMeasurementsEntry? = null,
    val primaryTrend: WeightTrend? = null,
    val trends: List<WeightTrend> = emptyList(),
    val goalProgress: GoalProgress? = null,
    val bmi: Double? = null,
    val bmiCategory: String? = null,
    val healthyWeightRangeText: String? = null,
    val optimalWeightText: String? = null,
    val weightInput: String = "",
    val selectedWeightDate: LocalDate = LocalDate.now(),
    val weightErrorText: String? = null,
    val isBodyMeasurementsDialogVisible: Boolean = false,
    val selectedBodyMeasurementsDate: LocalDate = LocalDate.now(),
    val waistInput: String = "",
    val waistErrorText: String? = null,
    val hipsInput: String = "",
    val hipsErrorText: String? = null,
    val shouldersInput: String = "",
    val shouldersErrorText: String? = null,
    val heightInput: String = "",
    val heightErrorText: String? = null,
    val targetWeightInput: String = "",
    val targetWeightErrorText: String? = null,
)
