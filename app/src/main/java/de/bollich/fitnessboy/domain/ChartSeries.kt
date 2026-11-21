package de.bollich.fitnessboy.domain

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters

/** A measured value on a day, e.g. a weight or a waist measurement. */
data class DatedValue(
    val date: LocalDate,
    val value: Double,
)

/** Time range shown in a chart, counted back from today; [days] `null` means everything. */
enum class ChartPeriod(val days: Long?) {
    ONE_MONTH(30),
    THREE_MONTHS(91),
    ONE_YEAR(365),
    ALL(null),
}

/** How values are combined into one chart point. */
enum class ChartGrouping {
    DAY,
    WEEK,
    MONTH,
}

/** One chart point: the average of [count] values between [start] and [end] (both inclusive). */
data class ChartGroup(
    val grouping: ChartGrouping,
    val start: LocalDate,
    val end: LocalDate,
    val average: Double,
    val count: Int,
)

/** Longest span of visible values (in days) that is still shown per day, respectively per week. */
private const val DAILY_MAX_SPAN_DAYS = 92L
private const val WEEKLY_MAX_SPAN_DAYS = 366L

/**
 * Prepares [values] for a chart: keeps those within [period] before [today] and groups them so
 * the chart never gets crowded – per day for up to three months, per week (Monday to Sunday) for
 * up to a year, per month beyond that. Each group holds the average. Sorted oldest first.
 */
fun summarizeForChart(
    values: List<DatedValue>,
    period: ChartPeriod,
    today: LocalDate,
): List<ChartGroup> {
    val visible = period.days
        ?.let { days -> values.filter { it.date > today.minusDays(days) && it.date <= today } }
        ?: values
    if (visible.isEmpty()) return emptyList()

    val grouping = groupingFor(spanInDays = ChronoUnit.DAYS.between(visible.minOf { it.date }, visible.maxOf { it.date }))
    return visible
        .groupBy { grouping.startOf(it.date) }
        .map { (start, group) ->
            ChartGroup(
                grouping = grouping,
                start = start,
                end = grouping.endOf(start),
                average = group.map(DatedValue::value).average(),
                count = group.size,
            )
        }
        .sortedBy(ChartGroup::start)
}

internal fun groupingFor(spanInDays: Long): ChartGrouping =
    when {
        spanInDays <= DAILY_MAX_SPAN_DAYS -> ChartGrouping.DAY
        spanInDays <= WEEKLY_MAX_SPAN_DAYS -> ChartGrouping.WEEK
        else -> ChartGrouping.MONTH
    }

private fun ChartGrouping.startOf(date: LocalDate): LocalDate =
    when (this) {
        ChartGrouping.DAY -> date
        ChartGrouping.WEEK -> date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        ChartGrouping.MONTH -> date.withDayOfMonth(1)
    }

private fun ChartGrouping.endOf(start: LocalDate): LocalDate =
    when (this) {
        ChartGrouping.DAY -> start
        ChartGrouping.WEEK -> start.plusDays(6)
        ChartGrouping.MONTH -> start.with(TemporalAdjusters.lastDayOfMonth())
    }
