package de.bollich.fitnessboy.ui.measurements

import de.bollich.fitnessboy.domain.BodyMeasurement
import de.bollich.fitnessboy.model.BodyMeasurementsEntry
import java.time.LocalDate

data class BodyMeasurementsFormState(
    val isSheetVisible: Boolean = false,
    val selectedDate: LocalDate = LocalDate.now(),
    val inputs: Map<BodyMeasurement, String> = emptyMap(),
    val invalidMeasurements: Set<BodyMeasurement> = emptySet(),
)

data class BodyMeasurementsUiState(
    val isLoading: Boolean = true,
    val entries: List<BodyMeasurementsEntry> = emptyList(),
    val form: BodyMeasurementsFormState = BodyMeasurementsFormState(),
) {
    val latestEntry: BodyMeasurementsEntry?
        get() = entries.firstOrNull()
}
