package de.bollich.fitnessboy.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class ChartSeriesTest {
    private val today = LocalDate.of(2026, 11, 21)

    private fun value(date: LocalDate, value: Double) = DatedValue(date, value)

    @Test
    fun groupingFor_dependsOnSpan() {
        assertEquals(ChartGrouping.DAY, groupingFor(0))
        assertEquals(ChartGrouping.DAY, groupingFor(92))
        assertEquals(ChartGrouping.WEEK, groupingFor(93))
        assertEquals(ChartGrouping.WEEK, groupingFor(366))
        assertEquals(ChartGrouping.MONTH, groupingFor(367))
    }

    @Test
    fun shortSpan_isGroupedPerDayWithAverageOfSameDay() {
        val groups = summarizeForChart(
            listOf(
                value(LocalDate.of(2026, 11, 20), 81.0),
                value(LocalDate.of(2026, 11, 1), 83.0),
                value(LocalDate.of(2026, 11, 20), 80.0),
            ),
            ChartPeriod.ALL,
            today,
        )

        assertEquals(listOf(LocalDate.of(2026, 11, 1), LocalDate.of(2026, 11, 20)), groups.map { it.start })
        assertTrue(groups.all { it.grouping == ChartGrouping.DAY && it.start == it.end })
        assertEquals(80.5, groups.last().average, 0.0)
        assertEquals(2, groups.last().count)
    }

    @Test
    fun spanUpToAYear_isGroupedPerIsoWeek() {
        val groups = summarizeForChart(
            listOf(
                value(LocalDate.of(2026, 3, 2), 90.0), // Monday
                value(LocalDate.of(2026, 3, 8), 88.0), // Sunday, same week
                value(LocalDate.of(2026, 3, 9), 87.0), // next Monday
                value(LocalDate.of(2026, 11, 20), 80.0),
            ),
            ChartPeriod.ALL,
            today,
        )

        assertEquals(ChartGrouping.WEEK, groups.first().grouping)
        assertEquals(LocalDate.of(2026, 3, 2), groups[0].start)
        assertEquals(LocalDate.of(2026, 3, 8), groups[0].end)
        assertEquals(89.0, groups[0].average, 0.0)
        assertEquals(2, groups[0].count)
        assertEquals(LocalDate.of(2026, 3, 9), groups[1].start)
        assertEquals(LocalDate.of(2026, 11, 16), groups[2].start)
    }

    @Test
    fun longSpan_isGroupedPerMonth() {
        val groups = summarizeForChart(
            listOf(
                value(LocalDate.of(2025, 1, 5), 95.0),
                value(LocalDate.of(2025, 1, 25), 93.0),
                value(LocalDate.of(2026, 11, 20), 80.0),
            ),
            ChartPeriod.ALL,
            today,
        )

        assertEquals(ChartGrouping.MONTH, groups.first().grouping)
        assertEquals(LocalDate.of(2025, 1, 1), groups[0].start)
        assertEquals(LocalDate.of(2025, 1, 31), groups[0].end)
        assertEquals(94.0, groups[0].average, 0.0)
        assertEquals(LocalDate.of(2026, 11, 1), groups[1].start)
    }

    @Test
    fun period_keepsOnlyValuesWithinTheLastDays() {
        val values = listOf(
            value(LocalDate.of(2026, 3, 1), 90.0),
            value(LocalDate.of(2026, 10, 22), 82.0), // exactly 30 days ago: outside
            value(LocalDate.of(2026, 10, 23), 81.0),
            value(LocalDate.of(2026, 11, 21), 80.0),
        )

        val groups = summarizeForChart(values, ChartPeriod.ONE_MONTH, today)

        assertEquals(listOf(LocalDate.of(2026, 10, 23), LocalDate.of(2026, 11, 21)), groups.map { it.start })
    }

    @Test
    fun periodWithoutValues_isEmpty() {
        val groups = summarizeForChart(listOf(value(LocalDate.of(2026, 3, 1), 90.0)), ChartPeriod.THREE_MONTHS, today)

        assertTrue(groups.isEmpty())
    }
}
