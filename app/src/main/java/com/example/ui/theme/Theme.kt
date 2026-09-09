package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
  primary = TerracottaPrimaryDark,
  onPrimary = TerracottaOnPrimaryDark,
  primaryContainer = TerracottaContainerDark,
  onPrimaryContainer = TerracottaOnContainerDark,
  secondary = SecondaryOnContainerDark,
  onSecondary = PaperSurfaceDark,
  secondaryContainer = SecondaryContainerDark,
  onSecondaryContainer = SecondaryOnContainerDark,
  tertiary = GoldTertiaryLight,
  onTertiary = GoldOnTertiaryLight,
  tertiaryContainer = GoldContainerLight,
  onTertiaryContainer = GoldOnContainerLight,
  background = LinenBackgroundDark,
  surface = PaperSurfaceDark,
  surfaceVariant = CardBorderDark,
  onSurface = CharcoalTextDark,
  onSurfaceVariant = CharcoalMutedDark,
  outline = CardBorderDark
)

private val LightColorScheme = lightColorScheme(
  primary = TerracottaPrimaryLight,
  onPrimary = TerracottaOnPrimaryLight,
  primaryContainer = TerracottaContainerLight,
  onPrimaryContainer = TerracottaOnContainerLight,
  secondary = SecondaryWarmEarthLight,
  onSecondary = SecondaryOnEarthLight,
  secondaryContainer = SecondaryContainerLight,
  onSecondaryContainer = SecondaryOnContainerLight,
  tertiary = GoldTertiaryLight,
  onTertiary = GoldOnTertiaryLight,
  tertiaryContainer = GoldContainerLight,
  onTertiaryContainer = GoldOnContainerLight,
  background = LinenBackgroundLight,
  surface = PaperSurfaceLight,
  surfaceVariant = SecondaryContainerLight,
  onSurface = CharcoalTextLight,
  onSurfaceVariant = CharcoalMutedLight,
  outline = CardBorderLight
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // For distinct medical branding, we can use our branded colors by default or dynamic colors if enabled
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    darkTheme -> DarkColorScheme
    else -> LightColorScheme
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
