package de.bollich.fitnessboy.ui.components.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.abs

data class ChartPoint(
    val date: LocalDate,
    val value: Double,
)

private val sameYearDateFormatter = DateTimeFormatter.ofPattern("dd.MM.")
private val multiYearDateFormatter = DateTimeFormatter.ofPattern("dd.MM.yy")

/**
 * Line chart over time with a value axis (round ticks and grid lines) and a date axis.
 * Tapping selects the nearest point.
 *
 * @param points sorted by date, oldest first; [selectedIndex] refers to this list.
 * @param formatValue formats axis values, e.g. "120".
 * @param unit shown above the value axis, e.g. "kg".
 * @param referenceValue optional dashed horizontal line, e.g. the target weight.
 * @param description spoken by screen readers instead of the drawing.
 */
@Composable
fun LineChart(
    points: List<ChartPoint>,
    selectedIndex: Int?,
    onSelect: (Int) -> Unit,
    formatValue: (Double) -> String,
    unit: String,
    description: String,
    modifier: Modifier = Modifier,
    referenceValue: Double? = null,
    height: Dp = 240.dp,
) {
    if (points.isEmpty()) return

    val colors = ChartColors(
        line = MaterialTheme.colorScheme.primary,
        point = MaterialTheme.colorScheme.primary,
        grid = MaterialTheme.colorScheme.outlineVariant,
        label = MaterialTheme.colorScheme.onSurfaceVariant,
        reference = MaterialTheme.colorScheme.onSurfaceVariant,
        selection = MaterialTheme.colorScheme.secondary,
        surface = MaterialTheme.colorScheme.surface,
    )
    val labelStyle = MaterialTheme.typography.labelMedium.copy(color = colors.label)
    val textMeasurer = rememberTextMeasurer()
    val valueAxis = remember(points, referenceValue) {
        valueAxisFor(points.map(ChartPoint::value) + listOfNotNull(referenceValue))
    }
    val dateAxis = remember(points) { dateAxisFor(points.map(ChartPoint::date)) }
    val dateFormatter = if (dateAxis.start.year == dateAxis.end.year) sameYearDateFormatter else multiYearDateFormatter

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .semantics { contentDescription = description }
            .pointerInput(points) {
                detectTapGestures { tap ->
                    val plot = plotArea(size.toSize(), textMeasurer, labelStyle, valueAxis, formatValue, unit)
                    onSelect(nearestIndex(points, dateAxis, plot, tap.x))
                }
            },
    ) {
        val plot = plotArea(size, textMeasurer, labelStyle, valueAxis, formatValue, unit)
        fun offsetOf(point: ChartPoint) = Offset(
            x = plot.left + plot.width * dateAxis.fractionOf(point.date),
            y = plot.bottom - plot.height * valueAxis.fractionOf(point.value),
        )

        drawValueAxis(textMeasurer, labelStyle, valueAxis, formatValue, unit, plot, colors)
        drawDateAxis(textMeasurer, labelStyle, dateAxis, dateFormatter, plot)
        referenceValue?.let { drawReferenceLine(plot.bottom - plot.height * valueAxis.fractionOf(it), plot, colors) }

        val offsets = points.map(::offsetOf)
        drawSeries(offsets, plot, colors)
        selectedIndex?.let { offsets.getOrNull(it) }?.let { drawSelection(it, plot, colors) }
    }
}

private data class ChartColors(
    val line: Color,
    val point: Color,
    val grid: Color,
    val label: Color,
    val reference: Color,
    val selection: Color,
    val surface: Color,
)

/** Area for the data, leaving room for the value labels (left), the unit (top) and dates (bottom). */
private fun Density.plotArea(
    size: Size,
    textMeasurer: TextMeasurer,
    labelStyle: TextStyle,
    valueAxis: ValueAxis,
    formatValue: (Double) -> String,
    unit: String,
): Rect {
    val labelWidth = (valueAxis.ticks.map(formatValue) + unit)
        .maxOf { textMeasurer.measure(it, labelStyle).size.width }
    val labelHeight = textMeasurer.measure(unit, labelStyle).size.height
    val selectionRadius = 10.dp.toPx()
    return Rect(
        left = labelWidth + 8.dp.toPx(),
        top = labelHeight + 8.dp.toPx(),
        right = size.width - selectionRadius,
        bottom = size.height - labelHeight - 8.dp.toPx(),
    )
}

private fun DrawScope.drawValueAxis(
    textMeasurer: TextMeasurer,
    labelStyle: TextStyle,
    valueAxis: ValueAxis,
    formatValue: (Double) -> String,
    unit: String,
    plot: Rect,
    colors: ChartColors,
) {
    drawText(textMeasurer.measure(unit, labelStyle), topLeft = Offset(0f, 0f))
    valueAxis.ticks.forEach { tick ->
        val y = plot.bottom - plot.height * valueAxis.fractionOf(tick)
        drawLine(colors.grid, Offset(plot.left, y), Offset(plot.right, y), strokeWidth = 1.dp.toPx())
        val label = textMeasurer.measure(formatValue(tick), labelStyle)
        drawText(label, topLeft = Offset(plot.left - 8.dp.toPx() - label.size.width, y - label.size.height / 2f))
    }
}

private fun DrawScope.drawDateAxis(
    textMeasurer: TextMeasurer,
    labelStyle: TextStyle,
    dateAxis: DateAxis,
    dateFormatter: DateTimeFormatter,
    plot: Rect,
) {
    val sampleWidth = textMeasurer.measure(dateAxis.end.format(dateFormatter), labelStyle).size.width
    val maxLabels = (plot.width / (sampleWidth * 1.6f)).toInt().coerceIn(2, 5)
    dateAxis.ticks(maxLabels).forEach { date ->
        val label: TextLayoutResult = textMeasurer.measure(date.format(dateFormatter), labelStyle)
        val centerX = plot.left + plot.width * dateAxis.fractionOf(date)
        val x = (centerX - label.size.width / 2f).coerceIn(plot.left - label.size.width / 2f, size.width - label.size.width)
        drawText(label, topLeft = Offset(x, plot.bottom + 8.dp.toPx()))
    }
}

private fun DrawScope.drawReferenceLine(y: Float, plot: Rect, colors: ChartColors) {
    drawLine(
        color = colors.reference,
        start = Offset(plot.left, y),
        end = Offset(plot.right, y),
        strokeWidth = 2.dp.toPx(),
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8.dp.toPx(), 6.dp.toPx())),
    )
}

private fun DrawScope.drawSeries(offsets: List<Offset>, plot: Rect, colors: ChartColors) {
    if (offsets.size > 1) {
        val line = Path().apply {
            moveTo(offsets.first().x, offsets.first().y)
            offsets.drop(1).forEach { lineTo(it.x, it.y) }
        }
        val area = Path().apply {
            addPath(line)
            lineTo(offsets.last().x, plot.bottom)
            lineTo(offsets.first().x, plot.bottom)
            close()
        }
        drawPath(area, Brush.verticalGradient(listOf(colors.line.copy(alpha = 0.18f), Color.Transparent), plot.top, plot.bottom))
        drawPath(line, colors.line, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
    }
    offsets.forEach { drawCircle(colors.point, radius = 3.5.dp.toPx(), center = it) }
}

private fun DrawScope.drawSelection(offset: Offset, plot: Rect, colors: ChartColors) {
    drawLine(colors.selection, Offset(offset.x, plot.top), Offset(offset.x, plot.bottom), strokeWidth = 1.dp.toPx())
    drawCircle(colors.surface, radius = 7.dp.toPx(), center = offset)
    drawCircle(colors.selection, radius = 7.dp.toPx(), center = offset, style = Stroke(width = 3.dp.toPx()))
}

private fun nearestIndex(points: List<ChartPoint>, dateAxis: DateAxis, plot: Rect, x: Float): Int =
    points.indices.minBy { abs(plot.left + plot.width * dateAxis.fractionOf(points[it].date) - x) }
