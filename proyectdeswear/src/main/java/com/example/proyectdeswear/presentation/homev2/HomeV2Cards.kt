package com.example.proyectdeswear.presentation.homev2

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import com.example.proyectdeswear.presentation.Task

@Composable
fun HomeV2SummaryCard(
    progress: Float,
    completed: Int,
    pending: Int
) {
    HomeV2CardContainer(
        accent = HomeV2Colors.Purple
    ) {
        HomeV2CardTitle(
            text = "RESUMEN DEL DIA",
            color = HomeV2Colors.Lavender
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier.size(100.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val stroke = 9.dp.toPx()
                val inset = stroke / 2f

                drawArc(
                    color = HomeV2Colors.SurfaceLight,
                    startAngle = -90f,
                    sweepAngle = 360f,
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
                    color = HomeV2Colors.Purple,
                    startAngle = -90f,
                    sweepAngle = 360f *
                        progress.coerceIn(0f, 1f),
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
                horizontalAlignment =
                    Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${(progress * 100).toInt()}%",
                    color = HomeV2Colors.TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Progreso",
                    color = HomeV2Colors.TextSecondary,
                    fontSize = 8.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(7.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceEvenly
        ) {
            HomeV2Metric(
                value = completed.toString(),
                label = "Terminadas",
                color = HomeV2Colors.Green
            )

            HomeV2Metric(
                value = pending.toString(),
                label = "Pendientes",
                color = HomeV2Colors.Orange
            )
        }
    }
}

@Composable
fun HomeV2NextTaskCard(
    task: Task?
) {
    HomeV2CardContainer(
        accent = HomeV2Colors.Cyan
    ) {
        HomeV2CardTitle(
            text = "PROXIMA TAREA",
            color = HomeV2Colors.Cyan
        )

        Spacer(modifier = Modifier.height(13.dp))

        if (task == null) {
            Text(
                text = "Todo listo",
                color = HomeV2Colors.TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "No tienes tareas pendientes",
                color = HomeV2Colors.TextSecondary,
                fontSize = 9.sp,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        } else {
            Text(
                text = task.titulo.ifBlank {
                    "Tarea sin titulo"
                },
                color = HomeV2Colors.TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(7.dp))

            Text(
                text = task.descripcion.ifBlank {
                    "Sin descripcion"
                },
                color = HomeV2Colors.TextSecondary,
                fontSize = 9.sp,
                textAlign = TextAlign.Center,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(11.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceEvenly
            ) {
                HomeV2Information(
                    label = "Fecha",
                    value = shortHomeV2Date(task.fecha)
                )

                HomeV2Information(
                    label = "Hora",
                    value = task.hora.ifBlank {
                        "--:--"
                    }
                )
            }

            Spacer(modifier = Modifier.height(9.dp))

            HomeV2Status(task)
        }
    }
}

@Composable
fun HomeV2RecommendationCard(
    task: Task?
) {
    HomeV2CardContainer(
        accent = HomeV2Colors.Lavender
    ) {
        HomeV2CardTitle(
            text = "MINDSAI RECOMIENDA",
            color = HomeV2Colors.Lavender
        )

        Spacer(modifier = Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    HomeV2Colors.Purple.copy(
                        alpha = 0.16f
                    ),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "AI",
                color = HomeV2Colors.Lavender,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (task == null) {
            Text(
                text = "Organiza tu siguiente meta",
                color = HomeV2Colors.TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(7.dp))

            Text(
                text = "Tu agenda esta libre",
                color = HomeV2Colors.TextSecondary,
                fontSize = 9.sp,
                textAlign = TextAlign.Center
            )
        } else {
            Text(
                text = "Empieza por",
                color = HomeV2Colors.TextSecondary,
                fontSize = 8.sp
            )

            Text(
                text = task.titulo.ifBlank {
                    "Tu siguiente actividad"
                },
                color = HomeV2Colors.TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = homeV2Recommendation(task),
                color = HomeV2Colors.Cyan,
                fontSize = 9.sp,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun HomeV2CardContainer(
    accent: Color,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 27.dp)
            .height(210.dp)
            .background(
                HomeV2Colors.Surface,
                RoundedCornerShape(30.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth(0.48f)
                .height(3.dp)
                .background(
                    accent,
                    RoundedCornerShape(
                        bottomStart = 4.dp,
                        bottomEnd = 4.dp
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 14.dp,
                    vertical = 12.dp
                ),
            horizontalAlignment =
                Alignment.CenterHorizontally,
            verticalArrangement =
                Arrangement.Center,
            content = content
        )
    }
}

@Composable
private fun HomeV2CardTitle(
    text: String,
    color: Color
) {
    Text(
        text = text,
        color = color,
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        maxLines = 1
    )
}

@Composable
private fun HomeV2Metric(
    value: String,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = color,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = label,
            color = HomeV2Colors.TextSecondary,
            fontSize = 8.sp,
            maxLines = 1
        )
    }
}

@Composable
private fun HomeV2Information(
    label: String,
    value: String
) {
    Column(
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            color = HomeV2Colors.TextSecondary,
            fontSize = 8.sp
        )

        Text(
            text = value,
            color = HomeV2Colors.TextPrimary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1
        )
    }
}

@Composable
private fun HomeV2Status(task: Task) {
    val state = homeV2DateState(task.fecha)

    val color = when (state) {
        HomeV2DateState.OVERDUE ->
            HomeV2Colors.Red

        HomeV2DateState.TODAY ->
            HomeV2Colors.Orange

        HomeV2DateState.UPCOMING ->
            HomeV2Colors.Green

        HomeV2DateState.UNKNOWN ->
            HomeV2Colors.TextSecondary
    }

    val label = when (state) {
        HomeV2DateState.OVERDUE -> "VENCIDA"
        HomeV2DateState.TODAY -> "PARA HOY"
        HomeV2DateState.UPCOMING -> "PROXIMA"
        HomeV2DateState.UNKNOWN -> "SIN FECHA"
    }

    Box(
        modifier = Modifier
            .background(
                color.copy(alpha = 0.15f),
                RoundedCornerShape(15.dp)
            )
            .padding(
                horizontal = 12.dp,
                vertical = 5.dp
            )
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
