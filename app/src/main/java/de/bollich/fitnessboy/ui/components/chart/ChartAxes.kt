package de.bollich.fitnessboy.ui.components.chart

import java.time.LocalDate
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.roundToLong

/** Value axis with "nice" round tick values, e.g. 100, 110, 120, 130. */
data class ValueAxis(
    val min: Double,
    val max: Double,
    val step: Double,
) {
    val ticks: List<Double>
        get() {
            val count = ((max - min) / step).roundToInt()
            return (0..count).map { index -> roundToStep(min + index * step) }
        }

    /** Position of [value] between [min] (0) and [max] (1). */
    fun fractionOf(value: Double): Float = ((value - min) / (max - min)).toFloat()

    private fun roundToStep(value: Double): Double {
        val decimals = (-floor(log10(step))).coerceAtLeast(0.0)
        val factor = 10.0.pow(decimals)
        return (value * factor).roundToLong() / factor
    }
}

/**
 * Builds an axis that contains all [values] and uses round steps of 1, 2 or 5 times a power of ten,
 * aiming for about [targetTickCount] ticks.
 */
fun valueAxisFor(values: List<Double>, targetTickCount: Int = 5): ValueAxis {
    require(values.isNotEmpty()) { "values must not be empty" }
    require(targetTickCount >= 2) { "targetTickCount must be at least 2" }

    var minValue = values.min()
    var maxValue = values.max()
    if (minValue == maxValue) {
        minValue -= 1.0
        maxValue += 1.0
    }

    val step = niceStep((maxValue - minValue) / (targetTickCount - 1))
    return ValueAxis(
        min = floor(minValue / step) * step,
        max = ceil(maxValue / step) * step,
        step = step,
    )
}

private fun niceStep(rawStep: Double): Double {
    val magnitude = 10.0.pow(floor(log10(rawStep)))
    val nice = when (rawStep / magnitude) {
        in 0.0..1.0 -> 1.0
        in 1.0..2.0 -> 2.0
        in 2.0..5.0 -> 5.0
        else -> 10.0
    }
    return nice * magnitude
}

/** Time axis from [start] to [end]; positions are proportional to the number of days. */
data class DateAxis(
    val start: LocalDate,
    val end: LocalDate,
) {
    private val totalDays: Long = end.toEpochDay() - start.toEpochDay()

    /** Position of [date] between [start] (0) and [end] (1); a single day sits in the middle. */
    fun fractionOf(date: LocalDate): Float =
        if (totalDays == 0L) 0.5f else (date.toEpochDay() - start.toEpochDay()).toFloat() / totalDays

    /** Up to [maxCount] evenly spaced dates including [start] and [end], without duplicates. */
    fun ticks(maxCount: Int): List<LocalDate> {
        if (totalDays == 0L || maxCount < 2) return listOf(start)
        val count = maxCount.coerceAtMost(totalDays.toInt() + 1)
        return (0 until count)
            .map { index -> start.plusDays((totalDays * index / (count - 1).toDouble()).roundToLong()) }
            .distinct()
    }
}

fun dateAxisFor(dates: List<LocalDate>): DateAxis {
    require(dates.isNotEmpty()) { "dates must not be empty" }
    return DateAxis(start = dates.min(), end = dates.max())
}
