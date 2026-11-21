package de.bollich.fitnessboy.domain.usecase

import de.bollich.fitnessboy.domain.BodyMeasurement
import de.bollich.fitnessboy.domain.BodyMeasurement.CHEST
import de.bollich.fitnessboy.domain.BodyMeasurement.HIPS
import de.bollich.fitnessboy.domain.BodyMeasurement.SHOULDERS
import de.bollich.fitnessboy.domain.BodyMeasurement.THIGH
import de.bollich.fitnessboy.domain.BodyMeasurement.UPPER_ARM
import de.bollich.fitnessboy.domain.BodyMeasurement.WAIST
import de.bollich.fitnessboy.testing.FakeBodyMeasurementsRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class AddBodyMeasurementsEntryTest {
    private val repository = FakeBodyMeasurementsRepository()
    private val addBodyMeasurementsEntry = AddBodyMeasurementsEntry(repository)
    private val date = LocalDate.of(2026, 3, 27)
    private val required = mapOf(WAIST to "88", HIPS to "96", SHOULDERS to "48")

    @Test
    fun requiredOnly_savesEntryWithoutOptionalValues() = runTest {
        val result = addBodyMeasurementsEntry(date, required)

        assertEquals(AddBodyMeasurementsEntry.Result.Success, result)
        val entry = repository.entries.value.single()
        assertEquals(date, entry.date)
        assertEquals(88.0, entry.waistInCm, 0.0)
        assertEquals(96.0, entry.hipsInCm, 0.0)
        assertEquals(48.0, entry.shouldersInCm, 0.0)
        assertNull(entry.upperArmInCm)
        assertNull(entry.chestInCm)
        assertNull(entry.thighInCm)
    }

    @Test
    fun optionalValues_areSavedWhenFilledIn() = runTest {
        addBodyMeasurementsEntry(date, required + mapOf(UPPER_ARM to "35,5", CHEST to "105", THIGH to " "))

        val entry = repository.entries.value.single()
        assertEquals(35.5, entry.upperArmInCm!!, 0.0)
        assertEquals(105.0, entry.chestInCm!!, 0.0)
        assertNull(entry.thighInCm)
    }

    @Test
    fun invalidOrMissingRequiredValues_areAllReportedWithoutSaving() = runTest {
        val result = addBodyMeasurementsEntry(date, mapOf(WAIST to "abc", SHOULDERS to "999"))

        assertEquals(AddBodyMeasurementsEntry.Result.Invalid(setOf(WAIST, HIPS, SHOULDERS)), result)
        assertTrue(repository.entries.value.isEmpty())
    }

    @Test
    fun invalidOptionalValue_isReportedWithoutSaving() = runTest {
        val result = addBodyMeasurementsEntry(date, required + mapOf(THIGH to "5"))

        assertEquals(AddBodyMeasurementsEntry.Result.Invalid(setOf(THIGH)), result)
        assertTrue(repository.entries.value.isEmpty())
    }

    @Test
    fun requiredFlags_matchTheProductDecision() {
        assertEquals(listOf(WAIST, HIPS, SHOULDERS), BodyMeasurement.entries.filter { it.isRequired })
    }
}
