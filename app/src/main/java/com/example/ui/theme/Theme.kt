package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = TerracottaLight,
    onPrimary = Color.White,
    primaryContainer = TerracottaContainer,
    onPrimaryContainer = CreamIvoryText,
    secondary = SageGreenLight,
    onSecondary = Color.White,
    secondaryContainer = SageGreenContainer,
    onSecondaryContainer = CreamIvoryText,
    background = DarkBgBase,
    onBackground = CreamIvoryText,
    surface = DarkGlassSurface,
    onSurface = CreamIvoryText,
    surfaceVariant = DarkGlassSurfaceVariant,
    onSurfaceVariant = CreamIvoryMuted,
    outline = DarkGlassBorder,
    outlineVariant = DarkGlassBorderHighlight
)

private val LightColorScheme = lightColorScheme(
    primary = TerracottaPrimary,
    onPrimary = Color.White,
    primaryContainer = TerracottaContainer,
    onPrimaryContainer = DarkBrownText,
    secondary = SageGreenPrimary,
    onSecondary = Color.White,
    secondaryContainer = SageGreenContainer,
    onSecondaryContainer = DarkBrownText,
    background = LightBgBase,
    onBackground = DarkBrownText,
    surface = LightGlassSurface,
    onSurface = DarkBrownText,
    surfaceVariant = LightGlassSurfaceVariant,
    onSurfaceVariant = DarkBrownMuted,
    outline = LightGlassBorder,
    outlineVariant = LightGlassBorderHighlight
)

@Composable
fun RootChartTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
