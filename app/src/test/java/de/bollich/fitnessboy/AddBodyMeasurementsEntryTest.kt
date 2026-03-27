package de.bollich.fitnessboy

import de.bollich.fitnessboy.domain.repository.BodyMeasurementsRepository
import de.bollich.fitnessboy.domain.usecase.AddBodyMeasurementsEntry
import de.bollich.fitnessboy.model.BodyMeasurementsEntry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class AddBodyMeasurementsEntryTest {
    @Test
    fun addBodyMeasurementsEntry_savesValidEntry() {
        val repository = FakeBodyMeasurementsRepository()
        val useCase = AddBodyMeasurementsEntry(repository)

        val result = useCase(
            date = LocalDate.of(2026, 3, 27),
            waistInput = "88",
            hipsInput = "96",
            shouldersInput = "48",
        )

        assertTrue(result is AddBodyMeasurementsEntry.Result.Success)
        assertEquals(1, repository.entries.size)
        assertEquals(88.0, repository.entries.single().waistInCm, 0.0)
    }

    @Test
    fun addBodyMeasurementsEntry_rejectsInvalidWaist() {
        val repository = FakeBodyMeasurementsRepository()
        val useCase = AddBodyMeasurementsEntry(repository)

        val result = useCase(
            date = LocalDate.of(2026, 3, 27),
            waistInput = "abc",
            hipsInput = "96",
            shouldersInput = "48",
        )

        assertTrue(result is AddBodyMeasurementsEntry.Result.InvalidWaist)
        assertTrue(repository.entries.isEmpty())
    }
}

private class FakeBodyMeasurementsRepository : BodyMeasurementsRepository {
    val entries = mutableListOf<BodyMeasurementsEntry>()

    override fun load(): List<BodyMeasurementsEntry> = entries.sortedByDescending { it.date }

    override fun add(entry: BodyMeasurementsEntry): List<BodyMeasurementsEntry> {
        entries.add(entry)
        return load()
    }

    override fun delete(entry: BodyMeasurementsEntry): List<BodyMeasurementsEntry> {
        entries.remove(entry)
        return load()
    }
}
