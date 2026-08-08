package com.example.proyectdeswear.presentation.homev2

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeV2Header(
    notificationCount: Int,
    onNotificationsClick: () -> Unit
) {
    var now by remember {
        mutableStateOf(Date())
    }

    LaunchedEffect(Unit) {
        while (true) {
            now = Date()
            delay(30_000)
        }
    }

    val timeFormatter = remember {
        SimpleDateFormat(
            "HH:mm",
            Locale.getDefault()
        )
    }

    val dateFormatter = remember {
        SimpleDateFormat(
            "EEE, d MMM",
            Locale.getDefault()
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 26.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = timeFormatter.format(now),
                color = HomeV2Colors.TextPrimary,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1
            )

            Text(
                text = dateFormatter
                    .format(now)
                    .replaceFirstChar {
                        if (it.isLowerCase()) {
                            it.titlecase(Locale.getDefault())
                        } else {
                            it.toString()
                        }
                    },
                color = HomeV2Colors.TextSecondary,
                fontSize = 8.sp,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(38.dp)
                .background(
                    HomeV2Colors.SurfaceLight,
                    CircleShape
                )
                .clickable(
                    interactionSource = remember {
                        MutableInteractionSource()
                    },
                    indication = null,
                    onClick = onNotificationsClick
                ),
            contentAlignment = Alignment.Center
        ) {
            HomeV2BellIcon(
                modifier = Modifier.size(19.dp)
            )

            if (notificationCount > 0) {
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.TopEnd)
                        .background(
                            HomeV2Colors.Red,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = notificationCount
                            .coerceAtMost(9)
                            .toString(),
                        color = HomeV2Colors.TextPrimary,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeV2BellIcon(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val strokeWidth = 1.7.dp.toPx()

        val path = Path().apply {
            moveTo(
                size.width * 0.50f,
                size.height * 0.13f
            )

            cubicTo(
                size.width * 0.31f,
                size.height * 0.13f,
                size.width * 0.25f,
                size.height * 0.30f,
                size.width * 0.25f,
                size.height * 0.48f
            )

            lineTo(
                size.width * 0.25f,
                size.height * 0.63f
            )

            cubicTo(
                size.width * 0.25f,
                size.height * 0.69f,
                size.width * 0.18f,
                size.height * 0.76f,
                size.width * 0.14f,
                size.height * 0.81f
            )

            lineTo(
                size.width * 0.86f,
                size.height * 0.81f
            )

            cubicTo(
                size.width * 0.82f,
                size.height * 0.76f,
                size.width * 0.75f,
                size.height * 0.69f,
                size.width * 0.75f,
                size.height * 0.63f
            )

            lineTo(
                size.width * 0.75f,
                size.height * 0.48f
            )

            cubicTo(
                size.width * 0.75f,
                size.height * 0.30f,
                size.width * 0.69f,
                size.height * 0.13f,
                size.width * 0.50f,
                size.height * 0.13f
            )

            close()
        }

        drawPath(
            path = path,
            color = HomeV2Colors.TextPrimary,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round
            )
        )

        drawCircle(
            color = HomeV2Colors.TextPrimary,
            radius = size.width * 0.065f,
            center = Offset(
                x = size.width * 0.50f,
                y = size.height * 0.90f
            )
        )
    }
}
