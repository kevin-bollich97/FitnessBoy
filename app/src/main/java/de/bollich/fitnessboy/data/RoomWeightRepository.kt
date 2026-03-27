package de.bollich.fitnessboy.data

import de.bollich.fitnessboy.data.local.WeightEntryDao
import de.bollich.fitnessboy.data.local.toEntity
import de.bollich.fitnessboy.data.local.toModel
import de.bollich.fitnessboy.domain.repository.WeightRepository
import de.bollich.fitnessboy.model.WeightEntry

class RoomWeightRepository(
    private val weightEntryDao: WeightEntryDao,
    private val legacyWeightStore: WeightStore,
) : WeightRepository {
    override fun load(): List<WeightEntry> {
        migrateLegacyEntriesIfNeeded()
        return weightEntryDao.getAll().map { it.toModel() }
    }

    override fun save(entries: List<WeightEntry>) {
        weightEntryDao.deleteAll()
        weightEntryDao.insertAll(entries.map { it.toEntity() })
    }

    private fun migrateLegacyEntriesIfNeeded() {
        if (weightEntryDao.count() > 0) {
            return
        }

        val legacyEntries = legacyWeightStore.load()
        if (legacyEntries.isEmpty()) {
            return
        }

        weightEntryDao.insertAll(legacyEntries.map { it.toEntity() })
        legacyWeightStore.clear()
    }
}
