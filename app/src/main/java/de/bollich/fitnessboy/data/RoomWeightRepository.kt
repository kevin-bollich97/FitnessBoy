package de.bollich.fitnessboy.data

import de.bollich.fitnessboy.data.local.WeightEntryDao
import de.bollich.fitnessboy.data.local.toEntity
import de.bollich.fitnessboy.data.local.toModel
import de.bollich.fitnessboy.domain.repository.WeightRepository
import de.bollich.fitnessboy.model.WeightEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomWeightRepository(
    private val weightEntryDao: WeightEntryDao,
) : WeightRepository {
    override fun observeEntries(): Flow<List<WeightEntry>> =
        weightEntryDao.observeAll()
            .map { entities -> entities.map { it.toModel() } }

    override suspend fun add(entry: WeightEntry) {
        weightEntryDao.insert(entry.toEntity())
    }

    override suspend fun delete(entry: WeightEntry) {
        weightEntryDao.delete(entry.toEntity())
    }
}
