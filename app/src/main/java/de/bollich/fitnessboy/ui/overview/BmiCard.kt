package de.bollich.fitnessboy.ui.overview

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import de.bollich.fitnessboy.R
import de.bollich.fitnessboy.domain.BmiCategory
import de.bollich.fitnessboy.format.formatNumber

/** Current BMI with its category. Opens the explanation, or the profile if the height is missing. */
@Composable
internal fun BmiCard(
    bmi: Double?,
    bmiCategory: BmiCategory?,
    hasHeight: Boolean,
    onShowExplanation: () -> Unit,
    onOpenProfile: () -> Unit,
) {
    when {
        !hasHeight -> OverviewCard(
            title = stringResource(R.string.overview_bmi_title),
            onClick = onOpenProfile,
        ) {
            Text(
                text = stringResource(R.string.overview_bmi_missing_height),
                style = MaterialTheme.typography.bodyLarge,
            )
        }

        bmi != null && bmiCategory != null -> OverviewCard(
            title = stringResource(R.string.overview_bmi_title),
            onClick = onShowExplanation,
        ) {
            Text(
                text = formatNumber(bmi),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(bmiCategory.labelRes),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = bmiCategory.color(),
            )
            Text(
                text = stringResource(R.string.overview_bmi_explain),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }

        // Height is known but there is no weight yet: the weight card already asks for an entry.
        else -> Unit
    }
}
