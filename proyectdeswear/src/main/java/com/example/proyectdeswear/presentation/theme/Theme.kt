package com.example.proyectdeswear.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Colors

private val MindsAIColors = Colors(
    primary = Color(0xFF6C2CFF),
    primaryVariant = Color(0xFF5520D6),
    secondary = Color(0xFFC7B6FF),
    secondaryVariant = Color(0xFFA98CFF),

    background = Color(0xFFF7F7FC),
    surface = Color(0xFFFFFFFF),

    error = Color(0xFFD32F2F),

    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFF1F1F24),
    onBackground = Color(0xFF1F1F24),
    onSurface = Color(0xFF1F1F24),
    onError = Color(0xFFFFFFFF)
)

@Composable
fun ProyectoDesDisIntTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = MindsAIColors,
        content = content
    )
}
