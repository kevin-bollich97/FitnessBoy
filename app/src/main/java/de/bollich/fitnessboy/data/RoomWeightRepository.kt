package de.bollich.fitnessboy.data

import de.bollich.fitnessboy.data.local.WeightEntryDao
import de.bollich.fitnessboy.data.local.toEntity
import de.bollich.fitnessboy.data.local.toModel
import de.bollich.fitnessboy.domain.repository.WeightRepository
import de.bollich.fitnessboy.model.WeightEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext

class RoomWeightRepository(
    private val weightEntryDao: WeightEntryDao,
    private val legacyWeightStore: WeightStore,
) : WeightRepository {
    override fun observeEntries(): Flow<List<WeightEntry>> =
        weightEntryDao.observeAll()
            .onStart { migrateLegacyEntriesIfNeeded() }
            .map { entities -> entities.map { it.toModel() } }

    override suspend fun add(entry: WeightEntry) {
        weightEntryDao.insert(entry.toEntity())
    }

    override suspend fun delete(entry: WeightEntry) {
        weightEntryDao.delete(entry.toEntity())
    }

    private suspend fun migrateLegacyEntriesIfNeeded() {
        val legacyEntries = withContext(Dispatchers.IO) { legacyWeightStore.load() }
        if (legacyEntries.isEmpty()) return

        weightEntryDao.insertAllIfEmpty(legacyEntries.map { it.toEntity() })
        withContext(Dispatchers.IO) { legacyWeightStore.clear() }
    }
}
