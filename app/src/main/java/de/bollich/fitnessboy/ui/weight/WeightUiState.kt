package de.bollich.fitnessboy.ui.weight

import androidx.annotation.StringRes
import de.bollich.fitnessboy.domain.WeightTrend
import de.bollich.fitnessboy.model.UserProfile
import de.bollich.fitnessboy.model.WeightEntry
import java.time.LocalDate

data class WeightFormState(
    val weightInput: String = "",
    val selectedDate: LocalDate = LocalDate.now(),
    @StringRes val errorRes: Int? = null,
)

data class WeightUiState(
    val isLoading: Boolean = true,
    val entries: List<WeightEntry> = emptyList(),
    val profile: UserProfile = UserProfile(),
    val latestEntry: WeightEntry? = null,
    val trends: List<WeightTrend> = emptyList(),
    val bmi: Double? = null,
    val form: WeightFormState = WeightFormState(),
)
