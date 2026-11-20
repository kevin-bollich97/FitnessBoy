package de.bollich.fitnessboy.ui.weight

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
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
import de.bollich.fitnessboy.model.UserProfile
import de.bollich.fitnessboy.model.WeightEntry
import de.bollich.fitnessboy.ui.components.HeadlineSection
import de.bollich.fitnessboy.ui.theme.FitnessBoyTheme
import java.time.LocalDate

@Composable
fun WeightRoute(viewModel: WeightViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    if (uiState.isLoading) return

    WeightScreen(
        uiState = uiState,
        onWeightValueChange = viewModel::onWeightValueChange,
        onSelectedWeightDateChange = viewModel::onSelectedDateChange,
        onAddWeightClick = viewModel::onAddWeightClick,
    )
}

@Composable
private fun WeightScreen(
    uiState: WeightUiState,
    onWeightValueChange: (String) -> Unit,
    onSelectedWeightDateChange: (LocalDate) -> Unit,
    onAddWeightClick: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            HeadlineSection(
                title = stringResource(R.string.weight_title),
                subtitle = stringResource(R.string.weight_subtitle),
                metrics = uiState.headlineMetrics(),
                secondaryMetrics = uiState.trendMetrics(),
            )
        }

        item {
            AddWeightCard(
                value = uiState.form.weightInput,
                selectedDate = uiState.form.selectedDate,
                errorText = uiState.form.errorRes?.let { stringResource(it) },
                onValueChange = onWeightValueChange,
                onDateChange = onSelectedWeightDateChange,
                onAddClick = onAddWeightClick,
            )
        }

        item {
            if (uiState.entries.isEmpty()) {
                EmptyStateCard()
            } else {
                WeightChartCard(
                    entries = uiState.entries,
                    targetWeightInKg = uiState.profile.targetWeightInKg,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WeightScreenPreview() {
    val entries = listOf(
        WeightEntry(id = 2, date = LocalDate.of(2026, 3, 9), weightInKg = 82.4),
        WeightEntry(id = 1, date = LocalDate.of(2026, 3, 8), weightInKg = 82.9),
    )
    FitnessBoyTheme {
        WeightScreen(
            uiState = WeightUiState(
                isLoading = false,
                entries = entries,
                profile = UserProfile(heightInCm = 180.0, targetWeightInKg = 78.0),
                latestEntry = entries.first(),
                bmi = 25.4,
                form = WeightFormState(selectedDate = LocalDate.of(2026, 3, 9)),
            ),
            onWeightValueChange = {},
            onSelectedWeightDateChange = {},
            onAddWeightClick = {},
        )
    }
}
