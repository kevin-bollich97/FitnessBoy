package de.bollich.fitnessboy.ui.weight

import androidx.annotation.StringRes
import de.bollich.fitnessboy.domain.ChartGroup
import de.bollich.fitnessboy.domain.ChartPeriod
import de.bollich.fitnessboy.model.WeightEntry
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
    /** Newest first. */
    val items: List<WeightListItem> = emptyList(),
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
