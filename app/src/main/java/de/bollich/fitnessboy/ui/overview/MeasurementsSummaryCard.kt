package de.bollich.fitnessboy.ui.overview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.bollich.fitnessboy.R
import de.bollich.fitnessboy.format.formattedDate
import de.bollich.fitnessboy.model.BodyMeasurementsEntry
import de.bollich.fitnessboy.domain.measuredValues
import de.bollich.fitnessboy.ui.measurements.measurementValueText

@Composable
internal fun MeasurementsSummaryCard(
    latestMeasurements: BodyMeasurementsEntry?,
    onClick: () -> Unit,
) {
    OverviewCard(
        title = stringResource(R.string.overview_measurements_title),
        onClick = onClick,
    ) {
        if (latestMeasurements == null) {
            Text(
                text = stringResource(R.string.overview_measurements_empty),
                style = MaterialTheme.typography.bodyLarge,
            )
            return@OverviewCard
        }

        Text(
            text = stringResource(R.string.overview_measurements_measured_on, latestMeasurements.date.formattedDate()),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            latestMeasurements.measuredValues().forEach { (measurement, valueInCm) ->
                Text(
                    text = measurementValueText(measurement, valueInCm),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}
