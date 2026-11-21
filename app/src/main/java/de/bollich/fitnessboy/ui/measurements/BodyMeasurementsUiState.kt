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

/** A measurement entry with the BMI on its date (latest weight up to then, current height). */
data class MeasurementListItem(
    val entry: BodyMeasurementsEntry,
    val bmi: Double?,
)

data class BodyMeasurementsUiState(
    val isLoading: Boolean = true,
    /** Newest first. */
    val items: List<MeasurementListItem> = emptyList(),
    /** Measurement shown in the chart and highlighted on the figure. */
    val chartMeasurement: BodyMeasurement = BodyMeasurement.WAIST,
    /** Item highlighted in the chart; the newest one with a value for [chartMeasurement] by default. */
    val selectedItem: MeasurementListItem? = null,
    val entryPendingDeletion: BodyMeasurementsEntry? = null,
    val form: BodyMeasurementsFormState = BodyMeasurementsFormState(),
) {
    val latestEntry: BodyMeasurementsEntry?
        get() = items.firstOrNull()?.entry
}
