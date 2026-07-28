package com.example.proyectdeswear.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme

private val WearColorPalette = Colors(
    primary = Color(0xFF00A200),
    primaryVariant = Color(0xFF00C853),
    secondary = Color(0xFF00C853),
    secondaryVariant = Color(0xFF00A200),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    error = Color(0xFFD32F2F),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color.White
)

@Composable
fun ProyectoDesDisIntTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = WearColorPalette,
        content = content
    )
}
