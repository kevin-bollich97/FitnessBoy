package de.bollich.fitnessboy

import de.bollich.fitnessboy.data.WeightEntryCodec
import de.bollich.fitnessboy.domain.calculateBmi
import de.bollich.fitnessboy.domain.calculateHealthyWeightRange
import de.bollich.fitnessboy.domain.calculateOptimalWeight
import de.bollich.fitnessboy.domain.classifyBmi
import de.bollich.fitnessboy.domain.parseHeight
import de.bollich.fitnessboy.domain.parseOptionalWeight
import de.bollich.fitnessboy.domain.parseWeight
import de.bollich.fitnessboy.model.WeightEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class WeightEntryCodecTest {
    @Test
    fun encodeAndDecode_roundTripsEntries() {
        val entries = listOf(
            WeightEntry(LocalDate.of(2026, 3, 9), 82.4),
            WeightEntry(LocalDate.of(2026, 3, 8), 82.9),
        )

        val decoded = WeightEntryCodec.decode(WeightEntryCodec.encode(entries))

        assertEquals(entries, decoded)
    }

    @Test
    fun parseWeight_rejectsInvalidInput() {
        assertNull(parseWeight("abc"))
        assertNull(parseWeight("19.9"))
        assertNull(parseWeight("600"))
    }

    @Test
    fun parseWeight_acceptsGermanDecimalComma() {
        assertEquals(82.4, parseWeight("82,4"))
    }

    @Test
    fun parseOptionalWeight_allowsBlankInput() {
        assertNull(parseOptionalWeight(""))
        assertEquals(75.0, parseOptionalWeight("75"))
    }

    @Test
    fun parseHeight_acceptsValidInput() {
        assertEquals(180.0, parseHeight("180"))
        assertEquals(180.5, parseHeight("180,5"))
    }

    @Test
    fun parseHeight_rejectsInvalidInput() {
        assertNull(parseHeight("abc"))
        assertNull(parseHeight("79"))
        assertNull(parseHeight("300"))
    }

    @Test
    fun calculateBmi_returnsExpectedValue() {
        assertEquals(25.4, calculateBmi(82.4, 180.0) ?: error("BMI should not be null"), 0.1)
    }

    @Test
    fun calculateHealthyWeightRange_returnsExpectedValues() {
        val range = calculateHealthyWeightRange(180.0) ?: error("Range should not be null")

        assertEquals(59.9, range.minimumInKg, 0.1)
        assertEquals(80.7, range.maximumInKg, 0.1)
    }

    @Test
    fun calculateOptimalWeight_returnsExpectedValue() {
        assertEquals(71.3, calculateOptimalWeight(180.0) ?: error("Optimal weight should not be null"), 0.1)
    }

    @Test
    fun classifyBmi_returnsExpectedCategory() {
        assertEquals("Untergewicht", classifyBmi(17.0))
        assertEquals("Normalgewicht", classifyBmi(22.0))
        assertEquals("Übergewicht", classifyBmi(27.0))
    }
}
