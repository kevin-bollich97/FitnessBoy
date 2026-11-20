package de.bollich.fitnessboy.ui.measurements

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
import de.bollich.fitnessboy.format.formattedDate
import de.bollich.fitnessboy.model.BodyMeasurementsEntry
import de.bollich.fitnessboy.ui.components.HeadlineMetric
import de.bollich.fitnessboy.ui.components.HeadlineSection
import de.bollich.fitnessboy.ui.theme.FitnessBoyTheme
import java.time.LocalDate

@Composable
fun MeasurementsRoute(
    viewModel: BodyMeasurementsViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    if (uiState.isLoading) return

    MeasurementsScreen(
        uiState = uiState,
        onBodyMeasurementsDialogChange = viewModel::onDialogVisibilityChange,
        onBodyMeasurementsDateChange = viewModel::onDateChange,
        onWaistValueChange = viewModel::onWaistValueChange,
        onHipsValueChange = viewModel::onHipsValueChange,
        onShouldersValueChange = viewModel::onShouldersValueChange,
        onAddBodyMeasurementsClick = viewModel::onSaveClick,
    )
}

@Composable
private fun MeasurementsScreen(
    uiState: BodyMeasurementsUiState,
    onBodyMeasurementsDialogChange: (Boolean) -> Unit,
    onBodyMeasurementsDateChange: (LocalDate) -> Unit,
    onWaistValueChange: (String) -> Unit,
    onHipsValueChange: (String) -> Unit,
    onShouldersValueChange: (String) -> Unit,
    onAddBodyMeasurementsClick: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            HeadlineSection(
                title = "Körpermaße",
                subtitle = "Erfasse Bauch, Hüfte und Schultern und nutze die Werte später für Verlauf und Vergleiche.",
                metrics = listOfNotNull(
                    uiState.latestEntry?.let { HeadlineMetric("Letzte Messung", it.date.formattedDate()) },
                ),
            )
        }

        item {
            BodyMeasurementsOverviewCard(
                latestEntry = uiState.latestEntry,
            )
        }

        item {
            OutlinedButton(
                onClick = { onBodyMeasurementsDialogChange(true) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
            ) {
                Text("Körpermaße erfassen")
            }
        }
    }

    if (uiState.form.isDialogVisible) {
        AddBodyMeasurementsDialog(
            selectedDate = uiState.form.selectedDate,
            waistInput = uiState.form.waistInput,
            waistErrorText = uiState.form.waistErrorRes?.let { stringResource(it) },
            hipsInput = uiState.form.hipsInput,
            hipsErrorText = uiState.form.hipsErrorRes?.let { stringResource(it) },
            shouldersInput = uiState.form.shouldersInput,
            shouldersErrorText = uiState.form.shouldersErrorRes?.let { stringResource(it) },
            onDateChange = onBodyMeasurementsDateChange,
            onWaistChange = onWaistValueChange,
            onHipsChange = onHipsValueChange,
            onShouldersChange = onShouldersValueChange,
            onDismiss = { onBodyMeasurementsDialogChange(false) },
            onSave = onAddBodyMeasurementsClick,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MeasurementsScreenPreview() {
    FitnessBoyTheme {
        MeasurementsScreen(
            uiState = BodyMeasurementsUiState(
                isLoading = false,
                entries = listOf(previewBodyMeasurementsEntry),
            ),
            onBodyMeasurementsDialogChange = {},
            onBodyMeasurementsDateChange = {},
            onWaistValueChange = {},
            onHipsValueChange = {},
            onShouldersValueChange = {},
            onAddBodyMeasurementsClick = {},
        )
    }
}

internal val previewBodyMeasurementsEntry = BodyMeasurementsEntry(
    id = 1,
    date = LocalDate.of(2026, 3, 9),
    waistInCm = 88.0,
    hipsInCm = 95.0,
    shouldersInCm = 48.0,
)
