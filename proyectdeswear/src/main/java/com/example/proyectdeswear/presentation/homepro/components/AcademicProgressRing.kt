package com.example.proyectdeswear.presentation.homepro.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import com.example.proyectdeswear.presentation.homepro.MindsAIColors

@Composable
fun AcademicProgressRing(
    progress: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(140.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val stroke = 13.dp.toPx()
            val inset = stroke / 2f

            drawArc(
                color = MindsAIColors.CardSecondary,
                startAngle = 130f,
                sweepAngle = 280f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = Size(
                    size.width - stroke,
                    size.height - stroke
                ),
                style = Stroke(
                    width = stroke,
                    cap = StrokeCap.Round
                )
            )

            drawArc(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        MindsAIColors.Purple,
                        MindsAIColors.Lavender,
                        MindsAIColors.Cyan
                    )
                ),
                startAngle = 130f,
                sweepAngle =
                    280f * progress.coerceIn(0f, 1f),
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = Size(
                    size.width - stroke,
                    size.height - stroke
                ),
                style = Stroke(
                    width = stroke,
                    cap = StrokeCap.Round
                )
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Enfoque",
                color = MindsAIColors.TextSecondary,
                fontSize = 8.sp
            )

            Text(
                text = "${(progress * 100).toInt()}%",
                color = MindsAIColors.TextPrimary,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Progreso academico",
                color = MindsAIColors.Cyan,
                fontSize = 8.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
