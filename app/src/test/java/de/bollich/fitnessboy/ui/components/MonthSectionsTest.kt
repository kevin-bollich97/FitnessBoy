package de.bollich.fitnessboy.ui.components

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.YearMonth

class MonthSectionsTest {
    private val dates = listOf(
        LocalDate.of(2026, 11, 20),
        LocalDate.of(2026, 11, 6),
        LocalDate.of(2026, 10, 27),
        LocalDate.of(2026, 10, 24),
        LocalDate.of(2026, 9, 6),
    )

    @Test
    fun sections_followTheOrderOfTheItems() {
        val sections = sectionsByMonth(dates, dateOf = { it })

        assertEquals(listOf(YearMonth.of(2026, 11), YearMonth.of(2026, 10), YearMonth.of(2026, 9)), sections.map { it.month })
        assertEquals(listOf(2, 2, 1), sections.map { it.items.size })
    }

    @Test
    fun limit_keepsFirstItemsButCountsWholeMonth() {
        val sections = sectionsByMonth(dates, dateOf = { it }, limit = 3)

        assertEquals(listOf(YearMonth.of(2026, 11), YearMonth.of(2026, 10)), sections.map { it.month })
        assertEquals(listOf(2, 1), sections.map { it.items.size })
        assertEquals(listOf(2, 2), sections.map { it.totalCount })
    }
}
