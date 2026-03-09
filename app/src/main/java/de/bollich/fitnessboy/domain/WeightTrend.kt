package de.bollich.fitnessboy.domain

import de.bollich.fitnessboy.format.formatNumber
import kotlin.math.absoluteValue

fun formatTrend(delta: Double): String {
    val direction = when {
        delta > 0 -> "+"
        delta < 0 -> "-"
        else -> ""
    }
    return "$direction${formatNumber(delta.absoluteValue)} kg"
}
