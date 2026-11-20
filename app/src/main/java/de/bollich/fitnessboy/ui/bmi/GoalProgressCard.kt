package de.bollich.fitnessboy.ui.bmi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.bollich.fitnessboy.R
import de.bollich.fitnessboy.domain.GoalDirection
import de.bollich.fitnessboy.domain.GoalProgress
import de.bollich.fitnessboy.domain.GoalStatus
import de.bollich.fitnessboy.format.formatNumber
import de.bollich.fitnessboy.ui.components.MetricPill
import de.bollich.fitnessboy.ui.components.formatKg
import kotlin.math.absoluteValue

@Composable
fun GoalProgressCard(goalProgress: GoalProgress) {
    ElevatedCard(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = stringResource(R.string.goal_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = goalStatusText(goalProgress),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MetricPill(
                    label = stringResource(R.string.goal_target),
                    value = formatKg(goalProgress.targetWeightInKg),
                )
                MetricPill(
                    label = stringResource(R.string.goal_progress),
                    value = stringResource(R.string.value_percent, goalProgress.progressPercent),
                )
                MetricPill(
                    label = stringResource(R.string.goal_remaining),
                    value = formatKg(goalProgress.remainingInKg.absoluteValue),
                )
            }
            Text(
                text = stringResource(
                    R.string.goal_progress_summary,
                    formatNumber(goalProgress.startWeightInKg),
                    formatNumber(goalProgress.currentWeightInKg),
                    formatNumber(goalProgress.achievedInKg.absoluteValue),
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun goalStatusText(goalProgress: GoalProgress): String =
    when (goalProgress.status) {
        GoalStatus.REACHED -> stringResource(
            R.string.goal_status_reached,
            formatNumber(goalProgress.currentWeightInKg),
        )
        GoalStatus.NOT_STARTED -> stringResource(R.string.goal_status_not_started)
        GoalStatus.IN_PROGRESS -> when (goalProgress.direction) {
            GoalDirection.LOSE -> stringResource(R.string.goal_status_losing)
            GoalDirection.GAIN -> stringResource(R.string.goal_status_gaining)
            GoalDirection.MAINTAIN -> stringResource(R.string.goal_status_maintaining)
        }
    }
