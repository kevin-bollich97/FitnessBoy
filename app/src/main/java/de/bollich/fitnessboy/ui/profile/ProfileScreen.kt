package de.bollich.fitnessboy.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.bollich.fitnessboy.R
import de.bollich.fitnessboy.model.UserProfile
import de.bollich.fitnessboy.ui.components.ScreenContentPadding
import de.bollich.fitnessboy.ui.theme.FitnessBoyTheme

@Composable
fun ProfileRoute(viewModel: ProfileViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    if (uiState.isLoading) return

    ProfileScreen(
        uiState = uiState,
        onHeightValueChange = viewModel::onHeightValueChange,
        onTargetWeightValueChange = viewModel::onTargetWeightValueChange,
        onSaveProfileClick = viewModel::onSaveClick,
    )
}

@Composable
private fun ProfileScreen(
    uiState: ProfileUiState,
    onHeightValueChange: (String) -> Unit,
    onTargetWeightValueChange: (String) -> Unit,
    onSaveProfileClick: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = ScreenContentPadding,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = stringResource(R.string.profile_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
        }
        item {
            ProfileCard(
                heightValue = uiState.form.heightInput,
                targetWeightValue = uiState.form.targetWeightInput,
                heightErrorText = uiState.form.heightErrorRes?.let { stringResource(it) },
                targetWeightErrorText = uiState.form.targetWeightErrorRes?.let { stringResource(it) },
                canSave = uiState.hasUnsavedChanges,
                isSaved = uiState.form.isSaved,
                onHeightValueChange = onHeightValueChange,
                onTargetWeightValueChange = onTargetWeightValueChange,
                onSaveClick = onSaveProfileClick,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileScreenPreview() {
    FitnessBoyTheme {
        ProfileScreen(
            uiState = ProfileUiState(
                isLoading = false,
                profile = UserProfile(heightInCm = 180.0, targetWeightInKg = 78.0),
                form = ProfileFormState(heightInput = "180", targetWeightInput = "76"),
                hasUnsavedChanges = true,
            ),
            onHeightValueChange = {},
            onTargetWeightValueChange = {},
            onSaveProfileClick = {},
        )
    }
}
