package de.bollich.fitnessboy.domain.repository

import de.bollich.fitnessboy.model.WeightEntry
import kotlinx.coroutines.flow.Flow

interface WeightRepository {
    /** Emits all entries, newest first. */
    fun observeEntries(): Flow<List<WeightEntry>>
    suspend fun add(entry: WeightEntry)
    suspend fun delete(entry: WeightEntry)
}
