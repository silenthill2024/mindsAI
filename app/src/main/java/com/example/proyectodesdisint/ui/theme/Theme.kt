package com.example.proyectodesdisint.ui.theme

import androidx.compose.material3.ColorScheme
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

// Esquemas de color para daltonismo (Adaptables a modo Claro y Oscuro)
private fun getColorblindScheme(type: ColorblindType, isDark: Boolean): ColorScheme {
    return when (type) {
        ColorblindType.PROTANOPIA -> if (isDark) {
            darkColorScheme(primary = Color(0xFF0055FF), secondary = Color(0xFFCCCC00), background = MindsBlack, surface = MindsSurface)
        } else {
            lightColorScheme(primary = Color(0xFF0044CC), secondary = Color(0xFFB3B300), background = Color(0xFFF0F4FF), surface = Color.White)
        }
        ColorblindType.DEUTERANOPIA -> if (isDark) {
            darkColorScheme(primary = Color(0xFF0077FF), secondary = Color(0xFFE69F00), background = MindsBlack, surface = MindsSurface)
        } else {
            lightColorScheme(primary = Color(0xFF0055BB), secondary = Color(0xFFCC8B00), background = Color(0xFFF0F7FF), surface = Color.White)
        }
        ColorblindType.TRITANOPIA -> if (isDark) {
            darkColorScheme(primary = Color(0xFFCC0000), secondary = Color(0xFF006666), background = MindsBlack, surface = MindsSurface)
        } else {
            lightColorScheme(primary = Color(0xFFAA0000), secondary = Color(0xFF005555), background = Color(0xFFFFF0F0), surface = Color.White)
        }
        else -> if (isDark) DarkColorScheme else LightColorScheme
    }
}

@Composable
fun ProyectoDesDisIntTheme(
    darkTheme: Boolean = ThemeState.isDarkTheme,
    colorblindType: ColorblindType = ThemeState.colorblindType,
    content: @Composable () -> Unit
) {
    val colorScheme = getColorblindScheme(colorblindType, darkTheme)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

