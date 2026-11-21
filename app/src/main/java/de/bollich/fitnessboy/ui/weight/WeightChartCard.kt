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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.bollich.fitnessboy.R
import de.bollich.fitnessboy.format.formatNumber
import de.bollich.fitnessboy.format.formattedDate
import de.bollich.fitnessboy.model.WeightEntry
import de.bollich.fitnessboy.ui.components.chart.ChartPoint
import de.bollich.fitnessboy.ui.components.chart.LineChart
import de.bollich.fitnessboy.ui.components.formatKg

/** Weight over time with the target as dashed line; tapping a point shows its details below. */
@Composable
internal fun WeightChartCard(
    items: List<WeightListItem>,
    selectedItem: WeightListItem?,
    targetWeightInKg: Double?,
    onEntrySelected: (WeightEntry) -> Unit,
) {
    val chronological = remember(items) { items.reversed() }
    val points = remember(chronological) { chronological.map { ChartPoint(it.entry.date, it.entry.weightInKg) } }
    val first = chronological.first().entry
    val last = chronological.last().entry
    val weights = chronological.map { it.entry.weightInKg }

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
            LineChart(
                points = points,
                selectedIndex = chronological.indexOf(selectedItem).takeIf { it >= 0 },
                onSelect = { onEntrySelected(chronological[it].entry) },
                formatValue = ::formatNumber,
                unit = stringResource(R.string.unit_kg),
                referenceValue = targetWeightInKg,
                description = stringResource(
                    R.string.weight_chart_description,
                    first.formattedDate(),
                    last.formattedDate(),
                    formatKg(weights.min()),
                    formatKg(weights.max()),
                ),
            )
            if (targetWeightInKg != null) {
                Text(
                    text = stringResource(R.string.weight_chart_target_legend, formatKg(targetWeightInKg)),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            selectedItem?.let { SelectedEntryDetails(it) }
        }
    }
}

@Composable
private fun SelectedEntryDetails(item: WeightListItem) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = item.entry.formattedDate(),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = weightWithBmi(item),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = stringResource(R.string.weight_chart_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** "125 kg · BMI 40,8", or just the weight if the height is unknown. */
@Composable
internal fun weightWithBmi(item: WeightListItem): String {
    val weight = formatKg(item.entry.weightInKg)
    return item.bmi?.let { stringResource(R.string.weight_with_bmi, weight, formatNumber(it)) } ?: weight
}
