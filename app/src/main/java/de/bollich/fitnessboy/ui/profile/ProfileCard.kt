package de.bollich.fitnessboy.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import de.bollich.fitnessboy.R

/** Height and target weight, each with a hint what it is used for. */
@Composable
internal fun ProfileCard(
    heightValue: String,
    targetWeightValue: String,
    heightErrorText: String?,
    targetWeightErrorText: String?,
    canSave: Boolean,
    isSaved: Boolean,
    onHeightValueChange: (String) -> Unit,
    onTargetWeightValueChange: (String) -> Unit,
    onSaveClick: () -> Unit,
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ProfileField(
                value = heightValue,
                onValueChange = onHeightValueChange,
                label = stringResource(R.string.profile_height_label),
                placeholder = stringResource(R.string.profile_height_placeholder),
                hint = stringResource(R.string.profile_height_hint),
                errorText = heightErrorText,
                imeAction = ImeAction.Next,
                onDone = {},
            )
            ProfileField(
                value = targetWeightValue,
                onValueChange = onTargetWeightValueChange,
                label = stringResource(R.string.profile_target_weight_label),
                placeholder = stringResource(R.string.profile_target_weight_placeholder),
                hint = stringResource(R.string.profile_target_weight_hint),
                errorText = targetWeightErrorText,
                imeAction = ImeAction.Done,
                onDone = { if (canSave) onSaveClick() },
            )
            Button(
                onClick = onSaveClick,
                enabled = canSave,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
            ) {
                Text(stringResource(R.string.profile_save_action))
            }
            if (isSaved) {
                Text(
                    text = stringResource(R.string.profile_saved),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
}

@Composable
private fun ProfileField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    hint: String,
    errorText: String?,
    imeAction: ImeAction,
    onDone: () -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
        label = { Text(label) },
        placeholder = { Text(placeholder) },
        supportingText = { Text(errorText ?: hint) },
        isError = errorText != null,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = imeAction),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
    )
}
