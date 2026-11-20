package de.bollich.fitnessboy.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import de.bollich.fitnessboy.ui.bmi.BmiRoute
import de.bollich.fitnessboy.ui.measurements.BodyMeasurementsViewModel
import de.bollich.fitnessboy.ui.measurements.MeasurementsHistoryRoute
import de.bollich.fitnessboy.ui.measurements.MeasurementsRoute
import de.bollich.fitnessboy.ui.navigation.AppTab
import de.bollich.fitnessboy.ui.navigation.Destination
import de.bollich.fitnessboy.ui.profile.ProfileRoute
import de.bollich.fitnessboy.ui.profile.ProfileViewModel
import de.bollich.fitnessboy.ui.weight.WeightHistoryRoute
import de.bollich.fitnessboy.ui.weight.WeightRoute
import de.bollich.fitnessboy.ui.weight.WeightViewModel

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
                        AppTab.MEASUREMENTS -> MeasurementsRoute(bodyMeasurementsViewModel)
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
                                contentDescription = stringResource(tab.labelRes),
                            )
                        },
                        label = { Text(stringResource(tab.labelRes)) },
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
