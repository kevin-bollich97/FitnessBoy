package de.bollich.fitnessboy.ui.overview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.bollich.fitnessboy.R
import de.bollich.fitnessboy.domain.GoalProgress
import de.bollich.fitnessboy.domain.GoalStatus
import de.bollich.fitnessboy.ui.components.formatKg
import kotlin.math.absoluteValue

@Composable
internal fun GoalCard(goalProgress: GoalProgress) {
    OverviewCard(
        title = stringResource(R.string.overview_goal_title, formatKg(goalProgress.targetWeightInKg)),
        emojiRes = R.string.emoji_goal,
        onClick = null,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LinearProgressIndicator(
                progress = { goalProgress.progressPercent / 100f },
                modifier = Modifier
                    .weight(1f)
                    .height(12.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.primaryContainer,
                gapSize = 0.dp,
                drawStopIndicator = {},
            )
            Text(
                text = stringResource(R.string.value_percent, goalProgress.progressPercent),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }
        Text(
            text = goalStatusText(goalProgress),
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
internal fun MissingGoalCard(onClick: () -> Unit) {
    OverviewCard(
        title = stringResource(R.string.overview_goal_missing_title),
        emojiRes = R.string.emoji_goal,
        onClick = onClick,
    ) {
        Text(
            text = stringResource(R.string.overview_goal_missing),
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@Composable
private fun goalStatusText(goalProgress: GoalProgress): String =
    if (goalProgress.status == GoalStatus.REACHED) {
        stringResource(R.string.overview_goal_reached, formatKg(goalProgress.currentWeightInKg))
    } else {
        stringResource(
            R.string.overview_goal_remaining,
            formatKg(goalProgress.remainingInKg.absoluteValue),
            formatKg(goalProgress.achievedInKg.absoluteValue),
        )
    }
