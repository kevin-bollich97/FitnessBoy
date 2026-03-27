package de.bollich.fitnessboy.domain.repository

import de.bollich.fitnessboy.model.BodyMeasurementsEntry

interface BodyMeasurementsRepository {
    fun load(): List<BodyMeasurementsEntry>
    fun add(entry: BodyMeasurementsEntry): List<BodyMeasurementsEntry>
    fun delete(entry: BodyMeasurementsEntry): List<BodyMeasurementsEntry>
}
