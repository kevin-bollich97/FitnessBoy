package de.bollich.fitnessboy.ui.measurements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.bollich.fitnessboy.R
import de.bollich.fitnessboy.domain.BodyMeasurement
import de.bollich.fitnessboy.domain.valueOf
import de.bollich.fitnessboy.format.formattedDate
import de.bollich.fitnessboy.model.BodyMeasurementsEntry

/** Figure with all measuring points next to the values of the latest entry. */
@Composable
internal fun LatestMeasurementsCard(
    latestEntry: BodyMeasurementsEntry,
    highlighted: BodyMeasurement,
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column {
                Text(
                    text = stringResource(R.string.measurements_latest_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = stringResource(R.string.overview_measurements_measured_on, latestEntry.date.formattedDate()),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                itemVerticalAlignment = Alignment.CenterVertically,
            ) {
                BodyFigure(highlighted = highlighted, modifier = Modifier.size(width = 96.dp, height = 152.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    BodyMeasurement.entries.forEach { measurement ->
                        MeasurementValueRow(latestEntry, measurement, isHighlighted = measurement == highlighted)
                    }
                }
            }
        }
    }
}

@Composable
private fun MeasurementValueRow(
    entry: BodyMeasurementsEntry,
    measurement: BodyMeasurement,
    isHighlighted: Boolean,
) {
    val value = entry.valueOf(measurement)
    val label = stringResource(measurement.labelRes)
    val notMeasured = stringResource(R.string.measurement_value, label, stringResource(R.string.measurement_not_measured))
    Text(
        text = value?.let { measurementValueText(measurement, it) }
            ?: stringResource(R.string.measurement_value, label, stringResource(R.string.value_missing)),
        modifier = if (value == null) Modifier.semantics { contentDescription = notMeasured } else Modifier,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal,
        color = if (value != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
    )
}
