package de.bollich.fitnessboy.data

import de.bollich.fitnessboy.data.local.BodyMeasurementsDao
import de.bollich.fitnessboy.data.local.toEntity
import de.bollich.fitnessboy.data.local.toModel
import de.bollich.fitnessboy.domain.repository.BodyMeasurementsRepository
import de.bollich.fitnessboy.model.BodyMeasurementsEntry

class RoomBodyMeasurementsRepository(
    private val bodyMeasurementsDao: BodyMeasurementsDao,
) : BodyMeasurementsRepository {
    override fun load(): List<BodyMeasurementsEntry> =
        bodyMeasurementsDao.getAll().map { it.toModel() }

    override fun add(entry: BodyMeasurementsEntry): List<BodyMeasurementsEntry> {
        bodyMeasurementsDao.insert(entry.toEntity())
        return load()
    }

    override fun delete(entry: BodyMeasurementsEntry): List<BodyMeasurementsEntry> {
        bodyMeasurementsDao.delete(entry.toEntity())
        return load()
    }
}
