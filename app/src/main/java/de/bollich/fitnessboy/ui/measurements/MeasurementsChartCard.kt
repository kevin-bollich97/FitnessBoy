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
import androidx.compose.ui.unit.dp
import de.bollich.fitnessboy.R
import de.bollich.fitnessboy.domain.BodyMeasurement
import de.bollich.fitnessboy.domain.ChartGroup
import de.bollich.fitnessboy.domain.ChartPeriod
import de.bollich.fitnessboy.format.formatNumber
import de.bollich.fitnessboy.ui.components.chart.ChartGroupDetails
import de.bollich.fitnessboy.ui.components.chart.ChartPeriodChips
import de.bollich.fitnessboy.ui.components.chart.ChartPoint
import de.bollich.fitnessboy.ui.components.chart.LineChart
import de.bollich.fitnessboy.ui.components.chart.chartGroupLabel
import de.bollich.fitnessboy.ui.components.formatCm

/** One measurement over a chosen period; tapping a point shows its details below. */
@Composable
internal fun MeasurementsChartCard(
    measurement: BodyMeasurement,
    period: ChartPeriod,
    groups: List<ChartGroup>,
    selectedGroup: ChartGroup?,
    selectedGroupBmi: Double?,
    hasAnyValue: Boolean,
    onMeasurementChange: (BodyMeasurement) -> Unit,
    onPeriodChange: (ChartPeriod) -> Unit,
    onGroupSelected: (ChartGroup) -> Unit,
) {
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
            if (!hasAnyValue) {
                Text(text = stringResource(R.string.measurements_chart_empty, label), style = MaterialTheme.typography.bodyLarge)
                return@Column
            }
            ChartPeriodChips(selected = period, onSelect = onPeriodChange)
            if (groups.isEmpty()) {
                Text(text = stringResource(R.string.chart_period_empty), style = MaterialTheme.typography.bodyLarge)
                return@Column
            }

            val points = remember(groups) { groups.map { ChartPoint(it.centerDate, it.average) } }
            LineChart(
                points = points,
                selectedIndex = groups.indexOf(selectedGroup).takeIf { it >= 0 },
                onSelect = { onGroupSelected(groups[it]) },
                formatValue = ::formatNumber,
                unit = stringResource(R.string.unit_cm),
                description = stringResource(
                    R.string.measurements_chart_description,
                    label,
                    chartGroupLabel(groups.first()),
                    chartGroupLabel(groups.last()),
                    formatCm(groups.minOf { it.average }),
                    formatCm(groups.maxOf { it.average }),
                ),
            )
            selectedGroup?.let {
                ChartGroupDetails(group = it, singleEntryBmi = selectedGroupBmi) { value -> measurementValueText(measurement, value) }
            }
        }
    }
}
