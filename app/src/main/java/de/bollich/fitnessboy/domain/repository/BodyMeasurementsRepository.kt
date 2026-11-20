package de.bollich.fitnessboy.domain.repository

import de.bollich.fitnessboy.model.BodyMeasurementsEntry
import kotlinx.coroutines.flow.Flow

interface BodyMeasurementsRepository {
    /** Emits all entries, newest first. */
    fun observeEntries(): Flow<List<BodyMeasurementsEntry>>
    suspend fun add(entry: BodyMeasurementsEntry)
    suspend fun delete(entry: BodyMeasurementsEntry)
}
