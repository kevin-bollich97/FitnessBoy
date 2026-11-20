package de.bollich.fitnessboy.ui.bmi

import androidx.annotation.StringRes
import de.bollich.fitnessboy.R
import de.bollich.fitnessboy.domain.BmiCategory

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
