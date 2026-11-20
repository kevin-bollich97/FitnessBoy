package de.bollich.fitnessboy.ui.bmi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.bollich.fitnessboy.domain.BmiCategory
import de.bollich.fitnessboy.domain.WeightRange
import de.bollich.fitnessboy.format.formatNumber
import de.bollich.fitnessboy.model.UserProfile
import de.bollich.fitnessboy.model.WeightEntry
import de.bollich.fitnessboy.ui.components.HeadlineSection
import de.bollich.fitnessboy.ui.components.MetricInfoCard
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
                title = "BMI und Ziele",
                subtitle = "Hier siehst du deinen BMI, Trends und Richtwerte auf Basis deiner Größe.",
                metrics = uiState.headlineMetrics(),
                secondaryMetrics = uiState.trendMetrics(),
            )
        }

        item {
            BmiInsightsCard(
                bmi = uiState.bmi?.let(::formatNumber),
                bmiCategory = uiState.bmiCategory,
                heightText = uiState.profile.heightInCm?.let { "${formatNumber(it)} cm" },
                currentWeightText = uiState.latestEntry?.let { "${formatNumber(it.weightInKg)} kg" },
            )
        }

        uiState.goalProgress?.let { goalProgress ->
            item {
                GoalProgressCard(goalProgress = goalProgress)
            }
        }

        item {
            MetricInfoCard(
                title = "Optimalgewicht",
                value = uiState.optimalWeightInKg?.let { "${formatNumber(it)} kg" } ?: "Bitte Profil ausfüllen",
                description = "Grobe Orientierung mit BMI 22 als Zielwert.",
            )
        }

        item {
            MetricInfoCard(
                title = "Gesunder Bereich",
                value = uiState.healthyWeightRange?.let {
                    "${formatNumber(it.minimumInKg)} bis ${formatNumber(it.maximumInKg)} kg"
                } ?: "Bitte Profil ausfüllen",
                description = "Abgeleitet aus BMI 18.5 bis 24.9 für deine Größe.",
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
