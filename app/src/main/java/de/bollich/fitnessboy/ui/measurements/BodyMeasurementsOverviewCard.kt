package de.bollich.fitnessboy.ui.measurements

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.bollich.fitnessboy.R
import de.bollich.fitnessboy.format.formattedDate
import de.bollich.fitnessboy.model.BodyMeasurementsEntry
import de.bollich.fitnessboy.ui.components.formatCm

@Composable
fun BodyMeasurementsOverviewCard(
    latestEntry: BodyMeasurementsEntry?,
) {
    ElevatedCard(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = stringResource(R.string.measurements_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = latestEntry?.let { stringResource(R.string.measurements_last_updated, it.date.formattedDate()) }
                            ?: stringResource(R.string.measurements_overview_empty_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .background(
                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.22f),
                        shape = RoundedCornerShape(22.dp),
                    )
                    .padding(16.dp),
            ) {
                BodySilhouette(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(width = 180.dp, height = 250.dp),
                )

                if (latestEntry != null) {
                    MeasurementBadge(
                        label = stringResource(R.string.measurements_shoulders),
                        value = formatCm(latestEntry.shouldersInCm),
                        modifier = Modifier.align(Alignment.TopCenter),
                    )
                    MeasurementBadge(
                        label = stringResource(R.string.measurements_waist),
                        value = formatCm(latestEntry.waistInCm),
                        modifier = Modifier.align(Alignment.CenterStart),
                    )
                    MeasurementBadge(
                        label = stringResource(R.string.measurements_hips),
                        value = formatCm(latestEntry.hipsInCm),
                        modifier = Modifier.align(Alignment.CenterEnd),
                    )
                } else {
                    Surface(
                        modifier = Modifier.align(Alignment.Center),
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                    ) {
                        Text(
                            text = stringResource(R.string.measurements_overview_empty),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MeasurementBadge(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
        shadowElevation = 4.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun BodySilhouette(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val bodyColor = Color(0xFF315A4A)
        val accentColor = Color(0xFFD98F4E)
        val centerX = size.width / 2f

        drawCircle(
            color = accentColor.copy(alpha = 0.9f),
            radius = size.width * 0.11f,
            center = Offset(centerX, size.height * 0.14f),
            style = Fill,
        )

        drawRoundRect(
            color = bodyColor.copy(alpha = 0.95f),
            topLeft = Offset(size.width * 0.33f, size.height * 0.24f),
            size = androidx.compose.ui.geometry.Size(size.width * 0.34f, size.height * 0.34f),
            cornerRadius = CornerRadius(44f, 44f),
        )
        drawLine(
            color = bodyColor,
            start = Offset(size.width * 0.27f, size.height * 0.30f),
            end = Offset(size.width * 0.14f, size.height * 0.54f),
            strokeWidth = 18f,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = bodyColor,
            start = Offset(size.width * 0.73f, size.height * 0.30f),
            end = Offset(size.width * 0.86f, size.height * 0.54f),
            strokeWidth = 18f,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = bodyColor,
            start = Offset(size.width * 0.42f, size.height * 0.58f),
            end = Offset(size.width * 0.34f, size.height * 0.92f),
            strokeWidth = 22f,
            cap = StrokeCap.Round,
        )
        drawLine(
            color = bodyColor,
            start = Offset(size.width * 0.58f, size.height * 0.58f),
            end = Offset(size.width * 0.66f, size.height * 0.92f),
            strokeWidth = 22f,
            cap = StrokeCap.Round,
        )
        drawRoundRect(
            color = accentColor.copy(alpha = 0.3f),
            topLeft = Offset(size.width * 0.24f, size.height * 0.28f),
            size = androidx.compose.ui.geometry.Size(size.width * 0.52f, size.height * 0.06f),
            cornerRadius = CornerRadius(40f, 40f),
            style = Stroke(width = 6f),
        )
        drawRoundRect(
            color = accentColor.copy(alpha = 0.3f),
            topLeft = Offset(size.width * 0.28f, size.height * 0.43f),
            size = androidx.compose.ui.geometry.Size(size.width * 0.44f, size.height * 0.06f),
            cornerRadius = CornerRadius(40f, 40f),
            style = Stroke(width = 6f),
        )
        drawRoundRect(
            color = accentColor.copy(alpha = 0.3f),
            topLeft = Offset(size.width * 0.25f, size.height * 0.54f),
            size = androidx.compose.ui.geometry.Size(size.width * 0.50f, size.height * 0.06f),
            cornerRadius = CornerRadius(40f, 40f),
            style = Stroke(width = 6f),
        )
    }
}
