package com.example.proyectdeswear.presentation.homepro.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import com.example.proyectdeswear.presentation.homepro.MindsAIColors

@Composable
fun AcademicIndicator(
    title: String,
    value: Int,
    color: Color,
    type: IndicatorType,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .background(
                    color.copy(alpha = 0.14f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            IndicatorIcon(
                type = type,
                color = color,
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text = title,
            color = MindsAIColors.TextPrimary,
            fontSize = 8.sp,
            textAlign = TextAlign.Center,
            maxLines = 1
        )

        Text(
            text = "$value%",
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

enum class IndicatorType {
    CLASSES,
    RESEARCH
}

@Composable
private fun IndicatorIcon(
    type: IndicatorType,
    color: Color,
    modifier: Modifier
) {
    Canvas(modifier = modifier) {
        when (type) {
            IndicatorType.CLASSES -> {
                drawLine(
                    color = color,
                    start = Offset(size.width * 0.12f, size.height * 0.40f),
                    end = Offset(size.width * 0.50f, size.height * 0.18f),
                    strokeWidth = 2.dp.toPx()
                )

                drawLine(
                    color = color,
                    start = Offset(size.width * 0.50f, size.height * 0.18f),
                    end = Offset(size.width * 0.88f, size.height * 0.40f),
                    strokeWidth = 2.dp.toPx()
                )

                drawLine(
                    color = color,
                    start = Offset(size.width * 0.12f, size.height * 0.40f),
                    end = Offset(size.width * 0.50f, size.height * 0.62f),
                    strokeWidth = 2.dp.toPx()
                )

                drawLine(
                    color = color,
                    start = Offset(size.width * 0.88f, size.height * 0.40f),
                    end = Offset(size.width * 0.50f, size.height * 0.62f),
                    strokeWidth = 2.dp.toPx()
                )

                drawLine(
                    color = color,
                    start = Offset(size.width * 0.76f, size.height * 0.42f),
                    end = Offset(size.width * 0.76f, size.height * 0.78f),
                    strokeWidth = 2.dp.toPx()
                )
            }

            IndicatorType.RESEARCH -> {
                drawCircle(
                    color = color,
                    radius = size.minDimension * 0.24f,
                    center = Offset(
                        size.width * 0.43f,
                        size.height * 0.42f
                    ),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(
                        width = 2.dp.toPx()
                    )
                )

                drawLine(
                    color = color,
                    start = Offset(size.width * 0.60f, size.height * 0.60f),
                    end = Offset(size.width * 0.86f, size.height * 0.86f),
                    strokeWidth = 3.dp.toPx()
                )
            }
        }
    }
}
