package de.bollich.fitnessboy.ui.measurements

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.bollich.fitnessboy.R
import de.bollich.fitnessboy.format.formatNumber
import de.bollich.fitnessboy.format.formattedDate
import de.bollich.fitnessboy.model.BodyMeasurementsEntry
import de.bollich.fitnessboy.ui.components.HeadlineSection
import de.bollich.fitnessboy.ui.theme.FitnessBoyTheme

@Composable
fun MeasurementsHistoryRoute(
    viewModel: BodyMeasurementsViewModel,
    onBackClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    if (uiState.isLoading) return

    MeasurementsHistoryScreen(
        entries = uiState.entries,
        onBackClick = onBackClick,
        onDeleteEntry = viewModel::onDeleteEntry,
    )
}

@Composable
private fun MeasurementsHistoryScreen(
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
                title = stringResource(R.string.measurements_history_title),
                subtitle = stringResource(R.string.measurements_history_subtitle),
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
            BodyMeasurementsHistoryRow(
                entry = entry,
                onDelete = { onDeleteEntry(entry) },
            )
        }
    }
}

@Composable
fun BodyMeasurementsHistoryRow(
    entry: BodyMeasurementsEntry,
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
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = entry.date.formattedDate(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(
                        R.string.measurements_history_summary,
                        formatNumber(entry.waistInCm),
                        formatNumber(entry.hipsInCm),
                        formatNumber(entry.shouldersInCm),
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
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
private fun MeasurementsHistoryScreenPreview() {
    FitnessBoyTheme {
        MeasurementsHistoryScreen(
            entries = listOf(previewBodyMeasurementsEntry),
            onBackClick = {},
            onDeleteEntry = {},
        )
    }
}
