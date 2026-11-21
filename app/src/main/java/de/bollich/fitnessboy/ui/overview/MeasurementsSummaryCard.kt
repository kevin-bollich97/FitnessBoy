package de.bollich.fitnessboy.ui.overview

import androidx.annotation.StringRes
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
import de.bollich.fitnessboy.ui.components.formatCm

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
            MeasurementValue(R.string.measurements_waist, latestMeasurements.waistInCm)
            MeasurementValue(R.string.measurements_hips, latestMeasurements.hipsInCm)
            MeasurementValue(R.string.measurements_shoulders, latestMeasurements.shouldersInCm)
        }
    }
}

@Composable
private fun MeasurementValue(@StringRes labelRes: Int, valueInCm: Double) {
    Text(
        text = stringResource(R.string.overview_measurement_value, stringResource(labelRes), formatCm(valueInCm)),
        style = MaterialTheme.typography.bodyLarge,
    )
}
