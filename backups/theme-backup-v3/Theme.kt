package com.example.proyectodesdisint.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = MindsAIPurplePrimary,
    onPrimary = Color.White,
    primaryContainer = MindsAIPurpleDark,
    onPrimaryContainer = Color.White,
    secondary = MindsAIPurpleSecondary,
    background = DarkBackground,
    surface = DarkSurface,
    onSurface = OnDarkSurface
)

private val LightColorScheme = lightColorScheme(
    primary = MindsAIPurplePrimary,
    onPrimary = Color.White,
    primaryContainer = MindsAIPurpleLight,
    onPrimaryContainer = MindsAIPurpleDark,
    secondary = MindsAIPurpleSecondary,
    background = Color(0xFFF8F9FF),
    surface = Color.White,
    onSurface = Color(0xFF1C1B1F)
)

@Composable
fun ProyectoDesDisIntTheme(
    darkTheme: Boolean = ThemeState.isDarkTheme,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
