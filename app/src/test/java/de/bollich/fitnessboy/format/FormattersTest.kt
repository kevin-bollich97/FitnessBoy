package de.bollich.fitnessboy.format

import org.junit.Assert.assertEquals
import org.junit.Test

class FormattersTest {
    @Test
    fun formatTrend_prefixesSign() {
        assertEquals("+1 kg", formatTrend(1.0))
        assertEquals("-2 kg", formatTrend(-2.0))
        assertEquals("0 kg", formatTrend(0.0))
    }
}
