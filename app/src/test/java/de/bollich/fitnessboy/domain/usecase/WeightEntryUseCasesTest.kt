package de.bollich.fitnessboy.domain.usecase

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

class WeightEntryUseCasesTest {
    private val repository = FakeWeightRepository()
    private val addWeightEntry = AddWeightEntry(repository)
    private val deleteWeightEntry = DeleteWeightEntry(repository)
    private val date = LocalDate.of(2026, 3, 9)

    @Test
    fun addWeightEntry_savesParsedWeight() = runTest {
        val result = addWeightEntry(weightInput = "82,4", selectedDate = date)

        assertEquals(AddWeightEntry.Result.Success, result)
        val entry = repository.entries.value.single()
        assertEquals(82.4, entry.weightInKg, 0.0)
        assertEquals(date, entry.date)
    }

    @Test
    fun addWeightEntry_rejectsInvalidWeight() = runTest {
        val result = addWeightEntry(weightInput = "abc", selectedDate = date)

        assertTrue(result is AddWeightEntry.Result.InvalidWeight)
        assertTrue(repository.entries.value.isEmpty())
    }

    @Test
    fun deleteWeightEntry_removesOnlyThatEntry() = runTest {
        // Identical date and weight: only the id tells them apart.
        addWeightEntry(weightInput = "80", selectedDate = date)
        addWeightEntry(weightInput = "80", selectedDate = date)
        val (first, second) = repository.entries.value

        deleteWeightEntry(first)

        assertEquals(listOf(second), repository.entries.value)
    }
}
