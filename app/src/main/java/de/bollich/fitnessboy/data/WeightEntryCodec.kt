package de.bollich.fitnessboy.data

import de.bollich.fitnessboy.model.WeightEntry
import java.time.LocalDate

object WeightEntryCodec {
    private const val entrySeparator = "\n"
    private const val fieldSeparator = "|"

    fun encode(entries: List<WeightEntry>): String =
        entries.joinToString(entrySeparator) { entry ->
            "${entry.date}$fieldSeparator${entry.weightInKg}"
        }

    fun decode(raw: String): List<WeightEntry> =
        raw.lineSequence()
            .mapNotNull(::decodeLine)
            .sortedByDescending(WeightEntry::date)
            .toList()

    private fun decodeLine(line: String): WeightEntry? {
        val parts = line.split(fieldSeparator)
        if (parts.size != 2) {
            return null
        }

        val date = runCatching { LocalDate.parse(parts[0]) }.getOrNull() ?: return null
        val weight = parts[1].toDoubleOrNull() ?: return null
        return WeightEntry(date = date, weightInKg = weight)
    }
}
