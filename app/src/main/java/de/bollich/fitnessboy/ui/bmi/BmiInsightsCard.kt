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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.bollich.fitnessboy.ui.components.MetricPill

@Composable
fun BmiInsightsCard(
    bmi: String?,
    bmiCategory: String?,
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
                text = "Deine Kennzahlen",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MetricPill(
                    label = "BMI",
                    value = bmi ?: "Offen",
                )
                MetricPill(
                    label = "Größe",
                    value = heightText ?: "Offen",
                )
                MetricPill(
                    label = "Gewicht",
                    value = currentWeightText ?: "Offen",
                )
            }
            if (bmiCategory != null) {
                val (containerColor, contentColor) = bmiCategoryColors(bmiCategory)
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = containerColor,
                ) {
                    Text(
                        text = "Einordnung: $bmiCategory",
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
private fun bmiCategoryColors(category: String): Pair<Color, Color> {
    val colorScheme = MaterialTheme.colorScheme
    return when (category) {
        "Normalgewicht" -> colorScheme.primary.copy(alpha = 0.16f) to colorScheme.primary
        "Untergewicht" -> colorScheme.tertiary.copy(alpha = 0.18f) to colorScheme.tertiary
        "Übergewicht" -> colorScheme.secondary.copy(alpha = 0.18f) to colorScheme.secondary
        else -> colorScheme.error.copy(alpha = 0.16f) to colorScheme.error
    }
}
