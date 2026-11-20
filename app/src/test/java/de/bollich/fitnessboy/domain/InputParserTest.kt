package de.bollich.fitnessboy.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class InputParserTest {
    @Test
    fun parseWeight_acceptsDotAndComma() {
        assertEquals(80.5, parseWeight("80.5")!!, 0.0)
        assertEquals(80.5, parseWeight("80,5")!!, 0.0)
    }

    @Test
    fun parseWeight_trimsWhitespace() {
        assertEquals(75.0, parseWeight("  75 ")!!, 0.0)
    }

    @Test
    fun parseWeight_acceptsBounds() {
        assertEquals(20.0, parseWeight("20")!!, 0.0)
        assertEquals(500.0, parseWeight("500")!!, 0.0)
    }

    @Test
    fun parseWeight_rejectsOutOfRange() {
        assertNull(parseWeight("19.9"))
        assertNull(parseWeight("500.1"))
        assertNull(parseWeight("-80"))
    }

    @Test
    fun parseWeight_rejectsInvalidInput() {
        assertNull(parseWeight(""))
        assertNull(parseWeight("abc"))
        assertNull(parseWeight("80kg"))
        assertNull(parseWeight("NaN"))
    }

    @Test
    fun parseOptionalWeight_returnsNullForBlank() {
        assertNull(parseOptionalWeight("  "))
        assertEquals(70.0, parseOptionalWeight("70")!!, 0.0)
        assertNull(parseOptionalWeight("10"))
    }

    @Test
    fun parseHeight_respectsBounds() {
        assertEquals(180.0, parseHeight("180")!!, 0.0)
        assertEquals(80.0, parseHeight("80")!!, 0.0)
        assertEquals(260.0, parseHeight("260")!!, 0.0)
        assertNull(parseHeight("79.9"))
        assertNull(parseHeight("260.1"))
    }

    @Test
    fun parseBodyMeasurement_respectsBounds() {
        assertEquals(88.5, parseBodyMeasurement("88,5")!!, 0.0)
        assertNull(parseBodyMeasurement("19.9"))
        assertNull(parseBodyMeasurement("250.1"))
    }
}
