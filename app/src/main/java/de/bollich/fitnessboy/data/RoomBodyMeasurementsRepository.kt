package de.bollich.fitnessboy.data

import de.bollich.fitnessboy.data.local.BodyMeasurementsDao
import de.bollich.fitnessboy.data.local.toEntity
import de.bollich.fitnessboy.data.local.toModel
import de.bollich.fitnessboy.domain.repository.BodyMeasurementsRepository
import de.bollich.fitnessboy.model.BodyMeasurementsEntry
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomBodyMeasurementsRepository(
    private val bodyMeasurementsDao: BodyMeasurementsDao,
) : BodyMeasurementsRepository {
    override fun observeEntries(): Flow<List<BodyMeasurementsEntry>> =
        bodyMeasurementsDao.observeAll()
            .map { entities -> entities.map { it.toModel() } }

    override suspend fun add(entry: BodyMeasurementsEntry) {
        bodyMeasurementsDao.insert(entry.toEntity())
    }

    override suspend fun delete(entry: BodyMeasurementsEntry) {
        bodyMeasurementsDao.delete(entry.toEntity())
    }
}
