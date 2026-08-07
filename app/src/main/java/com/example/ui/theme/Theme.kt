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
import androidx.compose.ui.graphics.Color

private val ElegantDarkColorScheme =
  darkColorScheme(
    primary = PrimaryBlue,
    onPrimary = OnPrimaryBlue,
    primaryContainer = PrimaryContainerBlue,
    onPrimaryContainer = OnPrimaryContainerBlue,
    secondary = TealAccent,
    background = DarkBackground,
    onBackground = TextLight,
    surface = DarkSurface,
    onSurface = TextLight,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextMuted,
    outline = DarkOutline
  )

@Composable
fun MyApplicationTheme(
  forceElegantDark: Boolean = true,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val systemDark = isSystemInDarkTheme()
  val effectiveDark = if (forceElegantDark) true else systemDark

  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (effectiveDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }
      effectiveDark -> ElegantDarkColorScheme
      else -> lightColorScheme(
        primary = PrimaryBlue,
        onPrimary = OnPrimaryBlue,
        primaryContainer = PrimaryContainerBlue,
        onPrimaryContainer = OnPrimaryContainerBlue,
        secondary = TealAccent,
        background = Color(0xFFF8F9FA),
        onBackground = Color(0xFF1A1C1E),
        surface = Color(0xFFFFFFFF),
        onSurface = Color(0xFF1A1C1E),
        surfaceVariant = Color(0xFFE1E2EC),
        onSurfaceVariant = Color(0xFF44474F),
        outline = Color(0xFF74777F)
      )
    }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
