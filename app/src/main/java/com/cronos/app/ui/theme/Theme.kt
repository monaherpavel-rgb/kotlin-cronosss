package com.cronos.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = CronosAccent,
    onPrimary = CronosBlack,
    primaryContainer = CronosAccentVariant,
    onPrimaryContainer = CronosTextPrimary,
    secondary = CronosLightGray,
    onSecondary = CronosTextPrimary,
    background = CronosBlack,
    onBackground = CronosTextPrimary,
    surface = CronosDarkGray,
    onSurface = CronosTextPrimary,
    surfaceVariant = CronosGray,
    onSurfaceVariant = CronosTextSecondary,
    error = CronosError,
    onError = CronosTextPrimary
)

@Composable
fun CronosTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
