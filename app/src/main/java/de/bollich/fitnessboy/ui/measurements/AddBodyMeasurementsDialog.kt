package de.bollich.fitnessboy.ui.measurements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.bollich.fitnessboy.R
import de.bollich.fitnessboy.format.formattedDate
import de.bollich.fitnessboy.ui.components.localDateFromUtcMillis
import de.bollich.fitnessboy.ui.components.toUtcStartOfDayMillis
import java.time.LocalDate
import java.util.Locale

@Composable
fun AddBodyMeasurementsDialog(
    selectedDate: LocalDate,
    waistInput: String,
    waistErrorText: String?,
    hipsInput: String,
    hipsErrorText: String?,
    shouldersInput: String,
    shouldersErrorText: String?,
    onDateChange: (LocalDate) -> Unit,
    onWaistChange: (String) -> Unit,
    onHipsChange: (String) -> Unit,
    onShouldersChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
) {
    var showDatePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onSave) {
                Text(stringResource(R.string.action_save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        },
        title = {
            Text(
                text = stringResource(R.string.measurements_add_action),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.measurements_date),
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
                            Text(stringResource(R.string.action_change_date))
                        }
                    }
                }
                OutlinedTextField(
                    value = waistInput,
                    onValueChange = onWaistChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text(stringResource(R.string.measurements_waist_label)) },
                    placeholder = { Text(stringResource(R.string.measurements_waist_placeholder)) },
                    supportingText = { waistErrorText?.let { Text(it) } },
                    isError = waistErrorText != null,
                )
                OutlinedTextField(
                    value = hipsInput,
                    onValueChange = onHipsChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text(stringResource(R.string.measurements_hips_label)) },
                    placeholder = { Text(stringResource(R.string.measurements_hips_placeholder)) },
                    supportingText = { hipsErrorText?.let { Text(it) } },
                    isError = hipsErrorText != null,
                )
                OutlinedTextField(
                    value = shouldersInput,
                    onValueChange = onShouldersChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text(stringResource(R.string.measurements_shoulders_label)) },
                    placeholder = { Text(stringResource(R.string.measurements_shoulders_placeholder)) },
                    supportingText = { shouldersErrorText?.let { Text(it) } },
                    isError = shouldersErrorText != null,
                )
            }
        },
    )

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
                    Text(stringResource(R.string.action_apply))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.action_cancel))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
