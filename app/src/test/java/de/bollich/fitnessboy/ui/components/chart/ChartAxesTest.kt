package de.bollich.fitnessboy.ui.components.chart

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class ChartAxesTest {
    @Test
    fun valueAxis_usesRoundStepsAroundValues() {
        val axis = valueAxisFor(listOf(125.0, 132.0, 128.0, 100.0))

        assertEquals(100.0, axis.min, 0.0)
        assertEquals(140.0, axis.max, 0.0)
        assertEquals(10.0, axis.step, 0.0)
        assertEquals(listOf(100.0, 110.0, 120.0, 130.0, 140.0), axis.ticks)
    }

    @Test
    fun valueAxis_handlesSmallRangesWithDecimalSteps() {
        val axis = valueAxisFor(listOf(82.4, 82.9))

        assertEquals(0.2, axis.step, 1e-9)
        assertEquals(listOf(82.4, 82.6, 82.8, 83.0), axis.ticks)
    }

    @Test
    fun valueAxis_expandsSingleValue() {
        val axis = valueAxisFor(listOf(80.0))

        assertTrue(axis.min < 80.0)
        assertTrue(axis.max > 80.0)
        assertEquals(0.5f, axis.fractionOf(80.0), 0.0001f)
    }

    @Test
    fun valueAxis_fractionMapsMinToZeroAndMaxToOne() {
        val axis = valueAxisFor(listOf(100.0, 140.0))

        assertEquals(0f, axis.fractionOf(axis.min), 0f)
        assertEquals(1f, axis.fractionOf(axis.max), 0f)
    }

    @Test
    fun dateAxis_positionsDatesByDays() {
        val axis = dateAxisFor(listOf(LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 11), LocalDate.of(2026, 3, 3)))

        assertEquals(0f, axis.fractionOf(LocalDate.of(2026, 3, 1)), 0f)
        assertEquals(0.2f, axis.fractionOf(LocalDate.of(2026, 3, 3)), 0.0001f)
        assertEquals(1f, axis.fractionOf(LocalDate.of(2026, 3, 11)), 0f)
    }

    @Test
    fun dateAxis_ticksIncludeStartAndEndAndAreEvenlySpaced() {
        val axis = DateAxis(LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 31))

        assertEquals(
            listOf(LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 11), LocalDate.of(2026, 3, 21), LocalDate.of(2026, 3, 31)),
            axis.ticks(maxCount = 4),
        )
    }

    @Test
    fun dateAxis_shortRangeHasNoDuplicateTicks() {
        val axis = DateAxis(LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 2))

        assertEquals(listOf(LocalDate.of(2026, 3, 1), LocalDate.of(2026, 3, 2)), axis.ticks(maxCount = 4))
    }

    @Test
    fun dateAxis_singleDayHasOneTickInTheMiddle() {
        val day = LocalDate.of(2026, 3, 1)
        val axis = dateAxisFor(listOf(day, day))

        assertEquals(listOf(day), axis.ticks(maxCount = 4))
        assertEquals(0.5f, axis.fractionOf(day), 0f)
    }

    @Test
    fun reference_withinValues_isInside() {
        assertEquals(ReferencePosition.INSIDE, referencePositionFor(listOf(80.0, 90.0), 85.0))
    }

    @Test
    fun reference_closeToValues_isInside() {
        // Values 124..128 (span 4) and target 121: combined span 7, values still use more than half.
        assertEquals(ReferencePosition.INSIDE, referencePositionFor(listOf(124.0, 128.0), 121.0))
    }

    @Test
    fun reference_farFromValues_isBelowOrAbove() {
        assertEquals(ReferencePosition.BELOW, referencePositionFor(listOf(124.0, 128.0), 100.0))
        assertEquals(ReferencePosition.ABOVE, referencePositionFor(listOf(55.0, 57.0), 70.0))
    }

    @Test
    fun reference_nearSingleValue_isInside() {
        assertEquals(ReferencePosition.INSIDE, referencePositionFor(listOf(80.0), 78.0))
        assertEquals(ReferencePosition.BELOW, referencePositionFor(listOf(80.0), 70.0))
    }

    @Test
    fun valueAxis_coversAtLeastMinSpanAroundValues() {
        val axis = valueAxisFor(listOf(124.5, 126.0), minSpan = 4.0)

        assertTrue(axis.max - axis.min >= 4.0)
        assertTrue(axis.min <= 124.5 && axis.max >= 126.0)
        assertEquals(123.0, axis.min, 0.0)
        assertEquals(128.0, axis.max, 0.0)
    }
}
