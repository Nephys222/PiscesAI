/*
 * Copyright 2024 NIlearning
 *
 *  Showcase app for Gemini Pro Implementation with Jetpack Compose
 *
 *  Last modified 11/02/2024, 21:27
 */

package com.nilearning.ai.pisces.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Blue80,
    onPrimary = Color(0xFF002D6F),
    primaryContainer = Color(0xFF00429B),
    onPrimaryContainer = Color(0xFFD9E2FF),
    secondary = BlueGrey80,
    onSecondary = Color(0xFF293042),
    secondaryContainer = Color(0xFF404659) ,
    onSecondaryContainer = Color(0xFFDCE2F9),
    tertiary = Neon80,
    onTertiary = Color(0xFF3D2F00),
    tertiaryContainer = Color(0xFF646402),
    onTertiaryContainer = Color(0xFFFFE08D),
    background = Color(0xFF1B1B1F),
    onBackground = Color(0xFFE3E2E6),
    surface = Color(0xFF1B1B1F),
    onSurface = Color(0xFFE3E2E6),
    surfaceVariant = Color(0xFF44464F),
    onSurfaceVariant = Color(0xFFC5C6D0),
    outline = Color(0xFF8F9099)
)

private val LightColorScheme = lightColorScheme(
    primary = Blue40,
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD9E2FF),
    onPrimaryContainer = Color(0xFF001945),
    secondary = BlueGrey40,
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFDCE2F9) ,
    onSecondaryContainer = Color(0xFF141B2C),
    tertiary = Neon40,
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFB9F474),
    onTertiaryContainer = Color(0xFF0F2000),
    background = Color(0xFFFEFBFF),
    onBackground = Color(0xFF1B1B1F),
    surface = Color(0xFFFEFBFF),
    onSurface = Color(0xFF1B1B1F),
    surfaceVariant = Color(0xFFE1E2EC),
    onSurfaceVariant = Color(0xFF44464F),
    outline = Color(0xFF757780)
)

@Composable
fun GenerativeAISample(
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
