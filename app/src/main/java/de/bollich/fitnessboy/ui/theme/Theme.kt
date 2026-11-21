package de.bollich.fitnessboy.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = AzureLight,
    onPrimary = Color(0xFF0A2A66),
    primaryContainer = AzureDeep,
    onPrimaryContainer = BabyBlueLight,
    secondary = SlateLight,
    onSecondary = Color(0xFF1E2B40),
    secondaryContainer = SlateDark,
    onSecondaryContainer = Cloud,
    tertiary = SlateLight,
    onTertiary = Color(0xFF1E2B40),
    tertiaryContainer = SlateDark,
    onTertiaryContainer = Cloud,
    background = Abyss,
    onBackground = Mist,
    surface = Abyss,
    onSurface = Mist,
    surfaceVariant = Color(0xFF2A3342),
    onSurfaceVariant = Haze,
    surfaceContainerLowest = Color(0xFF0B0F16),
    surfaceContainerLow = Color(0xFF18202C),
    surfaceContainer = Color(0xFF1C2431),
    surfaceContainerHigh = Color(0xFF252E3C),
    surfaceContainerHighest = Color(0xFF303A49),
    outline = Color(0xFF8592A6),
    outlineVariant = Color(0xFF3D4757),
    error = CherryLight,
)

private val LightColorScheme = lightColorScheme(
    primary = Azure,
    onPrimary = Color.White,
    primaryContainer = BabyBlue,
    onPrimaryContainer = Navy,
    secondary = Slate,
    onSecondary = Color.White,
    secondaryContainer = Cloud,
    onSecondaryContainer = SlateDeep,
    tertiary = Slate,
    onTertiary = Color.White,
    tertiaryContainer = Cloud,
    onTertiaryContainer = SlateDeep,
    background = Frost,
    onBackground = Ink,
    surface = Frost,
    onSurface = Ink,
    surfaceVariant = Color(0xFFEBEFF6),
    onSurfaceVariant = Pewter,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFFDFEFF),
    surfaceContainer = Color(0xFFEFF3F9),
    surfaceContainerHigh = Color(0xFFE9EEF6),
    surfaceContainerHighest = Color(0xFFE2E8F1),
    outline = Color(0xFFC3CCDA),
    outlineVariant = Color(0xFFE3E8F0),
    error = Cherry,
)

/** Signal colors the Material color scheme has no role for. */
@Immutable
data class ExtendedColors(
    val success: Color,
    val warning: Color,
)

private val LightExtendedColors = ExtendedColors(success = Forest, warning = Amber)

private val DarkExtendedColors = ExtendedColors(success = Sage, warning = AmberLight)

private val LocalExtendedColors = staticCompositionLocalOf { LightExtendedColors }

@Composable
fun FitnessBoyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalExtendedColors provides if (darkTheme) DarkExtendedColors else LightExtendedColors) {
        MaterialTheme(
            colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
            typography = Typography,
            shapes = Shapes,
            content = content,
        )
    }
}

object FitnessBoyTheme {
    val extendedColors: ExtendedColors
        @Composable get() = LocalExtendedColors.current
}
