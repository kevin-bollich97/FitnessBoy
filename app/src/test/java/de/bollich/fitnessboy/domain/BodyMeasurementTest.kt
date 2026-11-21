package de.bollich.fitnessboy.domain

import de.bollich.fitnessboy.model.BodyMeasurementsEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class BodyMeasurementTest {
    private val entry = BodyMeasurementsEntry(
        date = LocalDate.of(2026, 3, 27),
        waistInCm = 88.0,
        hipsInCm = 96.0,
        shouldersInCm = 48.0,
        chestInCm = 105.0,
    )

    @Test
    fun valueOf_returnsTheMatchingField() {
        assertEquals(88.0, entry.valueOf(BodyMeasurement.WAIST)!!, 0.0)
        assertEquals(96.0, entry.valueOf(BodyMeasurement.HIPS)!!, 0.0)
        assertEquals(48.0, entry.valueOf(BodyMeasurement.SHOULDERS)!!, 0.0)
        assertEquals(105.0, entry.valueOf(BodyMeasurement.CHEST)!!, 0.0)
        assertNull(entry.valueOf(BodyMeasurement.UPPER_ARM))
        assertNull(entry.valueOf(BodyMeasurement.THIGH))
    }

    @Test
    fun measuredValues_skipsMissingValuesAndKeepsOrder() {
        assertEquals(
            listOf(BodyMeasurement.WAIST, BodyMeasurement.HIPS, BodyMeasurement.SHOULDERS, BodyMeasurement.CHEST),
            entry.measuredValues().keys.toList(),
        )
    }
}
