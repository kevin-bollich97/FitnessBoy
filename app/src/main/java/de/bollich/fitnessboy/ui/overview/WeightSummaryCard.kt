package de.bollich.fitnessboy.ui.overview

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import de.bollich.fitnessboy.R
import de.bollich.fitnessboy.domain.WeightTrend
import de.bollich.fitnessboy.format.formatSignedNumber
import de.bollich.fitnessboy.format.formattedDate
import de.bollich.fitnessboy.model.WeightEntry
import de.bollich.fitnessboy.ui.components.formatKg

@Composable
internal fun WeightSummaryCard(
    latestWeight: WeightEntry?,
    recentTrend: WeightTrend?,
    totalTrend: WeightTrend?,
    onClick: () -> Unit,
) {
    OverviewCard(
        title = stringResource(R.string.overview_weight_title),
        onClick = onClick.takeIf { latestWeight != null },
    ) {
        if (latestWeight == null) {
            Text(
                text = stringResource(R.string.overview_weight_empty),
                style = MaterialTheme.typography.bodyLarge,
            )
            return@OverviewCard
        }

        Text(
            text = formatKg(latestWeight.weightInKg),
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = stringResource(R.string.overview_weight_measured_on, latestWeight.formattedDate()),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        recentTrend?.let {
            TrendText(stringResource(R.string.overview_trend_since_date, signedKg(it), it.referenceDate.formattedDate()))
        }
        totalTrend?.let {
            TrendText(stringResource(R.string.overview_trend_since_start, signedKg(it), it.referenceDate.formattedDate()))
        }
    }
}

@Composable
private fun TrendText(text: String) {
    Text(text = text, style = MaterialTheme.typography.bodyLarge)
}

@Composable
private fun signedKg(trend: WeightTrend): String =
    stringResource(R.string.value_kg, formatSignedNumber(trend.deltaInKg))
