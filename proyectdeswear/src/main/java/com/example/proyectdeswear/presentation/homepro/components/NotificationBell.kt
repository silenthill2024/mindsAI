package com.example.proyectdeswear.presentation.homepro.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import com.example.proyectdeswear.presentation.homepro.MindsAIColors

@Composable
fun NotificationBell(
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(38.dp)
            .background(
                MindsAIColors.CardSecondary,
                CircleShape
            )
            .clickable(
                interactionSource = remember {
                    MutableInteractionSource()
                },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        BellShape(
            modifier = Modifier.size(18.dp)
        )

        if (count > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(15.dp)
                    .background(
                        MindsAIColors.Purple,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = count.coerceAtMost(9).toString(),
                    color = MindsAIColors.TextPrimary,
                    fontSize = 7.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun BellShape(
    modifier: Modifier
) {
    Canvas(modifier = modifier) {
        val path = Path().apply {
            moveTo(size.width * 0.50f, size.height * 0.12f)

            cubicTo(
                size.width * 0.30f,
                size.height * 0.12f,
                size.width * 0.24f,
                size.height * 0.31f,
                size.width * 0.24f,
                size.height * 0.48f
            )

            lineTo(size.width * 0.24f, size.height * 0.65f)
            lineTo(size.width * 0.14f, size.height * 0.80f)
            lineTo(size.width * 0.86f, size.height * 0.80f)
            lineTo(size.width * 0.76f, size.height * 0.65f)
            lineTo(size.width * 0.76f, size.height * 0.48f)

            cubicTo(
                size.width * 0.76f,
                size.height * 0.31f,
                size.width * 0.70f,
                size.height * 0.12f,
                size.width * 0.50f,
                size.height * 0.12f
            )

            close()
        }

        drawPath(
            path = path,
            color = MindsAIColors.TextPrimary,
            style = Stroke(
                width = 1.6.dp.toPx(),
                cap = StrokeCap.Round
            )
        )

        drawCircle(
            color = MindsAIColors.TextPrimary,
            radius = size.width * 0.06f,
            center = Offset(
                size.width * 0.50f,
                size.height * 0.90f
            )
        )
    }
}
