package de.bollich.fitnessboy.data

import de.bollich.fitnessboy.model.WeightEntry
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class WeightEntryCodecTest {
    @Test
    fun encodeAndDecode_roundTripsEntries() {
        val entries = listOf(
            WeightEntry(date = LocalDate.of(2026, 3, 9), weightInKg = 82.4),
            WeightEntry(date = LocalDate.of(2026, 3, 8), weightInKg = 82.9),
        )

        val decoded = WeightEntryCodec.decode(WeightEntryCodec.encode(entries))

        assertEquals(entries, decoded)
    }
}
