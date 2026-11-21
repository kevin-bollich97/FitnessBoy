package de.bollich.fitnessboy.domain

import de.bollich.fitnessboy.model.WeightEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import java.time.LocalDate

class BmiOnDateTest {
    private val entries = listOf(
        WeightEntry(id = 1, date = LocalDate.of(2026, 3, 1), weightInKg = 85.0),
        WeightEntry(id = 3, date = LocalDate.of(2026, 3, 20), weightInKg = 81.0),
        WeightEntry(id = 2, date = LocalDate.of(2026, 3, 10), weightInKg = 83.0),
        WeightEntry(id = 4, date = LocalDate.of(2026, 3, 20), weightInKg = 80.0),
    )

    @Test
    fun weightOn_usesEntryOfThatDay() {
        assertEquals(83.0, weightOn(LocalDate.of(2026, 3, 10), entries)!!.weightInKg, 0.0)
    }

    @Test
    fun weightOn_usesLatestEntryBeforeTheDay() {
        assertEquals(83.0, weightOn(LocalDate.of(2026, 3, 15), entries)!!.weightInKg, 0.0)
        assertEquals(80.0, weightOn(LocalDate.of(2026, 12, 31), entries)!!.weightInKg, 0.0)
    }

    @Test
    fun weightOn_prefersLastAddedEntryOnTheSameDay() {
        assertEquals(4L, weightOn(LocalDate.of(2026, 3, 20), entries)!!.id)
    }

    @Test
    fun weightOn_isNullBeforeTheFirstEntry() {
        assertNull(weightOn(LocalDate.of(2026, 2, 28), entries))
        assertNull(weightOn(LocalDate.of(2026, 3, 1), emptyList()))
    }

    @Test
    fun calculateBmiOn_combinesWeightOnDateWithHeight() {
        assertEquals(25.62, calculateBmiOn(LocalDate.of(2026, 3, 15), entries, heightInCm = 180.0)!!, 0.01)
        assertNull(calculateBmiOn(LocalDate.of(2026, 3, 15), entries, heightInCm = null))
        assertNull(calculateBmiOn(LocalDate.of(2026, 2, 1), entries, heightInCm = 180.0))
    }
}
