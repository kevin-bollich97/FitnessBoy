package de.bollich.fitnessboy.ui.measurements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.bollich.fitnessboy.R
import de.bollich.fitnessboy.domain.BODY_MEASUREMENT_MAX_CM
import de.bollich.fitnessboy.domain.BODY_MEASUREMENT_MIN_CM
import de.bollich.fitnessboy.domain.BodyMeasurement
import de.bollich.fitnessboy.format.formatNumber
import de.bollich.fitnessboy.ui.components.EntryDateRow
import java.time.LocalDate

/** Shows the add-measurements sheet while the form of [viewModel] asks for it. */
@Composable
fun AddBodyMeasurementsSheetHost(viewModel: BodyMeasurementsViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val form = uiState.form
    if (!form.isSheetVisible) return

    AddBodyMeasurementsSheet(
        selectedDate = form.selectedDate,
        inputs = form.inputs,
        invalidMeasurements = form.invalidMeasurements,
        onDateChange = viewModel::onDateChange,
        onValueChange = viewModel::onValueChange,
        onSave = viewModel::onSaveClick,
        onDismiss = { viewModel.onSheetVisibilityChange(false) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddBodyMeasurementsSheet(
    selectedDate: LocalDate,
    inputs: Map<BodyMeasurement, String>,
    invalidMeasurements: Set<BodyMeasurement>,
    onDateChange: (LocalDate) -> Unit,
    onValueChange: (BodyMeasurement, String) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit,
) {
    val (required, optional) = BodyMeasurement.entries.partition { it.isRequired }
    val first = BodyMeasurement.entries.first()
    val last = BodyMeasurement.entries.last()
    val focusRequester = remember { FocusRequester() }
    val errorText = stringResource(
        R.string.error_invalid_measurement,
        formatNumber(BODY_MEASUREMENT_MIN_CM),
        formatNumber(BODY_MEASUREMENT_MAX_CM),
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.measurements_add_action),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            EntryDateRow(selectedDate = selectedDate, onDateChange = onDateChange)
            required.forEach { measurement ->
                MeasurementField(
                    measurement, inputs, invalidMeasurements, errorText, measurement == last, onValueChange, onSave,
                    modifier = if (measurement == first) Modifier.focusRequester(focusRequester) else Modifier,
                )
            }
            Text(
                text = stringResource(R.string.measurements_optional_title),
                modifier = Modifier.padding(top = 8.dp),
                style = MaterialTheme.typography.titleSmall,
            )
            optional.forEach { measurement ->
                MeasurementField(measurement, inputs, invalidMeasurements, errorText, measurement == last, onValueChange, onSave)
            }
            Button(
                onClick = onSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            ) {
                Text(stringResource(R.string.action_save))
            }
        }
    }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }
}

@Composable
private fun MeasurementField(
    measurement: BodyMeasurement,
    inputs: Map<BodyMeasurement, String>,
    invalidMeasurements: Set<BodyMeasurement>,
    errorText: String,
    isLast: Boolean,
    onValueChange: (BodyMeasurement, String) -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isError = measurement in invalidMeasurements
    OutlinedTextField(
        value = inputs[measurement].orEmpty(),
        onValueChange = { onValueChange(measurement, it) },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        label = { Text(stringResource(measurement.inputLabelRes)) },
        supportingText = if (isError) ({ Text(errorText) }) else null,
        isError = isError,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = if (isLast) ImeAction.Done else ImeAction.Next,
        ),
        keyboardActions = KeyboardActions(onDone = { onSave() }),
    )
}
