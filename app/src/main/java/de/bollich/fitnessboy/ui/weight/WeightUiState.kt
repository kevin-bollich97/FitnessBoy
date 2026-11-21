package de.bollich.fitnessboy.ui.weight

import androidx.annotation.StringRes
import de.bollich.fitnessboy.domain.ChartGroup
import de.bollich.fitnessboy.domain.ChartPeriod
import de.bollich.fitnessboy.model.WeightEntry
import de.bollich.fitnessboy.ui.components.MonthSection
import java.time.LocalDate

data class WeightFormState(
    val isSheetVisible: Boolean = false,
    val weightInput: String = "",
    val selectedDate: LocalDate = LocalDate.now(),
    @StringRes val errorRes: Int? = null,
)

/** A weight entry with the BMI it corresponds to (based on the current height). */
data class WeightListItem(
    val entry: WeightEntry,
    val bmi: Double?,
)

data class WeightUiState(
    val isLoading: Boolean = true,
    /** Whether there is any weight entry at all, regardless of the period. */
    val hasEntries: Boolean = false,
    /** Entries within [chartPeriod], newest first. */
    val items: List<WeightListItem> = emptyList(),
    /** [items] by month; only the newest few unless [showAllEntries]. */
    val entrySections: List<MonthSection<WeightListItem>> = emptyList(),
    val targetWeightInKg: Double? = null,
    val chartPeriod: ChartPeriod = ChartPeriod.ALL,
    /** Chart points for [chartPeriod], oldest first. */
    val chartGroups: List<ChartGroup> = emptyList(),
    /** Point highlighted in the chart; the newest one unless the user tapped another. */
    val selectedGroup: ChartGroup? = null,
    /** BMI of [selectedGroup] if it stands for exactly one entry. */
    val selectedGroupBmi: Double? = null,
    val showAllEntries: Boolean = false,
    val entryPendingDeletion: WeightEntry? = null,
    val form: WeightFormState = WeightFormState(),
)
