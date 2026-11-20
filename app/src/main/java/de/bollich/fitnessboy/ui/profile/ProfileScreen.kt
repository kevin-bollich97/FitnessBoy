package de.bollich.fitnessboy.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.bollich.fitnessboy.format.formatNumber
import de.bollich.fitnessboy.model.UserProfile
import de.bollich.fitnessboy.ui.components.HeadlineMetric
import de.bollich.fitnessboy.ui.components.HeadlineSection
import de.bollich.fitnessboy.ui.theme.FitnessBoyTheme

@Composable
fun ProfileRoute(
    viewModel: ProfileViewModel,
    onOpenWeightHistory: () -> Unit,
    onOpenMeasurementsHistory: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    if (uiState.isLoading) return

    ProfileScreen(
        uiState = uiState,
        onOpenWeightHistory = onOpenWeightHistory,
        onOpenMeasurementsHistory = onOpenMeasurementsHistory,
        onHeightValueChange = viewModel::onHeightValueChange,
        onTargetWeightValueChange = viewModel::onTargetWeightValueChange,
        onSaveProfileClick = viewModel::onSaveClick,
    )
}

@Composable
private fun ProfileScreen(
    uiState: ProfileUiState,
    onOpenWeightHistory: () -> Unit,
    onOpenMeasurementsHistory: () -> Unit,
    onHeightValueChange: (String) -> Unit,
    onTargetWeightValueChange: (String) -> Unit,
    onSaveProfileClick: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            HeadlineSection(
                title = "Profil",
                subtitle = "Passe hier deine Körperdaten und Zielwerte an.",
                metrics = listOfNotNull(
                    uiState.bmi?.let { HeadlineMetric("BMI", formatNumber(it)) },
                ),
            )
        }

        item {
            ProfileCard(
                heightValue = uiState.form.heightInput,
                targetWeightValue = uiState.form.targetWeightInput,
                savedHeightInCm = uiState.profile.heightInCm,
                savedTargetWeightInKg = uiState.profile.targetWeightInKg,
                heightErrorText = uiState.form.heightErrorRes?.let { stringResource(it) },
                targetWeightErrorText = uiState.form.targetWeightErrorRes?.let { stringResource(it) },
                bmi = uiState.bmi,
                onHeightValueChange = onHeightValueChange,
                onTargetWeightValueChange = onTargetWeightValueChange,
                onSaveClick = onSaveProfileClick,
            )
        }

        item {
            OutlinedButton(
                onClick = onOpenWeightHistory,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
            ) {
                Text("Gewichtsverlauf verwalten")
            }
        }

        item {
            OutlinedButton(
                onClick = onOpenMeasurementsHistory,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
            ) {
                Text("Maßverlauf verwalten")
            }
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
                bmi = 25.4,
                form = ProfileFormState(heightInput = "180", targetWeightInput = "78"),
            ),
            onOpenWeightHistory = {},
            onOpenMeasurementsHistory = {},
            onHeightValueChange = {},
            onTargetWeightValueChange = {},
            onSaveProfileClick = {},
        )
    }
}
