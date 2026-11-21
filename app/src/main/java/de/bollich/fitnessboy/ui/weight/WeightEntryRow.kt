package de.bollich.fitnessboy.ui.weight

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import de.bollich.fitnessboy.R
import de.bollich.fitnessboy.format.formattedDate

@Composable
internal fun WeightEntryRow(
    item: WeightListItem,
    onDelete: () -> Unit,
) {
    ListItem(
        headlineContent = { Text(weightWithBmi(item)) },
        supportingContent = { Text(item.entry.formattedDate()) },
        trailingContent = {
            IconButton(onClick = onDelete) {
                Icon(
                    painter = painterResource(R.drawable.ic_delete),
                    contentDescription = stringResource(R.string.action_delete_entry_from, item.entry.formattedDate()),
                )
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
    )
}
