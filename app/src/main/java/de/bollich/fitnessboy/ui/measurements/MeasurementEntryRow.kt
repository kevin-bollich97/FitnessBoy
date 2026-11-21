package de.bollich.fitnessboy.ui.measurements

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
import de.bollich.fitnessboy.ui.components.withBmi

@Composable
internal fun MeasurementEntryRow(
    item: MeasurementListItem,
    onDelete: () -> Unit,
) {
    val date = item.entry.date.formattedDate()
    ListItem(
        headlineContent = { Text(withBmi(date, item.bmi)) },
        supportingContent = { Text(measuredValuesText(item.entry)) },
        trailingContent = {
            IconButton(onClick = onDelete) {
                Icon(
                    painter = painterResource(R.drawable.ic_delete),
                    contentDescription = stringResource(R.string.action_delete_entry_from, date),
                )
            }
        },
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
    )
}
