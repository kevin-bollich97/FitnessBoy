package de.bollich.fitnessboy.ui.weight

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.bollich.fitnessboy.R
import de.bollich.fitnessboy.domain.ChartGroup
import de.bollich.fitnessboy.domain.ChartPeriod
import de.bollich.fitnessboy.format.formattedDate
import de.bollich.fitnessboy.model.WeightEntry
import de.bollich.fitnessboy.ui.components.COLLAPSED_ENTRY_COUNT
import de.bollich.fitnessboy.ui.components.ConfirmDeleteDialog
import de.bollich.fitnessboy.ui.components.EntryListHeader
import de.bollich.fitnessboy.ui.components.MonthHeader
import de.bollich.fitnessboy.ui.components.ScreenContentPadding
import de.bollich.fitnessboy.ui.components.ShowAllButton
import de.bollich.fitnessboy.ui.components.formatKg
import de.bollich.fitnessboy.ui.components.sectionsByMonth
import de.bollich.fitnessboy.ui.theme.FitnessBoyTheme
import java.time.LocalDate

@Composable
fun WeightRoute(viewModel: WeightViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    if (uiState.isLoading) return

    WeightScreen(
        uiState = uiState,
        onPeriodChange = viewModel::onChartPeriodChange,
        onGroupSelected = viewModel::onChartGroupSelected,
        onShowAllEntriesToggle = viewModel::onShowAllEntriesToggle,
        onDeleteRequest = viewModel::onDeleteRequest,
        onDeleteConfirm = viewModel::onDeleteConfirm,
    )
}

@Composable
private fun WeightScreen(
    uiState: WeightUiState,
    onPeriodChange: (ChartPeriod) -> Unit,
    onGroupSelected: (ChartGroup) -> Unit,
    onShowAllEntriesToggle: () -> Unit,
    onDeleteRequest: (WeightEntry?) -> Unit,
    onDeleteConfirm: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = ScreenContentPadding,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Text(
                text = stringResource(R.string.weight_title),
                style = MaterialTheme.typography.headlineMedium,
            )
        }

        if (!uiState.hasEntries) {
            item {
                Text(
                    text = stringResource(R.string.weight_empty),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
            return@LazyColumn
        }

        item {
            WeightChartCard(
                period = uiState.chartPeriod,
                groups = uiState.chartGroups,
                selectedGroup = uiState.selectedGroup,
                selectedGroupBmi = uiState.selectedGroupBmi,
                targetWeightInKg = uiState.targetWeightInKg,
                onPeriodChange = onPeriodChange,
                onGroupSelected = onGroupSelected,
            )
        }
        if (uiState.items.isEmpty()) return@LazyColumn
        val canExpand = uiState.items.size > COLLAPSED_ENTRY_COUNT
        item {
            EntryListHeader(
                title = stringResource(R.string.weight_entries_title),
                isExpanded = canExpand && uiState.showAllEntries,
                onCollapse = onShowAllEntriesToggle,
            )
        }
        uiState.entrySections.forEach { section ->
            stickyHeader(key = section.month) { MonthHeader(section) }
            items(section.items, key = { it.entry.id }) { item ->
                WeightEntryRow(item = item, onDelete = { onDeleteRequest(item.entry) })
                HorizontalDivider()
            }
        }
        if (canExpand) {
            item {
                ShowAllButton(
                    isExpanded = uiState.showAllEntries,
                    totalCount = uiState.items.size,
                    onToggle = onShowAllEntriesToggle,
                )
            }
        }
    }

    uiState.entryPendingDeletion?.let { entry ->
        ConfirmDeleteDialog(
            text = stringResource(R.string.weight_delete_text, entry.formattedDate(), formatKg(entry.weightInKg)),
            onConfirm = onDeleteConfirm,
            onDismiss = { onDeleteRequest(null) },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun WeightScreenPreview() {
    val items = listOf(
        WeightListItem(WeightEntry(id = 3, date = LocalDate.of(2026, 3, 20), weightInKg = 82.4), bmi = 25.4),
        WeightListItem(WeightEntry(id = 2, date = LocalDate.of(2026, 3, 9), weightInKg = 83.1), bmi = 25.6),
        WeightListItem(WeightEntry(id = 1, date = LocalDate.of(2026, 3, 1), weightInKg = 85.0), bmi = 26.2),
    )
    FitnessBoyTheme {
        WeightScreen(
            uiState = WeightUiState(
                isLoading = false,
                items = items,
                targetWeightInKg = 78.0,
                hasEntries = true,
                entrySections = sectionsByMonth(items, dateOf = { it.entry.date }),
            ),
            onPeriodChange = {},
            onGroupSelected = {},
            onShowAllEntriesToggle = {},
            onDeleteRequest = {},
            onDeleteConfirm = {},
        )
    }
}
