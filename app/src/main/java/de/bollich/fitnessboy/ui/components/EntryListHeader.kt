package de.bollich.fitnessboy.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import de.bollich.fitnessboy.R

/**
 * Title above an entry list. While the list is expanded it also offers to collapse it, so a long
 * list need not be scrolled to its end for that.
 */
@Composable
fun EntryListHeader(
    title: String,
    isExpanded: Boolean,
    onCollapse: () -> Unit,
) {
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        itemVerticalAlignment = Alignment.CenterVertically,
    ) {
        // As tall as the button, so the list does not shift when the button appears.
        Text(
            text = title,
            modifier = Modifier
                .heightIn(min = TITLE_MIN_HEIGHT)
                .wrapContentHeight(),
            style = MaterialTheme.typography.titleMedium,
        )
        if (isExpanded) {
            TextButton(onClick = onCollapse) {
                Text(stringResource(R.string.action_show_less))
            }
        }
    }
}

/** Height of the collapse button including its touch target. */
private val TITLE_MIN_HEIGHT = 48.dp
