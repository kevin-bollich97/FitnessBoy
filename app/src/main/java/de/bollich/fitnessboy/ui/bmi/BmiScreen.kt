package de.bollich.fitnessboy.ui.bmi

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
import de.bollich.fitnessboy.domain.BmiCategory
import de.bollich.fitnessboy.domain.WeightRange
import de.bollich.fitnessboy.format.formatNumber
import de.bollich.fitnessboy.model.UserProfile
import de.bollich.fitnessboy.model.WeightEntry
import de.bollich.fitnessboy.ui.components.HeadlineSection
import de.bollich.fitnessboy.ui.components.MetricInfoCard
import de.bollich.fitnessboy.ui.components.formatCm
import de.bollich.fitnessboy.ui.components.formatKg
import de.bollich.fitnessboy.ui.theme.FitnessBoyTheme
import de.bollich.fitnessboy.ui.weight.WeightUiState
import de.bollich.fitnessboy.ui.weight.WeightViewModel
import de.bollich.fitnessboy.ui.weight.headlineMetrics
import de.bollich.fitnessboy.ui.weight.trendMetrics
import java.time.LocalDate

@Composable
fun BmiRoute(viewModel: WeightViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    if (uiState.isLoading) return

    BmiScreen(uiState = uiState)
}

@Composable
private fun BmiScreen(
    uiState: WeightUiState,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            HeadlineSection(
                title = stringResource(R.string.bmi_title),
                subtitle = stringResource(R.string.bmi_subtitle),
                metrics = uiState.headlineMetrics(),
                secondaryMetrics = uiState.trendMetrics(),
            )
        }

        item {
            BmiInsightsCard(
                bmi = uiState.bmi?.let(::formatNumber),
                bmiCategory = uiState.bmiCategory,
                heightText = uiState.profile.heightInCm?.let { formatCm(it) },
                currentWeightText = uiState.latestEntry?.let { formatKg(it.weightInKg) },
            )
        }

        uiState.goalProgress?.let { goalProgress ->
            item {
                GoalProgressCard(goalProgress = goalProgress)
            }
        }

        item {
            MetricInfoCard(
                title = stringResource(R.string.bmi_optimal_weight_title),
                value = uiState.optimalWeightInKg?.let { formatKg(it) } ?: stringResource(R.string.hint_complete_profile),
                description = stringResource(R.string.bmi_optimal_weight_description, formatNumber(22.0)),
            )
        }

        item {
            MetricInfoCard(
                title = stringResource(R.string.bmi_healthy_range_title),
                value = uiState.healthyWeightRange?.let {
                    stringResource(
                        R.string.bmi_healthy_range_value,
                        formatNumber(it.minimumInKg),
                        formatNumber(it.maximumInKg),
                    )
                } ?: stringResource(R.string.hint_complete_profile),
                description = stringResource(R.string.bmi_healthy_range_description, formatNumber(18.5), formatNumber(24.9)),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun BmiScreenPreview() {
    val latestEntry = WeightEntry(id = 1, date = LocalDate.of(2026, 3, 9), weightInKg = 82.4)
    FitnessBoyTheme {
        BmiScreen(
            uiState = WeightUiState(
                isLoading = false,
                entries = listOf(latestEntry),
                profile = UserProfile(heightInCm = 180.0, targetWeightInKg = 78.0),
                latestEntry = latestEntry,
                bmi = 25.4,
                bmiCategory = BmiCategory.OVERWEIGHT,
                healthyWeightRange = WeightRange(minimumInKg = 59.9, maximumInKg = 80.7),
                optimalWeightInKg = 71.3,
            ),
        )
    }
}
