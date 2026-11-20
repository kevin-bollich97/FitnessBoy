package de.bollich.fitnessboy.ui.weight

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.bollich.fitnessboy.R
import de.bollich.fitnessboy.format.formatNumber
import de.bollich.fitnessboy.format.formattedDate
import de.bollich.fitnessboy.model.WeightEntry
import de.bollich.fitnessboy.ui.components.HeadlineMetric
import de.bollich.fitnessboy.ui.components.HeadlineSection
import de.bollich.fitnessboy.ui.components.formatKg
import de.bollich.fitnessboy.ui.theme.FitnessBoyTheme
import java.time.LocalDate

@Composable
fun WeightHistoryRoute(
    viewModel: WeightViewModel,
    onBackClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    if (uiState.isLoading) return

    WeightHistoryScreen(
        entries = uiState.entries,
        onBackClick = onBackClick,
        onDeleteEntry = viewModel::onDeleteEntry,
    )
}

@Composable
private fun WeightHistoryScreen(
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
                title = stringResource(R.string.weight_history_title),
                subtitle = stringResource(R.string.weight_history_subtitle),
                metrics = listOfNotNull(
                    entries.firstOrNull()?.let { HeadlineMetric(stringResource(R.string.metric_latest_weight), formatKg(it.weightInKg)) },
                ),
            )
        }

        item {
            OutlinedButton(
                onClick = onBackClick,
                shape = RoundedCornerShape(20.dp),
            ) {
                Text(stringResource(R.string.action_back_to_profile))
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
fun WeightHistoryRow(
    entry: WeightEntry,
    onDelete: () -> Unit,
) {
    ElevatedCard(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = formatNumber(entry.weightInKg),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = formatKg(entry.weightInKg),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = entry.formattedDate(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(
                    painter = painterResource(R.drawable.ic_delete),
                    contentDescription = stringResource(R.string.action_delete_entry),
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WeightHistoryScreenPreview() {
    FitnessBoyTheme {
        WeightHistoryScreen(
            entries = listOf(
                WeightEntry(id = 2, date = LocalDate.of(2026, 3, 9), weightInKg = 82.4),
                WeightEntry(id = 1, date = LocalDate.of(2026, 3, 8), weightInKg = 82.9),
            ),
            onBackClick = {},
            onDeleteEntry = {},
        )
    }
}
