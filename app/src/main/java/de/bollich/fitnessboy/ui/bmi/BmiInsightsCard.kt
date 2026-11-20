package de.bollich.fitnessboy.ui.bmi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.bollich.fitnessboy.R
import de.bollich.fitnessboy.domain.BmiCategory
import de.bollich.fitnessboy.ui.components.MetricPill

@Composable
fun BmiInsightsCard(
    bmi: String?,
    bmiCategory: BmiCategory?,
    heightText: String?,
    currentWeightText: String?,
) {
    ElevatedCard(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.bmi_insights_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MetricPill(
                    label = stringResource(R.string.metric_bmi),
                    value = bmi ?: stringResource(R.string.value_missing),
                )
                MetricPill(
                    label = stringResource(R.string.bmi_insights_height),
                    value = heightText ?: stringResource(R.string.value_missing),
                )
                MetricPill(
                    label = stringResource(R.string.bmi_insights_weight),
                    value = currentWeightText ?: stringResource(R.string.value_missing),
                )
            }
            if (bmiCategory != null) {
                val (containerColor, contentColor) = bmiCategoryColors(bmiCategory)
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = containerColor,
                ) {
                    Text(
                        text = stringResource(R.string.bmi_category_label, stringResource(bmiCategory.labelRes)),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = contentColor,
                    )
                }
            }
        }
    }
}

@Composable
private fun bmiCategoryColors(category: BmiCategory): Pair<Color, Color> {
    val colorScheme = MaterialTheme.colorScheme
    return when (category) {
        BmiCategory.NORMAL_WEIGHT -> colorScheme.primary.copy(alpha = 0.16f) to colorScheme.primary
        BmiCategory.UNDERWEIGHT -> colorScheme.tertiary.copy(alpha = 0.18f) to colorScheme.tertiary
        BmiCategory.OVERWEIGHT -> colorScheme.secondary.copy(alpha = 0.18f) to colorScheme.secondary
        BmiCategory.OBESITY_CLASS_I,
        BmiCategory.OBESITY_CLASS_II,
        BmiCategory.OBESITY_CLASS_III -> colorScheme.error.copy(alpha = 0.16f) to colorScheme.error
    }
}
