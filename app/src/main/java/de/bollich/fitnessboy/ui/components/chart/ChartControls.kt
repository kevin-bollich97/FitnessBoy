package de.bollich.fitnessboy.ui.components.chart

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.bollich.fitnessboy.R
import de.bollich.fitnessboy.domain.ChartGroup
import de.bollich.fitnessboy.domain.ChartGrouping
import de.bollich.fitnessboy.domain.ChartPeriod
import de.bollich.fitnessboy.format.formattedDate
import de.bollich.fitnessboy.format.formattedMonth
import de.bollich.fitnessboy.ui.components.WrappingSelectionField
import de.bollich.fitnessboy.ui.components.withBmi
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.IsoFields

private val dayMonthFormatter = DateTimeFormatter.ofPattern("dd.MM.")

@get:StringRes
private val ChartPeriod.labelRes: Int
    get() = when (this) {
        ChartPeriod.ONE_MONTH -> R.string.chart_period_one_month
        ChartPeriod.THREE_MONTHS -> R.string.chart_period_three_months
        ChartPeriod.ONE_YEAR -> R.string.chart_period_one_year
        ChartPeriod.ALL -> R.string.chart_period_all
    }

/** Row of fields that filter a chart; it wraps them with large font. */
@Composable
fun ChartFilterRow(content: @Composable FlowRowScope.() -> Unit) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        content = content,
    )
}

/** Field to choose the time range of a chart. */
@Composable
fun FlowRowScope.ChartPeriodField(
    selected: ChartPeriod,
    onSelect: (ChartPeriod) -> Unit,
) {
    WrappingSelectionField(
        label = stringResource(R.string.chart_period_label),
        options = ChartPeriod.entries,
        selected = selected,
        optionLabel = { stringResource(it.labelRes) },
        onSelect = onSelect,
    )
}

/**
 * "20.11.2026", "KW 47/2026 · 16.11.–22.11." or "November 2026"; a point from a single entry is
 * labelled with the date of that entry.
 */
@Composable
fun chartGroupLabel(group: ChartGroup): String =
    when {
        group.count == 1 -> group.lastDate.formattedDate()
        else -> periodLabel(group)
    }

@Composable
private fun periodLabel(group: ChartGroup): String =
    when (group.grouping) {
        ChartGrouping.DAY -> group.start.formattedDate()
        ChartGrouping.WEEK -> stringResource(
            R.string.chart_group_week,
            group.start.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR),
            group.start.get(IsoFields.WEEK_BASED_YEAR),
            group.start.format(dayMonthFormatter),
            group.end.format(dayMonthFormatter),
        )
        ChartGrouping.MONTH -> YearMonth.from(group.start).formattedMonth()
    }

/**
 * Details of the selected chart point: its period and value. A point from a single entry shows
 * the value with its BMI; a combined point shows the average and how many entries it covers.
 */
@Composable
fun ChartGroupDetails(
    group: ChartGroup,
    singleEntryBmi: Double?,
    formatValue: @Composable (Double) -> String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(
            text = chartGroupLabel(group),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = if (group.count == 1) {
                withBmi(formatValue(group.average), singleEntryBmi)
            } else {
                stringResource(
                    R.string.chart_group_average,
                    formatValue(group.average),
                    pluralStringResource(R.plurals.entry_count, group.count, group.count),
                )
            },
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
