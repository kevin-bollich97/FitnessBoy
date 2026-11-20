package de.bollich.fitnessboy.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.bollich.fitnessboy.format.formatNumber

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
