package de.bollich.fitnessboy.format

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class FormattersTest {
    @Test
    fun formatNumber_usesLocaleDecimalSeparator() {
        assertEquals("80,5", formatNumber(80.5, Locale.GERMANY))
        assertEquals("80.5", formatNumber(80.5, Locale.US))
    }

    @Test
    fun formatNumber_dropsTrailingZero() {
        assertEquals("80", formatNumber(80.0, Locale.GERMANY))
        assertEquals("80", formatNumber(80.04, Locale.GERMANY))
    }

    @Test
    fun formatNumber_roundsHalfUpToOneDecimal() {
        assertEquals("80,3", formatNumber(80.25, Locale.GERMANY))
        assertEquals("59,9", formatNumber(59.94, Locale.GERMANY))
    }

    @Test
    fun formatNumber_doesNotGroupThousands() {
        assertEquals("1234,5", formatNumber(1234.5, Locale.GERMANY))
    }

    @Test
    fun formatSignedNumber_prefixesSign() {
        assertEquals("+1", formatSignedNumber(1.0, Locale.GERMANY))
        assertEquals("-2,5", formatSignedNumber(-2.5, Locale.GERMANY))
        assertEquals("0", formatSignedNumber(0.0, Locale.GERMANY))
        assertEquals("0", formatSignedNumber(-0.04, Locale.GERMANY))
    }
}
