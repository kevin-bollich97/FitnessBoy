package de.bollich.fitnessboy.ui.measurements

import de.bollich.fitnessboy.domain.BodyMeasurement
import de.bollich.fitnessboy.domain.ChartGroup
import de.bollich.fitnessboy.domain.ChartPeriod
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
    /** Whether [chartMeasurement] was ever measured, regardless of the period. */
    val chartMeasurementHasValues: Boolean = false,
    val chartPeriod: ChartPeriod = ChartPeriod.ALL,
    /** Chart points of [chartMeasurement] for [chartPeriod], oldest first. */
    val chartGroups: List<ChartGroup> = emptyList(),
    /** Point highlighted in the chart; the newest one unless the user tapped another. */
    val selectedGroup: ChartGroup? = null,
    /** BMI on the date of [selectedGroup] if it stands for exactly one entry. */
    val selectedGroupBmi: Double? = null,
    val showAllEntries: Boolean = false,
    val entryPendingDeletion: BodyMeasurementsEntry? = null,
    val form: BodyMeasurementsFormState = BodyMeasurementsFormState(),
) {
    val latestEntry: BodyMeasurementsEntry?
        get() = items.firstOrNull()?.entry
}
