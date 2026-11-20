package de.bollich.fitnessboy.ui.weight

import de.bollich.fitnessboy.domain.formatTrend
import de.bollich.fitnessboy.format.formatNumber
import de.bollich.fitnessboy.format.formattedWeight
import de.bollich.fitnessboy.ui.components.HeadlineMetric

/** Latest weight and BMI. */
fun WeightUiState.headlineMetrics(): List<HeadlineMetric> =
    listOfNotNull(
        latestEntry?.let { HeadlineMetric("Letzter Wert", it.formattedWeight()) },
        bmi?.let { HeadlineMetric("BMI", formatNumber(it)) },
    )

/** One pill per available trend period. */
fun WeightUiState.trendMetrics(): List<HeadlineMetric> =
    trends.map { HeadlineMetric(it.period.label, formatTrend(it.deltaInKg)) }
