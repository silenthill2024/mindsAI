package com.example.proyectodesdisint.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LetterAvatar(
    name: String,
    size: Dp = 45.dp,
    modifier: Modifier = Modifier
) {
    val initial = if (name.isNotBlank()) name.first().uppercase() else "?"
    
    // Generar un color basado en la inicial para que sea consistente
    val backgroundColor = rememberColorForLetter(initial.toString())

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        backgroundColor,
                        backgroundColor.copy(alpha = 0.7f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initial.toString(),
            color = Color.White,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = (size.value * 0.45).sp
            )
        )
    }
}

@Composable
private fun rememberColorForLetter(letter: String): Color {
    val colors = listOf(
        Color(0xFF6231FF), // MindsAI Purple
        Color(0xFF00BFA5), // Teal
        Color(0xFFFF4B66), // Pink/Red
        Color(0xFF2196F3), // Blue
        Color(0xFFFFC107), // Amber
        Color(0xFF9C27B0), // Purple
        Color(0xFF4CAF50)  // Green
    )
    val index = if (letter.isNotEmpty()) {
        letter.first().code % colors.size
    } else 0
    return colors[index]
}
