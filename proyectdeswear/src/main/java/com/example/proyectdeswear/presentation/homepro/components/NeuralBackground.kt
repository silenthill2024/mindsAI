package com.example.proyectdeswear.presentation.homepro.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.proyectdeswear.presentation.homepro.MindsAIColors

@Composable
fun NeuralBackground(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        drawCircle(
            color = MindsAIColors.Purple.copy(alpha = 0.10f),
            radius = size.minDimension * 0.48f,
            center = Offset(
                size.width * 0.18f,
                size.height * 0.28f
            )
        )

        drawCircle(
            color = MindsAIColors.Cyan.copy(alpha = 0.07f),
            radius = size.minDimension * 0.45f,
            center = Offset(
                size.width * 0.83f,
                size.height * 0.70f
            )
        )

        val points = listOf(
            Offset(size.width * 0.12f, size.height * 0.25f),
            Offset(size.width * 0.27f, size.height * 0.17f),
            Offset(size.width * 0.40f, size.height * 0.30f),
            Offset(size.width * 0.60f, size.height * 0.18f),
            Offset(size.width * 0.78f, size.height * 0.29f),
            Offset(size.width * 0.86f, size.height * 0.50f),
            Offset(size.width * 0.68f, size.height * 0.60f),
            Offset(size.width * 0.48f, size.height * 0.52f),
            Offset(size.width * 0.26f, size.height * 0.62f),
            Offset(size.width * 0.13f, size.height * 0.76f),
            Offset(size.width * 0.42f, size.height * 0.82f),
            Offset(size.width * 0.72f, size.height * 0.80f)
        )

        val connections = listOf(
            0 to 1,
            1 to 2,
            2 to 3,
            3 to 4,
            4 to 5,
            5 to 6,
            6 to 7,
            7 to 8,
            8 to 9,
            9 to 10,
            10 to 11,
            11 to 6,
            2 to 7,
            3 to 7,
            7 to 10
        )

        connections.forEach { pair ->
            drawLine(
                color = MindsAIColors.Purple.copy(alpha = 0.18f),
                start = points[pair.first],
                end = points[pair.second],
                strokeWidth = 1f
            )
        }

        points.forEachIndexed { index, point ->
            drawCircle(
                color = if (index % 2 == 0) {
                    MindsAIColors.Purple
                } else {
                    MindsAIColors.Cyan
                },
                radius = 3.5f,
                center = point,
                alpha = 0.45f
            )
        }
    }
}
