package de.bollich.fitnessboy.domain.usecase

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class BodyMeasurementsUseCasesTest {
    private val repository = FakeBodyMeasurementsRepository()
    private val addBodyMeasurementsEntry = AddBodyMeasurementsEntry(repository)
    private val deleteBodyMeasurementsEntry = DeleteBodyMeasurementsEntry(repository)
    private val date = LocalDate.of(2026, 3, 27)

    @Test
    fun addBodyMeasurementsEntry_savesValidEntry() = runTest {
        val result = addBodyMeasurementsEntry(
            date = date,
            waistInput = "88",
            hipsInput = "96",
            shouldersInput = "48",
        )

        assertEquals(AddBodyMeasurementsEntry.Result.Success, result)
        val entry = repository.entries.value.single()
        assertEquals(88.0, entry.waistInCm, 0.0)
        assertEquals(96.0, entry.hipsInCm, 0.0)
        assertEquals(48.0, entry.shouldersInCm, 0.0)
    }

    @Test
    fun addBodyMeasurementsEntry_rejectsInvalidWaist() = runTest {
        val result = addBodyMeasurementsEntry(date, waistInput = "abc", hipsInput = "96", shouldersInput = "48")

        assertTrue(result is AddBodyMeasurementsEntry.Result.InvalidWaist)
        assertTrue(repository.entries.value.isEmpty())
    }

    @Test
    fun addBodyMeasurementsEntry_rejectsInvalidHips() = runTest {
        val result = addBodyMeasurementsEntry(date, waistInput = "88", hipsInput = "", shouldersInput = "48")

        assertTrue(result is AddBodyMeasurementsEntry.Result.InvalidHips)
        assertTrue(repository.entries.value.isEmpty())
    }

    @Test
    fun addBodyMeasurementsEntry_rejectsInvalidShoulders() = runTest {
        val result = addBodyMeasurementsEntry(date, waistInput = "88", hipsInput = "96", shouldersInput = "999")

        assertTrue(result is AddBodyMeasurementsEntry.Result.InvalidShoulders)
        assertTrue(repository.entries.value.isEmpty())
    }

    @Test
    fun deleteBodyMeasurementsEntry_removesEntry() = runTest {
        addBodyMeasurementsEntry(date, waistInput = "88", hipsInput = "96", shouldersInput = "48")

        deleteBodyMeasurementsEntry(repository.entries.value.single())

        assertTrue(repository.entries.value.isEmpty())
    }
}
