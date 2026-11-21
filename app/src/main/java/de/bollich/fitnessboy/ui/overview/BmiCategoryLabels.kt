package de.bollich.fitnessboy.ui.overview

import androidx.annotation.StringRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import de.bollich.fitnessboy.R
import de.bollich.fitnessboy.domain.BmiCategory
import de.bollich.fitnessboy.format.formatNumber
import de.bollich.fitnessboy.ui.theme.FitnessBoyTheme

@get:StringRes
val BmiCategory.labelRes: Int
    get() = when (this) {
        BmiCategory.UNDERWEIGHT -> R.string.bmi_category_underweight
        BmiCategory.NORMAL_WEIGHT -> R.string.bmi_category_normal_weight
        BmiCategory.OVERWEIGHT -> R.string.bmi_category_overweight
        BmiCategory.OBESITY_CLASS_I -> R.string.bmi_category_obesity_class_1
        BmiCategory.OBESITY_CLASS_II -> R.string.bmi_category_obesity_class_2
        BmiCategory.OBESITY_CLASS_III -> R.string.bmi_category_obesity_class_3
    }

/** BMI range of the category, e.g. "18,5 bis unter 25". */
@Composable
fun BmiCategory.rangeText(): String {
    val lower = lowerBound
    val upper = upperBound
    return when {
        lower == null -> stringResource(R.string.bmi_range_below, formatNumber(checkNotNull(upper)))
        upper == null -> stringResource(R.string.bmi_range_from, formatNumber(lower))
        else -> stringResource(R.string.bmi_range_between, formatNumber(lower), formatNumber(upper))
    }
}

/** Text color that signals how far the category is from normal weight. */
@Composable
fun BmiCategory.color(): Color {
    val colorScheme = MaterialTheme.colorScheme
    return when (this) {
        BmiCategory.NORMAL_WEIGHT -> FitnessBoyTheme.extendedColors.success
        BmiCategory.UNDERWEIGHT,
        BmiCategory.OVERWEIGHT -> FitnessBoyTheme.extendedColors.warning
        BmiCategory.OBESITY_CLASS_I,
        BmiCategory.OBESITY_CLASS_II,
        BmiCategory.OBESITY_CLASS_III -> colorScheme.error
    }
}
