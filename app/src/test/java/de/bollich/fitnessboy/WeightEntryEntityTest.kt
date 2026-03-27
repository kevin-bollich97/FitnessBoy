package de.bollich.fitnessboy

import de.bollich.fitnessboy.data.local.toEntity
import de.bollich.fitnessboy.data.local.toModel
import de.bollich.fitnessboy.model.WeightEntry
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class WeightEntryEntityTest {
    @Test
    fun weightEntryEntity_roundTripsModel() {
        val entry = WeightEntry(
            date = LocalDate.of(2026, 3, 9),
            weightInKg = 82.4,
        )

        val converted = entry.toEntity().toModel()

        assertEquals(entry, converted)
    }
}
