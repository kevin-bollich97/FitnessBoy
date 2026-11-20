package de.bollich.fitnessboy.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import de.bollich.fitnessboy.R
import de.bollich.fitnessboy.format.formatNumber

@Composable
fun formatKg(valueInKg: Double): String = stringResource(R.string.value_kg, formatNumber(valueInKg))

@Composable
fun formatCm(valueInCm: Double): String = stringResource(R.string.value_cm, formatNumber(valueInCm))
