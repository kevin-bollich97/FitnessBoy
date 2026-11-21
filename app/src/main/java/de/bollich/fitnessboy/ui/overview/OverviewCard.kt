package de.bollich.fitnessboy.ui.overview

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.bollich.fitnessboy.R

/**
 * Dashboard card with an emoji badge and a title. A [highlighted] card is tinted to stand out. If
 * [onClick] is set, the whole card is clickable and shows a chevron.
 */
@Composable
internal fun OverviewCard(
    title: String,
    @StringRes emojiRes: Int,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    highlighted: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    val colorScheme = MaterialTheme.colorScheme
    val colors = if (highlighted) {
        CardDefaults.cardColors(containerColor = colorScheme.primaryContainer, contentColor = colorScheme.onPrimaryContainer)
    } else {
        CardDefaults.cardColors(containerColor = colorScheme.surfaceContainerLow, contentColor = colorScheme.onSurface)
    }
    val badgeColor = if (highlighted) colorScheme.surfaceContainerLowest.copy(alpha = 0.7f) else colorScheme.primaryContainer
    val cardContent: @Composable ColumnScope.() -> Unit = {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                EmojiBadge(emojiRes, badgeColor)
                Text(
                    text = title,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium,
                )
                if (onClick != null) {
                    Icon(painter = painterResource(R.drawable.ic_chevron_right), contentDescription = null)
                }
            }
            content()
        }
    }

    if (onClick != null) {
        Card(onClick = onClick, modifier = modifier.fillMaxWidth(), colors = colors, content = cardContent)
    } else {
        Card(modifier = modifier.fillMaxWidth(), colors = colors, content = cardContent)
    }
}

/** Secondary text inside an overview card, a little lighter than the card's content color. */
internal val overviewSecondaryTextColor: Color
    @Composable get() = LocalContentColor.current.copy(alpha = 0.75f)

/** Decorative emoji on a tinted circle; hidden from screen readers. */
@Composable
private fun EmojiBadge(@StringRes emojiRes: Int, color: Color) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(color, CircleShape)
            .clearAndSetSemantics {},
        contentAlignment = Alignment.Center,
    ) {
        Text(text = stringResource(emojiRes), fontSize = 20.sp)
    }
}
