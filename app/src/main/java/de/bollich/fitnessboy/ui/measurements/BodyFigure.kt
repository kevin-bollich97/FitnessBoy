package de.bollich.fitnessboy.ui.measurements

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateRectAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.bollich.fitnessboy.domain.BodyMeasurement
import de.bollich.fitnessboy.ui.theme.FitnessBoyTheme
import kotlin.math.cos
import kotlin.math.sin

/**
 * Where each measurement is taken: the measuring tape around the body as an ellipse, in fractions
 * of the figure's width (x) and height (y). The flexed upper arm is horizontal, so its tape stands
 * upright.
 */
private val BodyMeasurement.tapeBounds: Rect
    get() = when (this) {
        BodyMeasurement.SHOULDERS -> tape(centerX = 0.50f, centerY = 0.41f, width = 0.46f)
        BodyMeasurement.CHEST -> tape(centerX = 0.50f, centerY = 0.47f, width = 0.42f)
        BodyMeasurement.WAIST -> tape(centerX = 0.50f, centerY = 0.54f, width = 0.40f)
        BodyMeasurement.HIPS -> tape(centerX = 0.50f, centerY = 0.61f, width = 0.42f)
        BodyMeasurement.UPPER_ARM -> tape(centerX = 0.24f, centerY = 0.43f, width = 0.07f, height = 0.08f)
        BodyMeasurement.THIGH -> tape(centerX = 0.42f, centerY = 0.73f, width = 0.19f)
    }

private fun tape(centerX: Float, centerY: Float, width: Float, height: Float = 0.04f) =
    Rect(centerX - width / 2, centerY - height / 2, centerX + width / 2, centerY + height / 2)

/**
 * A cheerful, slim little mascot in workout gear, flexing one arm. A measuring tape shows where
 * [highlighted] is taken and glides there when the measurement changes. The figure never changes
 * with the user's values.
 */
@Composable
fun BodyFigure(
    highlighted: BodyMeasurement,
    modifier: Modifier = Modifier,
) {
    val colorScheme = MaterialTheme.colorScheme
    val body = lerp(colorScheme.primaryContainer, colorScheme.primary, 0.3f)
    val colors = FigureColors(
        body = body,
        // A darker shade of the body, so the face stays visible in light and dark theme.
        face = lerp(body, Color.Black, 0.65f),
        accent = colorScheme.primary,
        cheeks = Cheeks,
    )
    val tapeBounds by animateRectAsState(
        targetValue = highlighted.tapeBounds,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMediumLow),
        label = "tapeBounds",
    )

    Canvas(modifier = modifier) {
        drawMascot(colors)
        drawTape(tapeBounds, colors.accent)
    }
}

private val Cheeks = Color(0x59FFB199)

private data class FigureColors(val body: Color, val face: Color, val accent: Color, val cheeks: Color)

/** Converts fractions of the figure into pixels. */
private fun DrawScope.at(x: Float, y: Float) = Offset(size.width * x, size.height * y)

private fun DrawScope.drawMascot(colors: FigureColors) {
    drawLegs(colors)
    drawArms(colors.body)
    drawTorso(colors.body)
    drawHead(colors)
}

/** Slim legs with little sneakers. */
private fun DrawScope.drawLegs(colors: FigureColors) {
    val leg = size.width * 0.12f
    listOf(0.42f, 0.58f).forEach { x ->
        drawLine(colors.body, at(x, 0.62f), at(x, 0.87f), strokeWidth = leg, cap = StrokeCap.Round)
        drawSneaker(center = at(x + (x - 0.5f) * 0.6f, 0.905f), colors.accent)
    }
}

private fun DrawScope.drawSneaker(center: Offset, accent: Color) {
    val shoe = Size(size.width * 0.20f, size.height * 0.06f)
    val topLeft = center - Offset(shoe.width / 2, shoe.height / 2)
    val corner = CornerRadius(shoe.height / 2)
    drawRoundRect(Color.White, topLeft, shoe, corner)
    drawRoundRect(accent, topLeft, shoe, corner, style = Stroke(width = 1.5.dp.toPx()))
    val sole = topLeft.y + shoe.height * 0.72f
    drawLine(accent, Offset(topLeft.x + shoe.height / 3, sole), Offset(topLeft.x + shoe.width - shoe.height / 3, sole), strokeWidth = 1.dp.toPx())
}

/** The left arm flexes like 💪, the right one hangs relaxed. */
private fun DrawScope.drawArms(color: Color) {
    val arm = size.width * 0.10f
    val elbow = at(0.14f, 0.44f)
    drawLine(color, at(0.36f, 0.42f), elbow, strokeWidth = arm, cap = StrokeCap.Round)
    drawLine(color, elbow, at(0.15f, 0.33f), strokeWidth = arm, cap = StrokeCap.Round)
    drawCircle(color, radius = arm * 0.7f, center = at(0.15f, 0.32f))
    drawLine(color, at(0.64f, 0.42f), at(0.80f, 0.58f), strokeWidth = arm, cap = StrokeCap.Round)
}

/** A slim body, a little wider at the shoulders. */
private fun DrawScope.drawTorso(color: Color) {
    val shoulders = CornerRadius(size.width * 0.14f)
    val hips = CornerRadius(size.width * 0.10f)
    val torso = Path().apply {
        addRoundRect(
            RoundRect(
                rect = Rect(at(0.32f, 0.37f), at(0.68f, 0.65f)),
                topLeft = shoulders,
                topRight = shoulders,
                bottomRight = hips,
                bottomLeft = hips,
            )
        )
    }
    drawPath(torso, color)
}

/** A big round head with a sweatband and a happy face. */
private fun DrawScope.drawHead(colors: FigureColors) {
    val center = at(0.5f, 0.20f)
    val radius = size.width * 0.27f
    drawCircle(colors.body, radius, center)
    drawSweatband(center, radius, colors.accent)
    drawEyes(colors.face)
    drawCircle(colors.cheeks, radius = size.width * 0.04f, center = at(0.31f, 0.245f))
    drawCircle(colors.cheeks, radius = size.width * 0.04f, center = at(0.69f, 0.245f))
    drawMouth(colors.face)
}

private fun DrawScope.drawSweatband(center: Offset, radius: Float, accent: Color) {
    val head = Path().apply { addOval(Rect(center, radius)) }
    clipPath(head) {
        val top = center.y - radius * 0.82f
        val height = radius * 0.28f
        drawRect(accent, Offset(center.x - radius, top), Size(radius * 2, height))
        drawLine(
            Color.White.copy(alpha = 0.7f),
            Offset(center.x - radius, top + height / 2),
            Offset(center.x + radius, top + height / 2),
            strokeWidth = 1.dp.toPx(),
        )
    }
}

/** Round dot eyes with a little highlight. */
private fun DrawScope.drawEyes(color: Color) {
    listOf(0.39f, 0.61f).forEach { x ->
        val eye = at(x, 0.205f)
        drawCircle(color, radius = size.width * 0.04f, center = eye)
        drawCircle(Color.White, radius = size.width * 0.013f, center = eye + Offset(size.width * 0.013f, -size.width * 0.013f))
    }
}

/** A soft, closed smile. */
private fun DrawScope.drawMouth(color: Color) {
    val width = size.width * 0.14f
    drawArc(
        color = color,
        startAngle = 20f,
        sweepAngle = 140f,
        useCenter = false,
        topLeft = at(0.5f, 0.235f) - Offset(width / 2, width / 4),
        size = Size(width, width / 2),
        style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round),
    )
}

/**
 * The measuring tape: the back half faint behind the body, the front half solid, with tick marks
 * like on a real measuring tape.
 */
private fun DrawScope.drawTape(bounds: Rect, color: Color) {
    val topLeft = at(bounds.left, bounds.top)
    val tapeSize = Size(size.width * bounds.width, size.height * bounds.height)
    drawArc(color.copy(alpha = 0.4f), 180f, 180f, false, topLeft, tapeSize, style = Stroke(width = 2.dp.toPx()))
    drawArc(color, 0f, 180f, false, topLeft, tapeSize, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
    if (tapeSize.width >= MIN_TICKED_TAPE_WIDTH.toPx()) drawTicks(topLeft, tapeSize, color)
}

/** Narrower tapes (around the arm) get no tick marks; they would clump together. */
private val MIN_TICKED_TAPE_WIDTH = 16.dp

private fun DrawScope.drawTicks(topLeft: Offset, tapeSize: Size, color: Color) {
    val center = topLeft + Offset(tapeSize.width / 2, tapeSize.height / 2)
    val tickLength = 3.dp.toPx()
    for (degrees in 30..150 step 20) {
        val radians = Math.toRadians(degrees.toDouble())
        val onTape = center + Offset(
            (tapeSize.width / 2 * cos(radians)).toFloat(),
            (tapeSize.height / 2 * sin(radians)).toFloat(),
        )
        drawLine(color, onTape, onTape + Offset(0f, tickLength), strokeWidth = 1.dp.toPx(), cap = StrokeCap.Round)
    }
}

@Preview(showBackground = true)
@Composable
private fun BodyFigurePreview() {
    FitnessBoyTheme {
        BodyFigure(highlighted = BodyMeasurement.WAIST, modifier = Modifier.size(width = 96.dp, height = 152.dp))
    }
}
