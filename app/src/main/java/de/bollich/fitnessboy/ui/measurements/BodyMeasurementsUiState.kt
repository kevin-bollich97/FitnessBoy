package de.bollich.fitnessboy.ui.measurements

import androidx.annotation.StringRes
import de.bollich.fitnessboy.model.BodyMeasurementsEntry
import java.time.LocalDate

data class BodyMeasurementsFormState(
    val isDialogVisible: Boolean = false,
    val selectedDate: LocalDate = LocalDate.now(),
    val waistInput: String = "",
    @StringRes val waistErrorRes: Int? = null,
    val hipsInput: String = "",
    @StringRes val hipsErrorRes: Int? = null,
    val shouldersInput: String = "",
    @StringRes val shouldersErrorRes: Int? = null,
)

data class BodyMeasurementsUiState(
    val isLoading: Boolean = true,
    val entries: List<BodyMeasurementsEntry> = emptyList(),
    val form: BodyMeasurementsFormState = BodyMeasurementsFormState(),
) {
    val latestEntry: BodyMeasurementsEntry?
        get() = entries.firstOrNull()
}
