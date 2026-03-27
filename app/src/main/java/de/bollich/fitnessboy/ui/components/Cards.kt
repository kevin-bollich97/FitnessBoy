package de.bollich.fitnessboy.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import de.bollich.fitnessboy.domain.formatTrend
import de.bollich.fitnessboy.domain.GoalDirection
import de.bollich.fitnessboy.domain.GoalProgress
import de.bollich.fitnessboy.domain.GoalStatus
import de.bollich.fitnessboy.domain.WeightTrend
import de.bollich.fitnessboy.format.formatNumber
import de.bollich.fitnessboy.format.formattedDate
import de.bollich.fitnessboy.format.formattedWeight
import de.bollich.fitnessboy.model.WeightEntry
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Locale
import kotlin.math.absoluteValue

@Composable
fun HeadlineSection(
    title: String,
    subtitle: String,
    latestEntry: WeightEntry?,
    trend: WeightTrend?,
    trendList: List<WeightTrend>,
    bmi: Double?,
) {
    ElevatedCard(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f),
                        )
                    )
                )
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Black,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (latestEntry != null) {
                    MetricPill(
                        label = "Letzter Wert",
                        value = latestEntry.formattedWeight(),
                    )
                }
                if (bmi != null) {
                    MetricPill(
                        label = "BMI",
                        value = formatNumber(bmi),
                    )
                }
            }
            if (trendList.isNotEmpty()) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    trendList.forEach { item ->
                        MetricPill(
                            label = item.period.label,
                            value = formatTrend(item.deltaInKg),
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileCard(
    heightValue: String,
    targetWeightValue: String,
    savedHeightInCm: Double?,
    savedTargetWeightInKg: Double?,
    heightErrorText: String?,
    targetWeightErrorText: String?,
    bmi: Double?,
    onHeightValueChange: (String) -> Unit,
    onTargetWeightValueChange: (String) -> Unit,
    onSaveClick: () -> Unit,
) {
    ElevatedCard(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Profil",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = buildProfileSummary(savedHeightInCm, savedTargetWeightInKg),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            OutlinedTextField(
                value = heightValue,
                onValueChange = onHeightValueChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Größe in cm") },
                placeholder = { Text("z.B. 180") },
                supportingText = {
                    when {
                        heightErrorText != null -> Text(heightErrorText)
                        bmi != null -> Text("Aktueller BMI: ${formatNumber(bmi)}")
                    }
                },
                isError = heightErrorText != null,
            )
            OutlinedTextField(
                value = targetWeightValue,
                onValueChange = onTargetWeightValueChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Zielgewicht in kg") },
                placeholder = { Text("z.B. 75") },
                supportingText = {
                    if (targetWeightErrorText != null) {
                        Text(targetWeightErrorText)
                    }
                },
                isError = targetWeightErrorText != null,
            )
            Button(
                onClick = onSaveClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
            ) {
                Text("Profil speichern")
            }
        }
    }
}

@Composable
fun AddWeightCard(
    value: String,
    selectedDate: LocalDate,
    errorText: String?,
    onValueChange: (String) -> Unit,
    onDateChange: (LocalDate) -> Unit,
    onAddClick: () -> Unit,
) {
    var showDatePicker by remember { mutableStateOf(false) }

    ElevatedCard(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Neuen Eintrag erfassen",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.55f),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(
                            text = "Ausgewähltes Datum",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = selectedDate.formattedDate(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    TextButton(onClick = { showDatePicker = true }) {
                        Text("Wählen")
                    }
                }
            }
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text("Gewicht in kg") },
                placeholder = { Text("z.B. 82.4") },
                supportingText = {
                    if (errorText != null) {
                        Text(errorText)
                    }
                },
                isError = errorText != null,
            )
            Button(
                onClick = onAddClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
            ) {
                Text("Eintrag speichern")
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = remember(selectedDate) {
            DatePickerState(
                locale = Locale.GERMANY,
                initialSelectedDateMillis = selectedDate.toUtcStartOfDayMillis(),
            )
        }
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis
                            ?.let(::localDateFromUtcMillis)
                            ?.let(onDateChange)
                        showDatePicker = false
                    }
                ) {
                    Text("Übernehmen")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Abbrechen")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun SummaryCard(entryCount: Int) {
    ElevatedCard(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .clip(RoundedCornerShape(26.dp))
                .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.45f))
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Dein Verlauf",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = if (entryCount == 1) "1 Eintrag gespeichert" else "$entryCount Einträge gespeichert",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = entryCount.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                )
            }
        }
    }
}

@Composable
fun EmptyStateCard() {
    ElevatedCard(shape = RoundedCornerShape(24.dp)) {
        Text(
            text = "Noch keine Gewichtsverläufe vorhanden. Lege oben deinen ersten Wert an.",
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun BmiInsightsCard(
    bmi: String?,
    bmiCategory: String?,
    heightText: String?,
    currentWeightText: String?,
) {
    ElevatedCard(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Deine Kennzahlen",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MetricPill(
                    label = "BMI",
                    value = bmi ?: "Offen",
                )
                MetricPill(
                    label = "Größe",
                    value = heightText ?: "Offen",
                )
                MetricPill(
                    label = "Gewicht",
                    value = currentWeightText ?: "Offen",
                )
            }
            if (bmiCategory != null) {
                val (containerColor, contentColor) = bmiCategoryColors(bmiCategory)
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = containerColor,
                ) {
                    Text(
                        text = "Einordnung: $bmiCategory",
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = contentColor,
                    )
                }
            }
        }
    }
}

@Composable
fun GoalProgressCard(goalProgress: GoalProgress) {
    ElevatedCard(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Zielverfolgung",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = goalStatusText(goalProgress),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MetricPill(
                    label = "Ziel",
                    value = "${formatNumber(goalProgress.targetWeightInKg)} kg",
                )
                MetricPill(
                    label = "Fortschritt",
                    value = "${goalProgress.progressPercent} %",
                )
                MetricPill(
                    label = "Verbleibend",
                    value = "${formatNumber(goalProgress.remainingInKg.absoluteValue)} kg",
                )
            }
            Text(
                text = "Start: ${formatNumber(goalProgress.startWeightInKg)} kg, aktuell: ${formatNumber(goalProgress.currentWeightInKg)} kg, geschafft: ${formatNumber(goalProgress.achievedInKg.absoluteValue)} kg.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun goalStatusText(goalProgress: GoalProgress): String =
    when (goalProgress.status) {
        GoalStatus.REACHED -> "Ziel erreicht. Du bist bei ${formatNumber(goalProgress.currentWeightInKg)} kg angekommen."
        GoalStatus.NOT_STARTED -> "Noch keine Zielverfolgung aktiv."
        GoalStatus.IN_PROGRESS -> when (goalProgress.direction) {
            GoalDirection.LOSE -> "Du arbeitest auf eine Gewichtsabnahme hin."
            GoalDirection.GAIN -> "Du arbeitest auf eine Gewichtszunahme hin."
            GoalDirection.MAINTAIN -> "Du hältst dein aktuelles Gewicht als Ziel."
        }
    }

@Composable
fun MetricInfoCard(
    title: String,
    value: String,
    description: String,
) {
    ElevatedCard(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
fun WeightHistoryRow(
    entry: WeightEntry,
    onDelete: () -> Unit,
) {
    ElevatedCard(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = entry.formattedWeight().substringBefore(" "),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = entry.formattedWeight(),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = entry.formattedDate(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            TextButton(onClick = onDelete) {
                Text("Löschen")
            }
        }
    }
}

@Composable
fun MetricPill(
    label: String,
    value: String,
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.border(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.45f),
            shape = RoundedCornerShape(20.dp),
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

private fun buildProfileSummary(
    savedHeightInCm: Double?,
    savedTargetWeightInKg: Double?,
): String {
    if (savedHeightInCm == null && savedTargetWeightInKg == null) {
        return "Lege deine Größe fest, damit später BMI und Ziele berechnet werden können."
    }

    return buildString {
        if (savedHeightInCm != null) {
            append("Größe: ${formatNumber(savedHeightInCm)} cm")
        }
        if (savedTargetWeightInKg != null) {
            if (isNotEmpty()) {
                append("  |  ")
            }
            append("Ziel: ${formatNumber(savedTargetWeightInKg)} kg")
        }
    }
}

@Composable
private fun bmiCategoryColors(category: String): Pair<Color, Color> {
    val colorScheme = MaterialTheme.colorScheme
    return when (category) {
        "Normalgewicht" -> colorScheme.primary.copy(alpha = 0.16f) to colorScheme.primary
        "Untergewicht" -> colorScheme.tertiary.copy(alpha = 0.18f) to colorScheme.tertiary
        "Übergewicht" -> colorScheme.secondary.copy(alpha = 0.18f) to colorScheme.secondary
        else -> colorScheme.error.copy(alpha = 0.16f) to colorScheme.error
    }
}

private fun LocalDate.toUtcStartOfDayMillis(): Long =
    atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()

private fun localDateFromUtcMillis(millis: Long): LocalDate =
    java.time.Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
