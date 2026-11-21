package de.bollich.fitnessboy.ui.components

import java.time.LocalDate
import java.time.YearMonth

/** Entries of one month for a list with month headers; [totalCount] counts hidden entries too. */
data class MonthSection<T>(
    val month: YearMonth,
    val totalCount: Int,
    val items: List<T>,
)

/**
 * Splits [items] (newest first) into month sections. With [limit], only the first [limit] items
 * are kept, while each section still reports how many entries its month has in total.
 */
fun <T> sectionsByMonth(items: List<T>, dateOf: (T) -> LocalDate, limit: Int? = null): List<MonthSection<T>> {
    val visible = limit?.let { items.take(it) } ?: items
    val totals = items.groupingBy { YearMonth.from(dateOf(it)) }.eachCount()
    return visible
        .groupBy { YearMonth.from(dateOf(it)) }
        .map { (month, monthItems) -> MonthSection(month, totals.getValue(month), monthItems) }
}
