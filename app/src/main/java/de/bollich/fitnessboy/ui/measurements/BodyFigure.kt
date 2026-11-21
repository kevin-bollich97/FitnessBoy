package de.bollich.fitnessboy.ui.measurements

import androidx.compose.foundation.Canvas
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import de.bollich.fitnessboy.domain.BodyMeasurement

private val RingColor = Color(0xFFD98F4E)

/** Where each measurement is taken, as fractions of the figure: center and size of the ring. */
private data class RingPosition(val centerX: Float, val centerY: Float, val width: Float, val height: Float)

private val BodyMeasurement.ringPosition: RingPosition
    get() = when (this) {
        BodyMeasurement.SHOULDERS -> RingPosition(0.50f, 0.27f, 0.54f, 0.06f)
        BodyMeasurement.CHEST -> RingPosition(0.50f, 0.35f, 0.42f, 0.06f)
        BodyMeasurement.WAIST -> RingPosition(0.50f, 0.45f, 0.40f, 0.06f)
        BodyMeasurement.HIPS -> RingPosition(0.50f, 0.55f, 0.48f, 0.06f)
        BodyMeasurement.UPPER_ARM -> RingPosition(0.205f, 0.41f, 0.14f, 0.05f)
        BodyMeasurement.THIGH -> RingPosition(0.395f, 0.69f, 0.16f, 0.05f)
    }

/** Simple body silhouette with a ring at every measuring point; [highlighted] stands out. */
@Composable
fun BodyFigure(
    highlighted: BodyMeasurement,
    modifier: Modifier = Modifier,
) {
    val bodyColor = MaterialTheme.colorScheme.primary
    Canvas(modifier = modifier) {
        drawSilhouette(bodyColor)
        BodyMeasurement.entries.forEach { measurement ->
            drawRing(measurement.ringPosition, isHighlighted = measurement == highlighted)
        }
    }
}

private fun DrawScope.drawSilhouette(bodyColor: Color) {
    fun at(x: Float, y: Float) = Offset(size.width * x, size.height * y)
    val limbWidth = 0.09f * size.width

    drawCircle(bodyColor, radius = size.width * 0.11f, center = at(0.5f, 0.14f))
    drawRoundRect(
        color = bodyColor,
        topLeft = at(0.33f, 0.24f),
        size = Size(size.width * 0.34f, size.height * 0.34f),
        cornerRadius = CornerRadius(size.width * 0.12f),
    )
    listOf(
        at(0.27f, 0.30f) to at(0.14f, 0.54f),
        at(0.73f, 0.30f) to at(0.86f, 0.54f),
    ).forEach { (start, end) -> drawLine(bodyColor, start, end, strokeWidth = limbWidth, cap = StrokeCap.Round) }
    listOf(
        at(0.42f, 0.58f) to at(0.34f, 0.92f),
        at(0.58f, 0.58f) to at(0.66f, 0.92f),
    ).forEach { (start, end) -> drawLine(bodyColor, start, end, strokeWidth = limbWidth * 1.2f, cap = StrokeCap.Round) }
}

private fun DrawScope.drawRing(position: RingPosition, isHighlighted: Boolean) {
    val ringSize = Size(size.width * position.width, size.height * position.height)
    drawRoundRect(
        color = if (isHighlighted) RingColor else RingColor.copy(alpha = 0.35f),
        topLeft = Offset(size.width * position.centerX - ringSize.width / 2f, size.height * position.centerY - ringSize.height / 2f),
        size = ringSize,
        cornerRadius = CornerRadius(ringSize.height / 2f),
        style = Stroke(width = if (isHighlighted) 3.dp.toPx() else 1.5.dp.toPx()),
    )
}
