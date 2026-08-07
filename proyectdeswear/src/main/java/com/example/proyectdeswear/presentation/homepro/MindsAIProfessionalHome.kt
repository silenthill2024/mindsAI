package com.example.proyectdeswear.presentation.homepro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import com.example.proyectdeswear.presentation.Task
import com.example.proyectdeswear.presentation.homepro.components.MindsAIHeader
import com.example.proyectdeswear.presentation.homepro.components.NeuralBackground
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MindsAIProfessionalHome(
    tasks: List<Task>,
    notificationCount: Int,
    onNotificationsClick: () -> Unit,
    onDelete: (Task) -> Unit,
    modifier: Modifier = Modifier
) {

    val pendingTasks =
        tasks.filter {
            !it.completado
        }

    val pending =
        pendingTasks.size

    val nextTask =
        pendingTasks.firstOrNull()

    val currentTime =
        SimpleDateFormat(
            "HH:mm",
            Locale.getDefault()
        ).format(Date())

    val currentDate =
        SimpleDateFormat(
            "EEE d MMM",
            Locale("es", "MX")
        ).format(Date())
            .replaceFirstChar {
                if (it.isLowerCase()) {
                    it.titlecase(
                        Locale("es", "MX")
                    )
                } else {
                    it.toString()
                }
            }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                MindsAIColors.Background
            )
    ) {

        NeuralBackground(
            modifier =
                Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 18.dp,
                    vertical = 9.dp
                ),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            MindsAIHeader(
                notificationCount =
                    notificationCount,
                onNotificationsClick =
                    onNotificationsClick
            )

            Spacer(
                modifier =
                    Modifier.height(5.dp)
            )

            Text(
                text = currentTime,
                color =
                    MindsAIColors.TextPrimary,
                fontSize = 28.sp,
                fontWeight =
                    FontWeight.Bold
            )

            Text(
                text = currentDate,
                color =
                    MindsAIColors.TextSecondary,
                fontSize = 10.sp
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            PendingCard(
                pending = pending
            )

            Spacer(
                modifier =
                    Modifier.height(9.dp)
            )

            if (nextTask != null) {

                Text(
                    text = "SIGUIENTE",
                    color =
                        MindsAIColors.Purple,
                    fontSize = 8.sp,
                    fontWeight =
                        FontWeight.Bold
                )

                Text(
                    text = nextTask.titulo,
                    color =
                        MindsAIColors.TextPrimary,
                    fontSize = 12.sp,
                    fontWeight =
                        FontWeight.Bold,
                    textAlign =
                        TextAlign.Center,
                    maxLines = 2
                )

                if (
                    nextTask.hora.isNotBlank()
                ) {

                    Text(
                        text = nextTask.hora,
                        color =
                            MindsAIColors.TextSecondary,
                        fontSize = 9.sp
                    )
                }

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                RecommendationText(
                    pending = pending
                )

            } else {

                Spacer(
                    modifier =
                        Modifier.height(8.dp)
                )

                Text(
                    text = "Todo al dia",
                    color =
                        MindsAIColors.TextPrimary,
                    fontSize = 14.sp,
                    fontWeight =
                        FontWeight.Bold
                )

                Text(
                    text =
                        "No tienes tareas pendientes",
                    color =
                        MindsAIColors.TextSecondary,
                    fontSize = 9.sp
                )
            }
        }
    }
}

@Composable
private fun PendingCard(
    pending: Int
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color =
                    MindsAIColors.Purple.copy(
                        alpha = 0.18f
                    ),
                shape =
                    RoundedCornerShape(18.dp)
            )
            .padding(
                vertical = 8.dp,
                horizontal = 12.dp
            ),
        contentAlignment =
            Alignment.Center
    ) {

        Row(
            horizontalArrangement =
                Arrangement.Center,
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Text(
                text =
                    pending.toString(),
                color =
                    MindsAIColors.Purple,
                fontSize = 19.sp,
                fontWeight =
                    FontWeight.Bold
            )

            Text(
                text =
                    if (pending == 1) {
                        "  tarea pendiente"
                    } else {
                        "  tareas pendientes"
                    },
                color =
                    MindsAIColors.TextPrimary,
                fontSize = 10.sp
            )
        }
    }
}
@Composable
private fun RecommendationText(
    pending: Int
) {

    val message =
        when {

            pending >= 5 ->
                "Prioriza la tarea mas proxima."

            pending >= 2 ->
                "Avanza una tarea a la vez."

            else ->
                "Solo falta una. Tu puedes."
        }

    Column(
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {

        Text(
            text =
                "MindsAI recomienda",
            color =
                MindsAIColors.Purple,
            fontSize = 8.sp,
            fontWeight =
                FontWeight.Bold
        )

        Text(
            text = message,
            color =
                MindsAIColors.TextSecondary,
            fontSize = 9.sp,
            textAlign =
                TextAlign.Center
        )
    }
}