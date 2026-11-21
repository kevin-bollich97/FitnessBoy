package de.bollich.fitnessboy.ui.weight

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.bollich.fitnessboy.R
import de.bollich.fitnessboy.domain.ChartGroup
import de.bollich.fitnessboy.domain.ChartPeriod
import de.bollich.fitnessboy.format.formatNumber
import de.bollich.fitnessboy.ui.components.chart.ChartGroupDetails
import de.bollich.fitnessboy.ui.components.chart.ChartPeriodChips
import de.bollich.fitnessboy.ui.components.chart.ChartPoint
import de.bollich.fitnessboy.ui.components.chart.LineChart
import de.bollich.fitnessboy.ui.components.chart.chartGroupLabel
import de.bollich.fitnessboy.ui.components.formatKg
import de.bollich.fitnessboy.ui.components.withBmi

/** Weight over a chosen period with the target as dashed line; tapping a point shows its details. */
@Composable
internal fun WeightChartCard(
    period: ChartPeriod,
    groups: List<ChartGroup>,
    selectedGroup: ChartGroup?,
    selectedGroupBmi: Double?,
    targetWeightInKg: Double?,
    onPeriodChange: (ChartPeriod) -> Unit,
    onGroupSelected: (ChartGroup) -> Unit,
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.weight_chart_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            ChartPeriodChips(selected = period, onSelect = onPeriodChange)
            if (groups.isEmpty()) {
                Text(text = stringResource(R.string.chart_period_empty), style = MaterialTheme.typography.bodyLarge)
                return@Column
            }

            val points = remember(groups) { groups.map { ChartPoint(it.start, it.average) } }
            LineChart(
                points = points,
                selectedIndex = groups.indexOf(selectedGroup).takeIf { it >= 0 },
                onSelect = { onGroupSelected(groups[it]) },
                formatValue = ::formatNumber,
                unit = stringResource(R.string.unit_kg),
                referenceValue = targetWeightInKg,
                description = stringResource(
                    R.string.weight_chart_description,
                    chartGroupLabel(groups.first()),
                    chartGroupLabel(groups.last()),
                    formatKg(groups.minOf { it.average }),
                    formatKg(groups.maxOf { it.average }),
                ),
            )
            if (targetWeightInKg != null) {
                Text(
                    text = stringResource(R.string.weight_chart_target_legend, formatKg(targetWeightInKg)),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            selectedGroup?.let { ChartGroupDetails(group = it, singleEntryBmi = selectedGroupBmi) { value -> formatKg(value) } }
        }
    }
}

/** "125 kg · BMI 40,8", or just the weight if the height is unknown. */
@Composable
internal fun weightWithBmi(item: WeightListItem): String = withBmi(formatKg(item.entry.weightInKg), item.bmi)
