package de.bollich.fitnessboy.ui.measurements

import androidx.annotation.StringRes
import de.bollich.fitnessboy.R
import de.bollich.fitnessboy.domain.BodyMeasurement

/** Short name, e.g. "Bauch". */
@get:StringRes
val BodyMeasurement.labelRes: Int
    get() = when (this) {
        BodyMeasurement.WAIST -> R.string.measurements_waist
        BodyMeasurement.HIPS -> R.string.measurements_hips
        BodyMeasurement.SHOULDERS -> R.string.measurements_shoulders
        BodyMeasurement.UPPER_ARM -> R.string.measurements_upper_arm
        BodyMeasurement.CHEST -> R.string.measurements_chest
        BodyMeasurement.THIGH -> R.string.measurements_thigh
    }

/** Input field label, e.g. "Bauchumfang in cm". */
@get:StringRes
val BodyMeasurement.inputLabelRes: Int
    get() = when (this) {
        BodyMeasurement.WAIST -> R.string.measurements_waist_label
        BodyMeasurement.HIPS -> R.string.measurements_hips_label
        BodyMeasurement.SHOULDERS -> R.string.measurements_shoulders_label
        BodyMeasurement.UPPER_ARM -> R.string.measurements_upper_arm_label
        BodyMeasurement.CHEST -> R.string.measurements_chest_label
        BodyMeasurement.THIGH -> R.string.measurements_thigh_label
    }
