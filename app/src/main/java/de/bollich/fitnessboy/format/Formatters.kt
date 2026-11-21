package de.bollich.fitnessboy.format

import de.bollich.fitnessboy.model.WeightEntry
import java.math.RoundingMode
import java.text.NumberFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.absoluteValue

private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
private val monthFormatter = DateTimeFormatter.ofPattern("LLLL yyyy", Locale.GERMAN)

fun WeightEntry.formattedDate(): String = date.format(dateFormatter)

fun LocalDate.formattedDate(): String = format(dateFormatter)

/** "November 2026" */
fun YearMonth.formattedMonth(): String = format(monthFormatter)

/** Formats with at most one decimal in the user's locale, e.g. "80,5" (de) or "80.5" (en). */
fun formatNumber(value: Double, locale: Locale = Locale.getDefault()): String =
    NumberFormat.getNumberInstance(locale).apply {
        maximumFractionDigits = 1
        roundingMode = RoundingMode.HALF_UP
        isGroupingUsed = false
    }.format(value)

/** Like [formatNumber] but with an explicit "+" or "-"; values that round to zero get no sign. */
fun formatSignedNumber(value: Double, locale: Locale = Locale.getDefault()): String {
    val magnitude = formatNumber(value.absoluteValue, locale)
    val sign = when {
        magnitude == formatNumber(0.0, locale) -> ""
        value > 0 -> "+"
        else -> "-"
    }
    return sign + magnitude
}
