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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.bollich.fitnessboy.R
import de.bollich.fitnessboy.format.formatNumber
import de.bollich.fitnessboy.ui.components.formatCm
import de.bollich.fitnessboy.ui.components.formatKg

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
                text = stringResource(R.string.profile_title),
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
                label = { Text(stringResource(R.string.profile_height_label)) },
                placeholder = { Text(stringResource(R.string.profile_height_placeholder)) },
                supportingText = {
                    when {
                        heightErrorText != null -> Text(heightErrorText)
                        bmi != null -> Text(stringResource(R.string.profile_current_bmi, formatNumber(bmi)))
                    }
                },
                isError = heightErrorText != null,
            )
            OutlinedTextField(
                value = targetWeightValue,
                onValueChange = onTargetWeightValueChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                label = { Text(stringResource(R.string.profile_target_weight_label)) },
                placeholder = { Text(stringResource(R.string.profile_target_weight_placeholder)) },
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
                Text(stringResource(R.string.profile_save_action))
            }
        }
    }
}

@Composable
private fun buildProfileSummary(
    savedHeightInCm: Double?,
    savedTargetWeightInKg: Double?,
): String {
    val parts = listOfNotNull(
        savedHeightInCm?.let { stringResource(R.string.profile_summary_height, formatCm(it)) },
        savedTargetWeightInKg?.let { stringResource(R.string.profile_summary_target, formatKg(it)) },
    )
    return parts.joinToString(separator = "  |  ")
        .ifEmpty { stringResource(R.string.profile_summary_empty) }
}
