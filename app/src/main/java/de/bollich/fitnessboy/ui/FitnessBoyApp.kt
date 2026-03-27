package de.bollich.fitnessboy.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import de.bollich.fitnessboy.domain.WeightTrend
import de.bollich.fitnessboy.format.formatNumber
import de.bollich.fitnessboy.model.UserProfile
import de.bollich.fitnessboy.model.WeightEntry
import de.bollich.fitnessboy.ui.components.AddWeightCard
import de.bollich.fitnessboy.ui.components.BmiInsightsCard
import de.bollich.fitnessboy.ui.components.EmptyStateCard
import de.bollich.fitnessboy.ui.components.GoalProgressCard
import de.bollich.fitnessboy.ui.components.HeadlineSection
import de.bollich.fitnessboy.ui.components.MetricInfoCard
import de.bollich.fitnessboy.ui.components.ProfileCard
import de.bollich.fitnessboy.ui.components.WeightChartCard
import de.bollich.fitnessboy.ui.components.WeightHistoryRow
import de.bollich.fitnessboy.ui.theme.FitnessBoyTheme
import java.time.LocalDate

@Composable
fun FitnessBoyApp(
    viewModel: FitnessBoyViewModel = viewModel(
        factory = FitnessBoyViewModel.factory(LocalContext.current)
    ),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FitnessBoyScreen(
        uiState = uiState,
        onTabSelected = viewModel::onTabSelected,
        onWeightPageChange = viewModel::onWeightPageChange,
        onWeightValueChange = viewModel::onWeightValueChange,
        onSelectedWeightDateChange = viewModel::onSelectedWeightDateChange,
        onHeightValueChange = viewModel::onHeightValueChange,
        onTargetWeightValueChange = viewModel::onTargetWeightValueChange,
        onSaveProfileClick = viewModel::onSaveProfileClick,
        onAddWeightClick = viewModel::onAddWeightClick,
        onDeleteEntry = viewModel::onDeleteEntry,
    )
}

@Composable
private fun FitnessBoyScreen(
    uiState: FitnessBoyUiState,
    onTabSelected: (AppTab) -> Unit,
    onWeightPageChange: (WeightPage) -> Unit,
    onWeightValueChange: (String) -> Unit,
    onSelectedWeightDateChange: (LocalDate) -> Unit,
    onHeightValueChange: (String) -> Unit,
    onTargetWeightValueChange: (String) -> Unit,
    onSaveProfileClick: () -> Unit,
    onAddWeightClick: () -> Unit,
    onDeleteEntry: (WeightEntry) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp),
                tonalElevation = 0.dp,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                windowInsets = WindowInsets(0, 0, 0, 0),
            ) {
                AppTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = tab == uiState.selectedTab,
                        onClick = { onTabSelected(tab) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            indicatorColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                        icon = {
                            Icon(
                                painter = painterResource(tab.iconRes),
                                contentDescription = tab.label,
                            )
                        },
                        label = { Text(tab.label) },
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                            MaterialTheme.colorScheme.tertiary.copy(alpha = 0.10f),
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.colorScheme.secondary.copy(alpha = 0.10f),
                        )
                    )
                )
                .padding(innerPadding)
        ) {
            when (uiState.selectedTab) {
                AppTab.WEIGHT -> WeightTab(
                    uiState = uiState,
                    onWeightPageChange = onWeightPageChange,
                    onWeightValueChange = onWeightValueChange,
                    onSelectedWeightDateChange = onSelectedWeightDateChange,
                    onAddWeightClick = onAddWeightClick,
                    onDeleteEntry = onDeleteEntry,
                )

                AppTab.BMI -> BmiTab(uiState = uiState)
                AppTab.PROFILE -> ProfileTab(
                    uiState = uiState,
                    onHeightValueChange = onHeightValueChange,
                    onTargetWeightValueChange = onTargetWeightValueChange,
                    onSaveProfileClick = onSaveProfileClick,
                )
            }
        }
    }
}

@Composable
private fun WeightTab(
    uiState: FitnessBoyUiState,
    onWeightPageChange: (WeightPage) -> Unit,
    onWeightValueChange: (String) -> Unit,
    onSelectedWeightDateChange: (LocalDate) -> Unit,
    onAddWeightClick: () -> Unit,
    onDeleteEntry: (WeightEntry) -> Unit,
) {
    if (uiState.selectedWeightPage == WeightPage.HISTORY) {
        WeightHistoryPage(
            entries = uiState.entries,
            onBackClick = { onWeightPageChange(WeightPage.DASHBOARD) },
            onDeleteEntry = onDeleteEntry,
        )
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            HeadlineSection(
                title = "Gewicht",
                subtitle = "Trage neue Werte ein und beobachte deinen Verlauf.",
                latestEntry = uiState.latestEntry,
                trend = uiState.primaryTrend,
                trendList = uiState.trends,
                bmi = uiState.bmi,
            )
        }

        item {
            AddWeightCard(
                value = uiState.weightInput,
                selectedDate = uiState.selectedWeightDate,
                errorText = uiState.weightErrorText,
                onValueChange = onWeightValueChange,
                onDateChange = onSelectedWeightDateChange,
                onAddClick = onAddWeightClick,
            )
        }

        uiState.goalProgress?.let { goalProgress ->
            item {
                GoalProgressCard(goalProgress = goalProgress)
            }
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

        if (uiState.entries.isNotEmpty()) {
            item {
                OutlinedButton(
                    onClick = { onWeightPageChange(WeightPage.HISTORY) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                ) {
                    Text("Verlauf öffnen")
                }
            }
        }
    }
}

@Composable
private fun WeightHistoryPage(
    entries: List<WeightEntry>,
    onBackClick: () -> Unit,
    onDeleteEntry: (WeightEntry) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            HeadlineSection(
                title = "Verlauf",
                subtitle = "Hier findest du alle gespeicherten Wiegeeinträge und kannst sie bei Bedarf löschen.",
                latestEntry = entries.firstOrNull(),
                trend = null,
                trendList = emptyList(),
                bmi = null,
            )
        }

        item {
            OutlinedButton(
                onClick = onBackClick,
                shape = RoundedCornerShape(20.dp),
            ) {
                Text("Zurück zur Gewichtsseite")
            }
        }

        items(entries, key = { "${it.date}-${it.weightInKg}" }) { entry ->
            WeightHistoryRow(
                entry = entry,
                onDelete = { onDeleteEntry(entry) },
            )
        }
    }
}

@Composable
private fun BmiTab(uiState: FitnessBoyUiState) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            HeadlineSection(
                title = "BMI und Ziele",
                subtitle = "Hier siehst du deinen BMI und Richtwerte auf Basis deiner Größe.",
                latestEntry = uiState.latestEntry,
                trend = null,
                trendList = emptyList(),
                bmi = uiState.bmi,
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
                value = uiState.optimalWeightText ?: "Bitte Profil ausfüllen",
                description = "Grobe Orientierung mit BMI 22 als Zielwert.",
            )
        }

        item {
            MetricInfoCard(
                title = "Gesunder Bereich",
                value = uiState.healthyWeightRangeText ?: "Bitte Profil ausfüllen",
                description = "Abgeleitet aus BMI 18.5 bis 24.9 für deine Größe.",
            )
        }
    }
}

@Composable
private fun ProfileTab(
    uiState: FitnessBoyUiState,
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
                latestEntry = null,
                trend = null,
                trendList = emptyList(),
                bmi = uiState.bmi,
            )
        }

        item {
            ProfileCard(
                heightValue = uiState.heightInput,
                targetWeightValue = uiState.targetWeightInput,
                savedHeightInCm = uiState.profile.heightInCm,
                savedTargetWeightInKg = uiState.profile.targetWeightInKg,
                heightErrorText = uiState.heightErrorText,
                targetWeightErrorText = uiState.targetWeightErrorText,
                bmi = uiState.bmi,
                onHeightValueChange = onHeightValueChange,
                onTargetWeightValueChange = onTargetWeightValueChange,
                onSaveClick = onSaveProfileClick,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FitnessBoyAppPreview() {
    FitnessBoyTheme {
        FitnessBoyScreen(
            uiState = FitnessBoyUiState(
                entries = listOf(
                    WeightEntry(LocalDate.of(2026, 3, 8), 82.9),
                    WeightEntry(LocalDate.of(2026, 3, 9), 82.4),
                ),
                profile = UserProfile(heightInCm = 180.0, targetWeightInKg = 78.0),
                latestEntry = WeightEntry(LocalDate.of(2026, 3, 9), 82.4),
                bmi = 25.4,
                bmiCategory = "Übergewicht",
                healthyWeightRangeText = "59.9 bis 80.7 kg",
                optimalWeightText = "71.3 kg",
                selectedWeightDate = LocalDate.of(2026, 3, 9),
                heightInput = "180",
                targetWeightInput = "78",
            ),
            onTabSelected = {},
            onWeightPageChange = {},
            onWeightValueChange = {},
            onSelectedWeightDateChange = {},
            onHeightValueChange = {},
            onTargetWeightValueChange = {},
            onSaveProfileClick = {},
            onAddWeightClick = {},
            onDeleteEntry = {},
        )
    }
}
