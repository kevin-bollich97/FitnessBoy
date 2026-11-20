package de.bollich.fitnessboy.ui.weight

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import de.bollich.fitnessboy.R
import de.bollich.fitnessboy.domain.WeightTrendPeriod
import de.bollich.fitnessboy.format.formatNumber
import de.bollich.fitnessboy.format.formatSignedNumber
import de.bollich.fitnessboy.ui.components.HeadlineMetric
import de.bollich.fitnessboy.ui.components.formatKg

/** Latest weight and BMI. */
@Composable
fun WeightUiState.headlineMetrics(): List<HeadlineMetric> =
    listOfNotNull(
        latestEntry?.let {
            HeadlineMetric(stringResource(R.string.metric_latest_weight), formatKg(it.weightInKg))
        },
        bmi?.let { HeadlineMetric(stringResource(R.string.metric_bmi), formatNumber(it)) },
    )

/** One pill per available trend period. */
@Composable
fun WeightUiState.trendMetrics(): List<HeadlineMetric> =
    trends.map {
        HeadlineMetric(
            label = stringResource(it.period.labelRes),
            value = stringResource(R.string.value_kg, formatSignedNumber(it.deltaInKg)),
        )
    }

@get:StringRes
private val WeightTrendPeriod.labelRes: Int
    get() = when (this) {
        WeightTrendPeriod.SEVEN_DAYS -> R.string.trend_period_seven_days
        WeightTrendPeriod.THIRTY_DAYS -> R.string.trend_period_thirty_days
        WeightTrendPeriod.SINCE_START -> R.string.trend_period_since_start
    }
