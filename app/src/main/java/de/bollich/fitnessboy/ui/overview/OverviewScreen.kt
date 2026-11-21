package de.bollich.fitnessboy.ui.overview

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
import de.bollich.fitnessboy.ui.components.ScreenContentPadding
import de.bollich.fitnessboy.domain.BmiCategory
import de.bollich.fitnessboy.domain.GoalDirection
import de.bollich.fitnessboy.domain.GoalProgress
import de.bollich.fitnessboy.domain.GoalStatus
import de.bollich.fitnessboy.domain.WeightTrend
import de.bollich.fitnessboy.domain.WeightTrendPeriod
import de.bollich.fitnessboy.model.BodyMeasurementsEntry
import de.bollich.fitnessboy.model.WeightEntry
import de.bollich.fitnessboy.ui.navigation.AppTab
import de.bollich.fitnessboy.ui.theme.FitnessBoyTheme
import java.time.LocalDate

@Composable
fun OverviewRoute(
    viewModel: OverviewViewModel,
    onOpenTab: (AppTab) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    if (uiState.isLoading) return

    OverviewScreen(
        uiState = uiState,
        onOpenTab = onOpenTab,
        onBmiInfoVisibilityChange = viewModel::onBmiInfoVisibilityChange,
    )
}

@Composable
private fun OverviewScreen(
    uiState: OverviewUiState,
    onOpenTab: (AppTab) -> Unit,
    onBmiInfoVisibilityChange: (Boolean) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = ScreenContentPadding,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = stringResource(R.string.overview_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
        }
        item {
            WeightSummaryCard(
                latestWeight = uiState.latestWeight,
                recentTrend = uiState.recentTrend,
                totalTrend = uiState.totalTrend,
                onClick = { onOpenTab(AppTab.WEIGHT) },
            )
        }
        when {
            !uiState.hasTargetWeight -> item { MissingGoalCard(onClick = { onOpenTab(AppTab.PROFILE) }) }
            uiState.goalProgress != null -> item { GoalCard(goalProgress = uiState.goalProgress) }
        }
        item {
            BmiCard(
                bmi = uiState.bmi,
                bmiCategory = uiState.bmiCategory,
                hasHeight = uiState.heightInCm != null,
                onShowExplanation = { onBmiInfoVisibilityChange(true) },
                onOpenProfile = { onOpenTab(AppTab.PROFILE) },
            )
        }
        item {
            MeasurementsSummaryCard(
                latestMeasurements = uiState.latestMeasurements,
                onClick = { onOpenTab(AppTab.MEASUREMENTS) },
            )
        }
    }

    val bmi = uiState.bmi
    val bmiCategory = uiState.bmiCategory
    val heightInCm = uiState.heightInCm
    if (uiState.isBmiInfoVisible && bmi != null && bmiCategory != null && heightInCm != null) {
        BmiInfoSheet(
            bmi = bmi,
            bmiCategory = bmiCategory,
            heightInCm = heightInCm,
            healthyWeightRange = uiState.healthyWeightRange,
            optimalWeightInKg = uiState.optimalWeightInKg,
            onDismiss = { onBmiInfoVisibilityChange(false) },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun OverviewScreenPreview() {
    val latest = WeightEntry(id = 3, date = LocalDate.of(2026, 3, 20), weightInKg = 82.4)
    FitnessBoyTheme {
        OverviewScreen(
            uiState = OverviewUiState(
                isLoading = false,
                latestWeight = latest,
                recentTrend = previewTrend(WeightTrendPeriod.SEVEN_DAYS, LocalDate.of(2026, 3, 13), 83.1, latest),
                totalTrend = previewTrend(WeightTrendPeriod.SINCE_START, LocalDate.of(2026, 3, 1), 85.0, latest),
                hasTargetWeight = true,
                goalProgress = GoalProgress(
                    startWeightInKg = 85.0,
                    currentWeightInKg = 82.4,
                    targetWeightInKg = 78.0,
                    remainingInKg = -4.4,
                    achievedInKg = -2.6,
                    progressPercent = 37,
                    direction = GoalDirection.LOSE,
                    status = GoalStatus.IN_PROGRESS,
                ),
                heightInCm = 180.0,
                bmi = 25.4,
                bmiCategory = BmiCategory.OVERWEIGHT,
                latestMeasurements = BodyMeasurementsEntry(
                    date = LocalDate.of(2026, 3, 9),
                    waistInCm = 88.0,
                    hipsInCm = 95.0,
                    shouldersInCm = 48.0,
                ),
            ),
            onOpenTab = {},
            onBmiInfoVisibilityChange = {},
        )
    }
}

private fun previewTrend(
    period: WeightTrendPeriod,
    referenceDate: LocalDate,
    referenceWeightInKg: Double,
    latest: WeightEntry,
) = WeightTrend(
    period = period,
    deltaInKg = latest.weightInKg - referenceWeightInKg,
    referenceDate = referenceDate,
    referenceWeightInKg = referenceWeightInKg,
    latestDate = latest.date,
    latestWeightInKg = latest.weightInKg,
)
