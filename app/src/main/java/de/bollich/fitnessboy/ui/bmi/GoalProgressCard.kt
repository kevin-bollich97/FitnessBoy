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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.bollich.fitnessboy.domain.GoalDirection
import de.bollich.fitnessboy.domain.GoalProgress
import de.bollich.fitnessboy.domain.GoalStatus
import de.bollich.fitnessboy.format.formatNumber
import de.bollich.fitnessboy.ui.components.MetricPill
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
                text = "Zielverfolgung",
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
                    label = "Ziel",
                    value = "${formatNumber(goalProgress.targetWeightInKg)} kg",
                )
                MetricPill(
                    label = "Fortschritt",
                    value = "${goalProgress.progressPercent} %",
                )
                MetricPill(
                    label = "Verbleibend",
                    value = "${formatNumber(goalProgress.remainingInKg.absoluteValue)} kg",
                )
            }
            Text(
                text = "Start: ${formatNumber(goalProgress.startWeightInKg)} kg, aktuell: ${formatNumber(goalProgress.currentWeightInKg)} kg, geschafft: ${formatNumber(goalProgress.achievedInKg.absoluteValue)} kg.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun goalStatusText(goalProgress: GoalProgress): String =
    when (goalProgress.status) {
        GoalStatus.REACHED -> "Ziel erreicht. Du bist bei ${formatNumber(goalProgress.currentWeightInKg)} kg angekommen."
        GoalStatus.NOT_STARTED -> "Noch keine Zielverfolgung aktiv."
        GoalStatus.IN_PROGRESS -> when (goalProgress.direction) {
            GoalDirection.LOSE -> "Du arbeitest auf eine Gewichtsabnahme hin."
            GoalDirection.GAIN -> "Du arbeitest auf eine Gewichtszunahme hin."
            GoalDirection.MAINTAIN -> "Du hältst dein aktuelles Gewicht als Ziel."
        }
    }
