package de.bollich.fitnessboy.ui.navigation

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

/** Keys of the Navigation 3 back stack. */
sealed interface Destination {
    data class Tab(val tab: AppTab) : Destination
    data object WeightHistory : Destination
    data object MeasurementsHistory : Destination
}
