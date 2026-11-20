package de.bollich.fitnessboy.domain

import de.bollich.fitnessboy.model.WeightEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class WeightTrendTest {
    private val today = LocalDate.of(2026, 3, 31)

    /** Builds entries newest first, as delivered by the repository. */
    private fun entries(vararg daysAgoToWeight: Pair<Long, Double>): List<WeightEntry> =
        daysAgoToWeight
            .map { (daysAgo, weight) -> WeightEntry(date = today.minusDays(daysAgo), weightInKg = weight) }
            .sortedByDescending(WeightEntry::date)

    @Test
    fun returnsNullWithLessThanTwoEntries() {
        assertNull(calculateWeightTrend(emptyList(), WeightTrendPeriod.SEVEN_DAYS))
        assertNull(calculateWeightTrend(entries(0L to 80.0), WeightTrendPeriod.SINCE_START))
    }

    @Test
    fun sinceStart_comparesLatestWithOldestEntry() {
        val trend = calculateWeightTrend(
            entries(0L to 78.0, 10L to 80.0, 60L to 85.0),
            WeightTrendPeriod.SINCE_START,
        )!!

        assertEquals(85.0, trend.referenceWeightInKg, 0.0)
        assertEquals(78.0, trend.latestWeightInKg, 0.0)
        assertEquals(-7.0, trend.deltaInKg, 0.0)
        assertTrue(trend.isDownward)
    }

    @Test
    fun sevenDays_usesMostRecentEntryAtOrBeforeCutoff() {
        val trend = calculateWeightTrend(
            entries(0L to 78.0, 3L to 79.0, 8L to 80.0, 20L to 82.0, 60L to 85.0),
            WeightTrendPeriod.SEVEN_DAYS,
        )!!

        assertEquals(today.minusDays(8), trend.referenceDate)
        assertEquals(-2.0, trend.deltaInKg, 0.0)
    }

    @Test
    fun thirtyDays_usesMostRecentEntryAtOrBeforeCutoff() {
        val trend = calculateWeightTrend(
            entries(0L to 78.0, 20L to 82.0, 30L to 83.0, 60L to 85.0),
            WeightTrendPeriod.THIRTY_DAYS,
        )!!

        assertEquals(today.minusDays(30), trend.referenceDate)
        assertEquals(-5.0, trend.deltaInKg, 0.0)
    }

    @Test
    fun shortHistory_fallsBackToOldestEntry() {
        val trend = calculateWeightTrend(
            entries(0L to 78.0, 3L to 80.0),
            WeightTrendPeriod.SEVEN_DAYS,
        )!!

        assertEquals(today.minusDays(3), trend.referenceDate)
        assertEquals(-2.0, trend.deltaInKg, 0.0)
    }

    @Test
    fun upwardTrend() {
        val trend = calculateWeightTrend(
            entries(0L to 81.0, 7L to 80.0),
            WeightTrendPeriod.SEVEN_DAYS,
        )!!

        assertTrue(trend.isUpward)
        assertEquals(1.0, trend.deltaInKg, 0.0)
    }
}
