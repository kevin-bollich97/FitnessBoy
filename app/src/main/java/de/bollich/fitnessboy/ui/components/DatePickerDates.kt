package de.bollich.fitnessboy.ui.components

import java.time.LocalDate
import java.time.ZoneOffset

internal fun LocalDate.toUtcStartOfDayMillis(): Long =
    atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()

internal fun localDateFromUtcMillis(millis: Long): LocalDate =
    java.time.Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
