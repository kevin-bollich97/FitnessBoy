package de.bollich.fitnessboy.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import de.bollich.fitnessboy.R

/** Nunito, a rounded typeface (variable font, SIL Open Font License, see `licenses/`). */
private val Nunito = FontFamily(
    listOf(
        FontWeight.Normal,
        FontWeight.Medium,
        FontWeight.SemiBold,
        FontWeight.Bold,
        FontWeight.ExtraBold,
        FontWeight.Black,
    ).map { weight ->
        Font(
            resId = R.font.nunito,
            weight = weight,
            variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)),
        )
    }
)

private val Default = Typography()

private fun TextStyle.nunito(weight: FontWeight) = copy(fontFamily = Nunito, fontWeight = weight)

val Typography = Typography(
    displayLarge = Default.displayLarge.nunito(FontWeight.Black),
    displayMedium = Default.displayMedium.nunito(FontWeight.Black),
    displaySmall = Default.displaySmall.nunito(FontWeight.Black),
    headlineLarge = Default.headlineLarge.nunito(FontWeight.Black),
    headlineMedium = Default.headlineMedium.copy(fontSize = 34.sp, lineHeight = 40.sp).nunito(FontWeight.Black),
    headlineSmall = Default.headlineSmall.nunito(FontWeight.ExtraBold),
    titleLarge = Default.titleLarge.nunito(FontWeight.ExtraBold),
    titleMedium = Default.titleMedium.copy(fontSize = 18.sp, lineHeight = 24.sp).nunito(FontWeight.ExtraBold),
    titleSmall = Default.titleSmall.nunito(FontWeight.Bold),
    bodyLarge = Default.bodyLarge.copy(fontSize = 17.sp).nunito(FontWeight.SemiBold),
    bodyMedium = Default.bodyMedium.copy(fontSize = 15.sp).nunito(FontWeight.SemiBold),
    bodySmall = Default.bodySmall.nunito(FontWeight.SemiBold),
    labelLarge = Default.labelLarge.copy(fontSize = 15.sp).nunito(FontWeight.ExtraBold),
    labelMedium = Default.labelMedium.nunito(FontWeight.Bold),
    labelSmall = Default.labelSmall.nunito(FontWeight.Bold),
)
