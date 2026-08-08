package com.example.proyectdeswear.presentation.homepro.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import com.example.proyectdeswear.presentation.homepro.MindsAIColors

@Composable
fun NeuralRecommendationCard(
    recommendation: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp)
            .background(
                MindsAIColors.Card.copy(alpha = 0.94f),
                RoundedCornerShape(24.dp)
            )
            .padding(
                horizontal = 14.dp,
                vertical = 11.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Red Neuronal",
                color = MindsAIColors.Purple,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = recommendation,
                color = MindsAIColors.TextPrimary,
                fontSize = 9.sp,
                textAlign = TextAlign.Start,
                maxLines = 3
            )
        }

        NeuralGraph(
            modifier = Modifier.size(57.dp)
        )
    }
}

@Composable
private fun NeuralGraph(
    modifier: Modifier
) {
    Canvas(modifier = modifier) {
        val points = listOf(
            Offset(size.width * 0.18f, size.height * 0.25f),
            Offset(size.width * 0.44f, size.height * 0.14f),
            Offset(size.width * 0.76f, size.height * 0.25f),
            Offset(size.width * 0.30f, size.height * 0.55f),
            Offset(size.width * 0.62f, size.height * 0.50f),
            Offset(size.width * 0.82f, size.height * 0.72f),
            Offset(size.width * 0.45f, size.height * 0.84f)
        )

        val links = listOf(
            0 to 1,
            1 to 2,
            0 to 3,
            1 to 3,
            1 to 4,
            2 to 4,
            3 to 4,
            4 to 5,
            3 to 6,
            4 to 6,
            5 to 6
        )

        links.forEach {
            drawLine(
                color = MindsAIColors.Cyan.copy(alpha = 0.45f),
                start = points[it.first],
                end = points[it.second],
                strokeWidth = 1.2f
            )
        }

        points.forEachIndexed { index, point ->
            drawCircle(
                color = if (index % 2 == 0) {
                    MindsAIColors.Purple
                } else {
                    MindsAIColors.Cyan
                },
                radius = 4f,
                center = point
            )
        }
    }
}
