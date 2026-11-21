package de.bollich.fitnessboy.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import de.bollich.fitnessboy.R

enum class AppTab(
    @StringRes val labelRes: Int,
    @DrawableRes val iconRes: Int,
) {
    OVERVIEW(R.string.tab_overview, R.drawable.ic_home),
    WEIGHT(R.string.tab_weight, R.drawable.ic_show_chart),
    MEASUREMENTS(R.string.tab_measurements, R.drawable.ic_straighten),
    PROFILE(R.string.tab_profile, R.drawable.ic_account_box),
}

/** Keys of the Navigation 3 back stack. */
sealed interface Destination {
    data class Tab(val tab: AppTab) : Destination
    data object WeightHistory : Destination
    data object MeasurementsHistory : Destination
}
