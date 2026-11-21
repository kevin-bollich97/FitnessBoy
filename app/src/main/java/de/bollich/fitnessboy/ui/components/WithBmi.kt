package de.bollich.fitnessboy.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import de.bollich.fitnessboy.R
import de.bollich.fitnessboy.format.formatNumber

/** "[text] · BMI 40,8", or just [text] if the BMI is unknown. */
@Composable
fun withBmi(text: String, bmi: Double?): String =
    bmi?.let { stringResource(R.string.value_with_bmi, text, formatNumber(it)) } ?: text
