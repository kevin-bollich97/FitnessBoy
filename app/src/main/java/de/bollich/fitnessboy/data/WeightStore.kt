package de.bollich.fitnessboy.data

import android.content.Context
import de.bollich.fitnessboy.model.WeightEntry

class WeightStore(private val context: Context) {
    fun load(): List<WeightEntry> {
        val raw = preferences.getString(entriesKey, "").orEmpty()
        return WeightEntryCodec.decode(raw)
    }

    fun save(entries: List<WeightEntry>) {
        preferences
            .edit()
            .putString(entriesKey, WeightEntryCodec.encode(entries.sortedByDescending(WeightEntry::date)))
            .apply()
    }

    fun clear() {
        preferences
            .edit()
            .remove(entriesKey)
            .apply()
    }

    private val preferences
        get() = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)

    private companion object {
        const val preferencesName = "weight_tracker"
        const val entriesKey = "entries"
    }
}
