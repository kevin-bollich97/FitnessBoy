package de.bollich.fitnessboy.ui.components

import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Read-only field that opens a menu to pick one of [options]. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> SelectionField(
    label: String,
    options: List<T>,
    selected: T,
    optionLabel: @Composable (T) -> String,
    onSelect: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by rememberSaveable { mutableStateOf(false) }
    val textStyle = MaterialTheme.typography.bodyLarge

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,
    ) {
        OutlinedTextField(
            value = optionLabel(selected),
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            textStyle = textStyle,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(optionLabel(option)) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}

/**
 * A [SelectionField] inside a `FlowRow`: it moves to the next row before its longest option would
 * be cut off (e.g. with large font) and shares free space in proportion to its width.
 */
@Composable
fun <T> FlowRowScope.WrappingSelectionField(
    label: String,
    options: List<T>,
    selected: T,
    optionLabel: @Composable (T) -> String,
    onSelect: (T) -> Unit,
) {
    val minWidth = minFieldWidth(options.map { optionLabel(it) }, MaterialTheme.typography.bodyLarge)
    SelectionField(
        label = label,
        options = options,
        selected = selected,
        optionLabel = optionLabel,
        onSelect = onSelect,
        // width() is what the row uses to decide on wrapping; weight() then stretches the field.
        modifier = Modifier
            .weight(minWidth.value)
            .width(minWidth),
    )
}

/** Width of the widest [texts] plus the field's padding and dropdown icon. */
@Composable
private fun minFieldWidth(texts: List<String>, style: TextStyle): Dp {
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    return remember(texts, style, density) {
        val widestText = texts.maxOf { textMeasurer.measure(it, style).size.width }
        with(density) { widestText.toDp() } + FIELD_DECORATION_WIDTH
    }
}

/** Start padding of the text plus the trailing dropdown icon. */
private val FIELD_DECORATION_WIDTH = 64.dp
