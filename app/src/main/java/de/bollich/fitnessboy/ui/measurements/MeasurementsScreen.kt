package de.bollich.fitnessboy.ui.measurements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.bollich.fitnessboy.R
import de.bollich.fitnessboy.ui.components.ScreenContentPadding
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

    MeasurementsScreen(uiState = uiState)
}

@Composable
private fun MeasurementsScreen(
    uiState: BodyMeasurementsUiState,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = ScreenContentPadding,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            HeadlineSection(
                title = stringResource(R.string.measurements_title),
                subtitle = stringResource(R.string.measurements_subtitle),
                metrics = listOfNotNull(
                    uiState.latestEntry?.let { HeadlineMetric(stringResource(R.string.metric_latest_measurement), it.date.formattedDate()) },
                ),
            )
        }

        item {
            BodyMeasurementsOverviewCard(
                latestEntry = uiState.latestEntry,
            )
        }

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
