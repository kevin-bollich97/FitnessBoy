package de.bollich.fitnessboy.ui.weight

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import de.bollich.fitnessboy.R
import de.bollich.fitnessboy.domain.WeightTrendPeriod
import de.bollich.fitnessboy.format.formatNumber
import de.bollich.fitnessboy.format.formatTrend
import de.bollich.fitnessboy.format.formattedWeight
import de.bollich.fitnessboy.ui.components.HeadlineMetric

/** Latest weight and BMI. */
@Composable
fun WeightUiState.headlineMetrics(): List<HeadlineMetric> =
    listOfNotNull(
        latestEntry?.let { HeadlineMetric("Letzter Wert", it.formattedWeight()) },
        bmi?.let { HeadlineMetric("BMI", formatNumber(it)) },
    )

/** One pill per available trend period. */
@Composable
fun WeightUiState.trendMetrics(): List<HeadlineMetric> =
    trends.map { HeadlineMetric(stringResource(it.period.labelRes), formatTrend(it.deltaInKg)) }

@get:StringRes
private val WeightTrendPeriod.labelRes: Int
    get() = when (this) {
        WeightTrendPeriod.SEVEN_DAYS -> R.string.trend_period_seven_days
        WeightTrendPeriod.THIRTY_DAYS -> R.string.trend_period_thirty_days
        WeightTrendPeriod.SINCE_START -> R.string.trend_period_since_start
    }
