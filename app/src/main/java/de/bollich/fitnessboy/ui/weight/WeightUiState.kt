package de.bollich.fitnessboy.ui.weight

import androidx.annotation.StringRes
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
    /** Item highlighted in the chart; the newest one unless the user tapped another. */
    val selectedItem: WeightListItem? = null,
    val entryPendingDeletion: WeightEntry? = null,
    val form: WeightFormState = WeightFormState(),
)
