package de.bollich.fitnessboy.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import de.bollich.fitnessboy.format.formatNumber
import de.bollich.fitnessboy.format.formattedDate
import de.bollich.fitnessboy.format.formattedShortDate
import de.bollich.fitnessboy.model.WeightEntry
import kotlin.math.max
import kotlin.math.min

@Composable
fun WeightChartCard(
    entries: List<WeightEntry>,
    targetWeightInKg: Double?,
) {
    val chartEntries = entries.sortedBy(WeightEntry::date)
    val weights = chartEntries.map(WeightEntry::weightInKg)
    val minWeight = min(weights.minOrNull() ?: return, targetWeightInKg ?: Double.POSITIVE_INFINITY)
    val maxWeight = max(weights.maxOrNull() ?: return, targetWeightInKg ?: Double.NEGATIVE_INFINITY)
    val weightRange = max(1.0, maxWeight - minWeight)

    ElevatedCard(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.24f),
                        )
                    )
                )
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Gewichts-Chart",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = if (chartEntries.size > 1) {
                            "${chartEntries.first().formattedDate()} bis ${chartEntries.last().formattedDate()}"
                        } else {
                            "Bisher nur ein Eintrag vorhanden"
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                MetricPill(
                    label = "Spanne",
                    value = "${formatNumber(weightRange)} kg",
                )
            }

            WeightChart(
                entries = chartEntries,
                minWeight = minWeight,
                maxWeight = maxWeight,
                targetWeightInKg = targetWeightInKg,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                ChartAxisLabel(text = chartEntries.first().formattedShortDate())
                if (chartEntries.size > 2) {
                    ChartAxisLabel(text = chartEntries[chartEntries.lastIndex / 2].formattedShortDate())
                }
                ChartAxisLabel(text = chartEntries.last().formattedShortDate(), alignEnd = true)
            }
        }
    }
}

@Composable
private fun WeightChart(
    entries: List<WeightEntry>,
    minWeight: Double,
    maxWeight: Double,
    targetWeightInKg: Double?,
    chartHeight: Dp = 220.dp,
) {
    val lineColor = MaterialTheme.colorScheme.primary
    val fillColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
    val axisColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f)
    val pointColor = MaterialTheme.colorScheme.secondary
    val targetColor = MaterialTheme.colorScheme.tertiary
    val range = max(1.0, maxWeight - minWeight)

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "${formatNumber(maxWeight)} kg",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = "${formatNumber(minWeight)} kg",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (targetWeightInKg != null) {
            Text(
                text = "Zielgewicht: ${formatNumber(targetWeightInKg)} kg",
                style = MaterialTheme.typography.labelMedium,
                color = targetColor,
            )
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(chartHeight)
                .background(
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.55f),
                    shape = RoundedCornerShape(24.dp),
                )
                .padding(horizontal = 8.dp, vertical = 6.dp)
        ) {
            val horizontalPadding = 24.dp.toPx()
            val verticalPadding = 20.dp.toPx()
            val usableWidth = (size.width - horizontalPadding * 2).coerceAtLeast(1f)
            val usableHeight = (size.height - verticalPadding * 2).coerceAtLeast(1f)

            fun xPosition(index: Int): Float {
                if (entries.size == 1) {
                    return size.width / 2f
                }

                val fraction = index.toFloat() / entries.lastIndex.coerceAtLeast(1)
                return horizontalPadding + (usableWidth * fraction)
            }

            fun yPosition(weight: Double): Float {
                val normalized = ((weight - minWeight) / range).toFloat()
                return size.height - verticalPadding - (usableHeight * normalized)
            }

            drawLine(
                color = axisColor,
                start = androidx.compose.ui.geometry.Offset(horizontalPadding, size.height - verticalPadding),
                end = androidx.compose.ui.geometry.Offset(size.width - horizontalPadding, size.height - verticalPadding),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round,
            )

            if (targetWeightInKg != null) {
                val targetY = yPosition(targetWeightInKg)
                drawLine(
                    color = targetColor,
                    start = androidx.compose.ui.geometry.Offset(horizontalPadding, targetY),
                    end = androidx.compose.ui.geometry.Offset(size.width - horizontalPadding, targetY),
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 14f)),
                )
            }

            val linePath = Path()
            val fillPath = Path()

            entries.forEachIndexed { index, entry ->
                val x = xPosition(index)
                val y = yPosition(entry.weightInKg)
                if (index == 0) {
                    linePath.moveTo(x, y)
                    fillPath.moveTo(x, size.height - verticalPadding)
                    fillPath.lineTo(x, y)
                } else {
                    linePath.lineTo(x, y)
                    fillPath.lineTo(x, y)
                }
            }

            if (entries.size > 1) {
                fillPath.lineTo(xPosition(entries.lastIndex), size.height - verticalPadding)
                fillPath.close()

                drawPath(path = fillPath, color = fillColor)
                drawPath(
                    path = linePath,
                    color = lineColor,
                    style = Stroke(
                        width = 4.dp.toPx(),
                        cap = StrokeCap.Round,
                    )
                )
            }

            entries.forEachIndexed { index, entry ->
                val center = androidx.compose.ui.geometry.Offset(
                    x = xPosition(index),
                    y = yPosition(entry.weightInKg),
                )
                drawCircle(
                    color = pointColor,
                    radius = 6.dp.toPx(),
                    center = center,
                )
                drawCircle(
                    color = Color.White,
                    radius = 2.5.dp.toPx(),
                    center = center,
                )
            }
        }
    }
}

@Composable
private fun ChartAxisLabel(
    text: String,
    alignEnd: Boolean = false,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = if (alignEnd) TextAlign.End else TextAlign.Start,
    )
}
