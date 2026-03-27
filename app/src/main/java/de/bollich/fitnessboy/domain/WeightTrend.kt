package de.bollich.fitnessboy.domain

import de.bollich.fitnessboy.format.formatNumber
import de.bollich.fitnessboy.model.WeightEntry
import java.time.LocalDate
import kotlin.math.absoluteValue

enum class WeightTrendPeriod(
    val label: String,
    val days: Long?,
) {
    SEVEN_DAYS("7 Tage", 7),
    THIRTY_DAYS("30 Tage", 30),
    SINCE_START("Seit Start", null),
}

data class WeightTrend(
    val period: WeightTrendPeriod,
    val deltaInKg: Double,
    val referenceDate: LocalDate,
    val referenceWeightInKg: Double,
    val latestDate: LocalDate,
    val latestWeightInKg: Double,
) {
    val isUpward: Boolean
        get() = deltaInKg > 0

    val isDownward: Boolean
        get() = deltaInKg < 0
}

fun calculateWeightTrend(
    entries: List<WeightEntry>,
    period: WeightTrendPeriod,
): WeightTrend? {
    if (entries.size < 2) return null

    val latestEntry = entries.firstOrNull() ?: return null
    val referenceEntry = when (period) {
        WeightTrendPeriod.SINCE_START -> entries.lastOrNull()
        WeightTrendPeriod.SEVEN_DAYS,
        WeightTrendPeriod.THIRTY_DAYS -> {
            val cutoffDate = latestEntry.date.minusDays(period.days ?: return null)
            entries
                .asReversed()
                .firstOrNull { it.date <= cutoffDate }
                ?: entries.lastOrNull()
        }
    } ?: return null

    if (referenceEntry.date == latestEntry.date && referenceEntry.weightInKg == latestEntry.weightInKg) {
        return null
    }

    return WeightTrend(
        period = period,
        deltaInKg = latestEntry.weightInKg - referenceEntry.weightInKg,
        referenceDate = referenceEntry.date,
        referenceWeightInKg = referenceEntry.weightInKg,
        latestDate = latestEntry.date,
        latestWeightInKg = latestEntry.weightInKg,
    )
}

fun formatTrend(delta: Double): String {
    val direction = when {
        delta > 0 -> "+"
        delta < 0 -> "-"
        else -> ""
    }
    return "$direction${formatNumber(delta.absoluteValue)} kg"
}
