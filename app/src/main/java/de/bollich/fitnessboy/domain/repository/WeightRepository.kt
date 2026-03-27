package de.bollich.fitnessboy.domain.repository

import de.bollich.fitnessboy.model.WeightEntry

interface WeightRepository {
    fun load(): List<WeightEntry>
    fun save(entries: List<WeightEntry>)
}
