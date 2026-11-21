package de.bollich.fitnessboy.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Sky,
    onPrimary = Midnight,
    primaryContainer = SteelBlue,
    onPrimaryContainer = Snow,
    secondary = Powder,
    onSecondary = DeepNavy,
    secondaryContainer = Ocean,
    onSecondaryContainer = Fog,
    tertiary = Color(0xFF7E9BB8),
    onTertiary = Midnight,
    background = Midnight,
    onBackground = Snow,
    surface = DeepNavy,
    onSurface = Snow,
    surfaceVariant = ColorTokens.darkSurfaceVariant,
    onSurfaceVariant = Pebble,
    surfaceContainerLowest = ColorTokens.darkSurfaceContainerLowest,
    surfaceContainerLow = ColorTokens.darkSurfaceContainerLow,
    surfaceContainer = ColorTokens.darkSurfaceContainer,
    surfaceContainerHigh = ColorTokens.darkSurfaceContainerHigh,
    surfaceContainerHighest = ColorTokens.darkSurfaceContainerHighest,
    outline = ColorTokens.darkOutline,
    error = Danger,
)

private val LightColorScheme = lightColorScheme(
    primary = SteelBlue,
    onPrimary = Snow,
    primaryContainer = ColorTokens.lightPrimaryContainer,
    onPrimaryContainer = SteelBlue,
    secondary = Ocean,
    onSecondary = Snow,
    secondaryContainer = ColorTokens.lightSecondaryContainer,
    onSecondaryContainer = Ocean,
    tertiary = Color(0xFF8FA8C0),
    onTertiary = Snow,
    background = Snow,
    onBackground = Graphite,
    surface = ColorTokens.lightSurface,
    onSurface = Graphite,
    surfaceVariant = Fog,
    onSurfaceVariant = Mist,
    surfaceContainerLowest = ColorTokens.lightSurfaceContainerLowest,
    surfaceContainerLow = ColorTokens.lightSurfaceContainerLow,
    surfaceContainer = ColorTokens.lightSurfaceContainer,
    surfaceContainerHigh = ColorTokens.lightSurfaceContainerHigh,
    surfaceContainerHighest = ColorTokens.lightSurfaceContainerHighest,
    outline = ColorTokens.lightOutline,
    error = Danger,
)

@Composable
fun FitnessBoyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}

private object ColorTokens {
    val lightSurface = Color(0xFFFFFFFF)
    val lightPrimaryContainer = Ice
    val lightSecondaryContainer = Color(0xFFD0E2F4)
    val lightOutline = Color(0xFFD2DDE8)
    val darkSurfaceVariant = Color(0xFF1B2A36)
    val darkOutline = Color(0xFF445767)

    // Surface container roles (used by NavigationBar, cards, sheets), tinted to the blue palette.
    val lightSurfaceContainerLowest = Color(0xFFFFFFFF)
    val lightSurfaceContainerLow = Color(0xFFF5F8FB)
    val lightSurfaceContainer = Color(0xFFEEF2F7)
    val lightSurfaceContainerHigh = Color(0xFFE8EDF3)
    val lightSurfaceContainerHighest = Color(0xFFE2E8EF)
    val darkSurfaceContainerLowest = Color(0xFF0A1016)
    val darkSurfaceContainerLow = Color(0xFF131C25)
    val darkSurfaceContainer = Color(0xFF17222C)
    val darkSurfaceContainerHigh = Color(0xFF1E2A35)
    val darkSurfaceContainerHighest = Color(0xFF26333F)
}
