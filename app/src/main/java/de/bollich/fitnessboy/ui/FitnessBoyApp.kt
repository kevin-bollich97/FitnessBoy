package de.bollich.fitnessboy.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import de.bollich.fitnessboy.format.formatNumber
import de.bollich.fitnessboy.model.BodyMeasurementsEntry
import de.bollich.fitnessboy.model.UserProfile
import de.bollich.fitnessboy.model.WeightEntry
import de.bollich.fitnessboy.ui.components.AddBodyMeasurementsDialog
import de.bollich.fitnessboy.ui.components.AddWeightCard
import de.bollich.fitnessboy.ui.components.BmiInsightsCard
import de.bollich.fitnessboy.ui.components.BodyMeasurementsHistoryRow
import de.bollich.fitnessboy.ui.components.BodyMeasurementsOverviewCard
import de.bollich.fitnessboy.ui.components.EmptyStateCard
import de.bollich.fitnessboy.ui.components.GoalProgressCard
import de.bollich.fitnessboy.ui.components.HeadlineSection
import de.bollich.fitnessboy.ui.components.MetricInfoCard
import de.bollich.fitnessboy.ui.components.ProfileCard
import de.bollich.fitnessboy.ui.components.WeightChartCard
import de.bollich.fitnessboy.ui.components.WeightHistoryRow
import de.bollich.fitnessboy.ui.measurements.BodyMeasurementsUiState
import de.bollich.fitnessboy.ui.measurements.BodyMeasurementsViewModel
import de.bollich.fitnessboy.ui.navigation.AppTab
import de.bollich.fitnessboy.ui.navigation.Destination
import de.bollich.fitnessboy.ui.profile.ProfileUiState
import de.bollich.fitnessboy.ui.profile.ProfileViewModel
import de.bollich.fitnessboy.ui.theme.FitnessBoyTheme
import de.bollich.fitnessboy.ui.weight.WeightFormState
import de.bollich.fitnessboy.ui.weight.WeightUiState
import de.bollich.fitnessboy.ui.weight.WeightViewModel
import java.time.LocalDate

@Composable
fun FitnessBoyApp(
    mainViewModel: MainViewModel = viewModel(factory = MainViewModel.Factory),
    weightViewModel: WeightViewModel = viewModel(factory = WeightViewModel.Factory),
    bodyMeasurementsViewModel: BodyMeasurementsViewModel = viewModel(factory = BodyMeasurementsViewModel.Factory),
    profileViewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.Factory),
) {
    val mainState by mainViewModel.uiState.collectAsStateWithLifecycle()

    FitnessBoyScaffold(
        selectedTab = mainState.selectedTab,
        onTabSelected = mainViewModel::onTabSelected,
    ) {
        if (mainState.isLoading) return@FitnessBoyScaffold

        NavDisplay(
            backStack = mainState.backStack,
            onBack = mainViewModel::onBack,
            entryProvider = entryProvider {
                entry<Destination.Tab> { destination ->
                    when (destination.tab) {
                        AppTab.WEIGHT -> WeightRoute(weightViewModel)
                        AppTab.BMI -> BmiRoute(weightViewModel)
                        AppTab.MEASUREMENTS -> MeasurementsRoute(
                            viewModel = bodyMeasurementsViewModel,
                            weightViewModel = weightViewModel,
                        )
                        AppTab.PROFILE -> ProfileRoute(
                            viewModel = profileViewModel,
                            onOpenWeightHistory = { mainViewModel.navigateTo(Destination.WeightHistory) },
                            onOpenMeasurementsHistory = {
                                mainViewModel.navigateTo(Destination.MeasurementsHistory)
                            },
                        )
                    }
                }
                entry<Destination.WeightHistory> {
                    WeightHistoryRoute(
                        viewModel = weightViewModel,
                        onBackClick = mainViewModel::onBack,
                    )
                }
                entry<Destination.MeasurementsHistory> {
                    MeasurementsHistoryRoute(
                        viewModel = bodyMeasurementsViewModel,
                        onBackClick = mainViewModel::onBack,
                    )
                }
            },
        )
    }
}

@Composable
private fun FitnessBoyScaffold(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    content: @Composable BoxScope.() -> Unit,
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
                        selected = tab == selectedTab,
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
            content()
        }
    }
}

@Composable
private fun WeightRoute(viewModel: WeightViewModel) {
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
private fun WeightHistoryRoute(
    viewModel: WeightViewModel,
    onBackClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    if (uiState.isLoading) return

    WeightHistoryPage(
        entries = uiState.entries,
        onBackClick = onBackClick,
        onDeleteEntry = viewModel::onDeleteEntry,
    )
}

@Composable
private fun BmiRoute(viewModel: WeightViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    if (uiState.isLoading) return

    BmiScreen(uiState = uiState)
}

@Composable
private fun MeasurementsRoute(
    viewModel: BodyMeasurementsViewModel,
    weightViewModel: WeightViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val weightUiState by weightViewModel.uiState.collectAsStateWithLifecycle()
    if (uiState.isLoading || weightUiState.isLoading) return

    MeasurementsScreen(
        uiState = uiState,
        latestWeightEntry = weightUiState.latestEntry,
        onBodyMeasurementsDialogChange = viewModel::onDialogVisibilityChange,
        onBodyMeasurementsDateChange = viewModel::onDateChange,
        onWaistValueChange = viewModel::onWaistValueChange,
        onHipsValueChange = viewModel::onHipsValueChange,
        onShouldersValueChange = viewModel::onShouldersValueChange,
        onAddBodyMeasurementsClick = viewModel::onSaveClick,
    )
}

@Composable
private fun MeasurementsHistoryRoute(
    viewModel: BodyMeasurementsViewModel,
    onBackClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    if (uiState.isLoading) return

    MeasurementsHistoryPage(
        entries = uiState.entries,
        onBackClick = onBackClick,
        onDeleteEntry = viewModel::onDeleteEntry,
    )
}

@Composable
private fun ProfileRoute(
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
                value = uiState.form.weightInput,
                selectedDate = uiState.form.selectedDate,
                errorText = uiState.form.errorText,
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
                Text("Zurück zum Profil")
            }
        }

        items(entries, key = { it.id }) { entry ->
            WeightHistoryRow(
                entry = entry,
                onDelete = { onDeleteEntry(entry) },
            )
        }
    }
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
                latestEntry = uiState.latestEntry,
                trend = uiState.primaryTrend,
                trendList = uiState.trends,
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
private fun MeasurementsScreen(
    uiState: BodyMeasurementsUiState,
    latestWeightEntry: WeightEntry?,
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
                latestEntry = latestWeightEntry,
                trend = null,
                trendList = emptyList(),
                bmi = null,
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
            waistErrorText = uiState.form.waistErrorText,
            hipsInput = uiState.form.hipsInput,
            hipsErrorText = uiState.form.hipsErrorText,
            shouldersInput = uiState.form.shouldersInput,
            shouldersErrorText = uiState.form.shouldersErrorText,
            onDateChange = onBodyMeasurementsDateChange,
            onWaistChange = onWaistValueChange,
            onHipsChange = onHipsValueChange,
            onShouldersChange = onShouldersValueChange,
            onDismiss = { onBodyMeasurementsDialogChange(false) },
            onSave = onAddBodyMeasurementsClick,
        )
    }
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
                latestEntry = null,
                trend = null,
                trendList = emptyList(),
                bmi = uiState.bmi,
            )
        }

        item {
            ProfileCard(
                heightValue = uiState.form.heightInput,
                targetWeightValue = uiState.form.targetWeightInput,
                savedHeightInCm = uiState.profile.heightInCm,
                savedTargetWeightInKg = uiState.profile.targetWeightInKg,
                heightErrorText = uiState.form.heightErrorText,
                targetWeightErrorText = uiState.form.targetWeightErrorText,
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

@Composable
private fun MeasurementsHistoryPage(
    entries: List<BodyMeasurementsEntry>,
    onBackClick: () -> Unit,
    onDeleteEntry: (BodyMeasurementsEntry) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            HeadlineSection(
                title = "Maßverlauf",
                subtitle = "Hier findest du alle gespeicherten Körpermaße und kannst sie bei Bedarf löschen.",
                latestEntry = null,
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
                Text("Zurück zum Profil")
            }
        }

        items(entries, key = { it.id }) { entry ->
            BodyMeasurementsHistoryRow(
                entry = entry,
                onDelete = { onDeleteEntry(entry) },
            )
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
