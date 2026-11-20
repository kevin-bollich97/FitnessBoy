package de.bollich.fitnessboy.ui.measurements

import de.bollich.fitnessboy.model.BodyMeasurementsEntry
import java.time.LocalDate

data class BodyMeasurementsFormState(
    val isDialogVisible: Boolean = false,
    val selectedDate: LocalDate = LocalDate.now(),
    val waistInput: String = "",
    val waistErrorText: String? = null,
    val hipsInput: String = "",
    val hipsErrorText: String? = null,
    val shouldersInput: String = "",
    val shouldersErrorText: String? = null,
)

data class BodyMeasurementsUiState(
    val isLoading: Boolean = true,
    val entries: List<BodyMeasurementsEntry> = emptyList(),
    val form: BodyMeasurementsFormState = BodyMeasurementsFormState(),
) {
    val latestEntry: BodyMeasurementsEntry?
        get() = entries.firstOrNull()
}
