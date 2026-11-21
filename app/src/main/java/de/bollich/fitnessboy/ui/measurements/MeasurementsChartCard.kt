package de.bollich.fitnessboy.ui.measurements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.bollich.fitnessboy.R
import de.bollich.fitnessboy.domain.BodyMeasurement
import de.bollich.fitnessboy.domain.valueOf
import de.bollich.fitnessboy.format.formatNumber
import de.bollich.fitnessboy.format.formattedDate
import de.bollich.fitnessboy.model.BodyMeasurementsEntry
import de.bollich.fitnessboy.ui.components.chart.ChartPoint
import de.bollich.fitnessboy.ui.components.chart.LineChart
import de.bollich.fitnessboy.ui.components.formatCm
import de.bollich.fitnessboy.ui.components.withBmi

/** One measurement over time, chosen by chips; tapping a point shows its details below. */
@Composable
internal fun MeasurementsChartCard(
    items: List<MeasurementListItem>,
    measurement: BodyMeasurement,
    selectedItem: MeasurementListItem?,
    onMeasurementChange: (BodyMeasurement) -> Unit,
    onEntrySelected: (BodyMeasurementsEntry) -> Unit,
) {
    val chronological = remember(items, measurement) {
        items.reversed().filter { it.entry.valueOf(measurement) != null }
    }
    val label = stringResource(measurement.labelRes)

    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.measurements_chart_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BodyMeasurement.entries.forEach {
                    FilterChip(
                        selected = it == measurement,
                        onClick = { onMeasurementChange(it) },
                        label = { Text(stringResource(it.labelRes)) },
                    )
                }
            }
            if (chronological.isEmpty()) {
                Text(
                    text = stringResource(R.string.measurements_chart_empty, label),
                    style = MaterialTheme.typography.bodyLarge,
                )
                return@Column
            }

            val values = chronological.map { checkNotNull(it.entry.valueOf(measurement)) }
            LineChart(
                points = chronological.zip(values) { item, value -> ChartPoint(item.entry.date, value) },
                selectedIndex = chronological.indexOf(selectedItem).takeIf { it >= 0 },
                onSelect = { onEntrySelected(chronological[it].entry) },
                formatValue = ::formatNumber,
                unit = stringResource(R.string.unit_cm),
                description = stringResource(
                    R.string.measurements_chart_description,
                    label,
                    chronological.first().entry.date.formattedDate(),
                    chronological.last().entry.date.formattedDate(),
                    formatCm(values.min()),
                    formatCm(values.max()),
                ),
            )
            selectedItem?.let { SelectedMeasurementDetails(it, measurement) }
        }
    }
}

@Composable
private fun SelectedMeasurementDetails(item: MeasurementListItem, measurement: BodyMeasurement) {
    val value = item.entry.valueOf(measurement) ?: return
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = item.entry.date.formattedDate(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = withBmi(measurementValueText(measurement, value), item.bmi),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = stringResource(R.string.chart_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
