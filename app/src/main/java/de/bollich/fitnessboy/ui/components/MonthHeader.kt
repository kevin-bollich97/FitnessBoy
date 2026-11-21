package de.bollich.fitnessboy.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import de.bollich.fitnessboy.R
import de.bollich.fitnessboy.format.formattedMonth

/** Opaque month header for lists, e.g. "November 2026 · 5 Einträge"; stays readable when sticky. */
@Composable
fun MonthHeader(section: MonthSection<*>) {
    Surface(color = MaterialTheme.colorScheme.background, modifier = Modifier.fillMaxWidth()) {
        Text(
            text = stringResource(
                R.string.list_month_header,
                section.month.formattedMonth(),
                pluralStringResource(R.plurals.entry_count, section.totalCount, section.totalCount),
            ),
            modifier = Modifier
                .padding(vertical = 8.dp)
                .semantics { heading() },
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}
