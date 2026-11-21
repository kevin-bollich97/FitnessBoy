package de.bollich.fitnessboy.ui.measurements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
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
import de.bollich.fitnessboy.domain.BodyMeasurement
import de.bollich.fitnessboy.format.formattedDate
import de.bollich.fitnessboy.model.BodyMeasurementsEntry
import de.bollich.fitnessboy.ui.components.ConfirmDeleteDialog
import de.bollich.fitnessboy.ui.components.ScreenContentPadding
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
        onMeasurementChange = viewModel::onChartMeasurementChange,
        onEntrySelected = viewModel::onChartEntrySelected,
        onDeleteRequest = viewModel::onDeleteRequest,
        onDeleteConfirm = viewModel::onDeleteConfirm,
    )
}

@Composable
private fun MeasurementsScreen(
    uiState: BodyMeasurementsUiState,
    onMeasurementChange: (BodyMeasurement) -> Unit,
    onEntrySelected: (BodyMeasurementsEntry) -> Unit,
    onDeleteRequest: (BodyMeasurementsEntry?) -> Unit,
    onDeleteConfirm: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = ScreenContentPadding,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = stringResource(R.string.measurements_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
        }

        val latestEntry = uiState.latestEntry
        if (latestEntry == null) {
            item {
                Text(
                    text = stringResource(R.string.measurements_empty),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            return@LazyColumn
        }

        item {
            LatestMeasurementsCard(latestEntry = latestEntry, highlighted = uiState.chartMeasurement)
        }
        item {
            MeasurementsChartCard(
                items = uiState.items,
                measurement = uiState.chartMeasurement,
                selectedItem = uiState.selectedItem,
                onMeasurementChange = onMeasurementChange,
                onEntrySelected = onEntrySelected,
            )
        }
        item {
            Text(
                text = stringResource(R.string.measurements_entries_title),
                modifier = Modifier.padding(top = 12.dp),
                style = MaterialTheme.typography.titleMedium,
            )
        }
        items(uiState.items, key = { it.entry.id }) { item ->
            MeasurementEntryRow(item = item, onDelete = { onDeleteRequest(item.entry) })
            HorizontalDivider()
        }
    }

    uiState.entryPendingDeletion?.let { entry ->
        ConfirmDeleteDialog(
            text = stringResource(R.string.measurements_delete_text, entry.date.formattedDate()),
            onConfirm = onDeleteConfirm,
            onDismiss = { onDeleteRequest(null) },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MeasurementsScreenPreview() {
    val items = listOf(
        MeasurementListItem(
            BodyMeasurementsEntry(
                id = 2,
                date = LocalDate.of(2026, 3, 20),
                waistInCm = 86.0,
                hipsInCm = 94.0,
                shouldersInCm = 48.0,
                upperArmInCm = 34.0,
            ),
            bmi = 25.4,
        ),
        MeasurementListItem(
            BodyMeasurementsEntry(id = 1, date = LocalDate.of(2026, 3, 1), waistInCm = 88.0, hipsInCm = 95.0, shouldersInCm = 48.0),
            bmi = 26.2,
        ),
    )
    FitnessBoyTheme {
        MeasurementsScreen(
            uiState = BodyMeasurementsUiState(isLoading = false, items = items, selectedItem = items.first()),
            onMeasurementChange = {},
            onEntrySelected = {},
            onDeleteRequest = {},
            onDeleteConfirm = {},
        )
    }
}
