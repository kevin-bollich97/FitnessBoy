package de.bollich.fitnessboy.ui.components

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import de.bollich.fitnessboy.R

/** Number of newest entries a list shows before it is expanded. */
const val COLLAPSED_ENTRY_COUNT = 3

/** Expands a shortened list to all [totalCount] entries, or collapses it again. */
@Composable
fun ShowAllButton(
    isExpanded: Boolean,
    totalCount: Int,
    onToggle: () -> Unit,
) {
    TextButton(onClick = onToggle) {
        Text(
            if (isExpanded) {
                stringResource(R.string.action_show_less)
            } else {
                pluralStringResource(R.plurals.action_show_all_entries, totalCount, totalCount)
            }
        )
    }
}
