package de.bollich.fitnessboy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.bollich.fitnessboy.domain.WeightTrend
import de.bollich.fitnessboy.domain.formatTrend
import de.bollich.fitnessboy.format.formatNumber
import de.bollich.fitnessboy.format.formattedWeight
import de.bollich.fitnessboy.model.WeightEntry

@Composable
fun HeadlineSection(
    title: String,
    subtitle: String,
    latestEntry: WeightEntry?,
    trend: WeightTrend?,
    trendList: List<WeightTrend>,
    bmi: Double?,
) {
    ElevatedCard(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f),
                        )
                    )
                )
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (latestEntry != null) {
                    MetricPill(
                        label = "Letzter Wert",
                        value = latestEntry.formattedWeight(),
                    )
                }
                if (bmi != null) {
                    MetricPill(
                        label = "BMI",
                        value = formatNumber(bmi),
                    )
                }
            }
            if (trendList.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    trendList.forEach { item ->
                        MetricPill(
                            label = item.period.label,
                            value = formatTrend(item.deltaInKg),
                        )
                    }
                }
            }
        }
    }
}
