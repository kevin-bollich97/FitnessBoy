package de.bollich.fitnessboy.ui.overview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.bollich.fitnessboy.R
import de.bollich.fitnessboy.domain.BmiCategory
import de.bollich.fitnessboy.domain.HEALTHY_BMI_MAX
import de.bollich.fitnessboy.domain.HEALTHY_BMI_MIN
import de.bollich.fitnessboy.domain.OPTIMAL_BMI
import de.bollich.fitnessboy.domain.WeightRange
import de.bollich.fitnessboy.format.formatNumber
import de.bollich.fitnessboy.ui.components.formatCm
import de.bollich.fitnessboy.ui.components.formatKg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BmiInfoSheet(
    bmi: Double,
    bmiCategory: BmiCategory,
    heightInCm: Double,
    healthyWeightRange: WeightRange?,
    optimalWeightInKg: Double?,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = stringResource(R.string.bmi_info_title),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = stringResource(R.string.bmi_info_text),
                style = MaterialTheme.typography.bodyLarge,
            )
            Text(
                text = stringResource(R.string.bmi_info_your_value, formatNumber(bmi)),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            BmiCategoryTable(currentCategory = bmiCategory)
            healthyWeightRange?.let {
                InfoBlock(
                    title = stringResource(R.string.bmi_info_healthy_range_title),
                    value = stringResource(
                        R.string.bmi_info_healthy_range_value,
                        formatNumber(it.minimumInKg),
                        formatNumber(it.maximumInKg),
                    ),
                    description = stringResource(
                        R.string.bmi_info_healthy_range_description,
                        formatNumber(HEALTHY_BMI_MIN),
                        formatNumber(HEALTHY_BMI_MAX),
                        formatCm(heightInCm),
                    ),
                )
            }
            optimalWeightInKg?.let {
                InfoBlock(
                    title = stringResource(R.string.bmi_info_optimal_weight_title),
                    value = formatKg(it),
                    description = stringResource(R.string.bmi_info_optimal_weight_description, formatNumber(OPTIMAL_BMI)),
                )
            }
        }
    }
}

@Composable
private fun BmiCategoryTable(currentCategory: BmiCategory) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = stringResource(R.string.bmi_info_categories_title),
            style = MaterialTheme.typography.titleSmall,
        )
        BmiCategory.entries.forEach { category ->
            BmiCategoryRow(category = category, isCurrent = category == currentCategory)
        }
    }
}

@Composable
private fun BmiCategoryRow(category: BmiCategory, isCurrent: Boolean) {
    val currentDescription = stringResource(R.string.bmi_info_current_category)
    Surface(
        color = if (isCurrent) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .fillMaxWidth()
            .semantics { if (isCurrent) stateDescription = currentDescription },
    ) {
        FlowRow(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(category.labelRes),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                color = if (isCurrent) category.color() else MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = category.rangeText(),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun InfoBlock(title: String, value: String, description: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleSmall)
        Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
