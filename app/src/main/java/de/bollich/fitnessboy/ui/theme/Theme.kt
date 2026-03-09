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
    val lightSecondaryContainer = Color(0xFFE8F0F7)
    val lightOutline = Color(0xFFD2DDE8)
    val darkSurfaceVariant = Color(0xFF1B2A36)
    val darkOutline = Color(0xFF445767)
}
