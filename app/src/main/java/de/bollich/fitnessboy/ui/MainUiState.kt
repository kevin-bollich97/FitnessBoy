package de.bollich.fitnessboy.ui

import de.bollich.fitnessboy.R

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

data class MainUiState(
    val isLoading: Boolean = true,
    val selectedTab: AppTab = AppTab.WEIGHT,
    val selectedWeightPage: WeightPage = WeightPage.DASHBOARD,
    val selectedProfilePage: ProfilePage = ProfilePage.OVERVIEW,
)
