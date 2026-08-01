package com.example.proyectdeswear.presentation.mindsai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import com.example.proyectdeswear.presentation.Task
import com.example.proyectdeswear.presentation.homepro.MindsAIColors

@Composable
fun MindsAIRecommendationsScreen(
    tasks: List<Task>,
    modifier: Modifier = Modifier
) {
    val pending = tasks.filter { !it.completado }
    val nextTask = pending.firstOrNull()

    val focus = when {
        pending.size >= 8 -> 42
        pending.size >= 5 -> 61
        pending.size >= 2 -> 78
        else -> 90
    }

    val workload = when {
        pending.size >= 8 -> "Muy alta"
        pending.size >= 5 -> "Alta"
        pending.size >= 2 -> "Moderada"
        else -> "Controlada"
    }

    val recommendations = buildList {
        when {
            pending.size >= 8 -> {
                add("Divide tus tareas en bloques de 25 minutos.")
                add("Comienza por la actividad con fecha mas cercana.")
                add("Haz una pausa breve despues de dos bloques.")
            }

            pending.size >= 5 -> {
                add("Prioriza tres actividades importantes.")
                add("Evita comenzar varias tareas al mismo tiempo.")
                add("Reserva diez minutos para descansar.")
            }

            pending.isNotEmpty() -> {
                add("Tu carga esta controlada.")
                add("Completa primero la siguiente actividad.")
                add("Mantén sesiones de estudio cortas.")
            }

            else -> {
                add("No tienes tareas pendientes.")
                add("Aprovecha para descansar y recuperar energia.")
            }
        }
    }

    ScalingLazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MindsAIColors.Background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "MindsAI",
                color = MindsAIColors.Purple,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Recomendaciones neuronales",
                color = MindsAIColors.Cyan,
                fontSize = 9.sp
            )

            Spacer(modifier = Modifier.height(9.dp))
        }

        item {
            NeuralStatusCard(
                focus = focus,
                workload = workload,
                pending = pending.size
            )

            Spacer(modifier = Modifier.height(9.dp))
        }

        nextTask?.let { task ->
            item {
                RecommendationCard(
                    title = "Prioridad sugerida",
                    message = task.titulo.ifBlank {
                        "Siguiente actividad pendiente"
                    }
                )

                Spacer(modifier = Modifier.height(7.dp))
            }
        }

        recommendations.forEachIndexed { index, message ->
            item {
                RecommendationCard(
                    title = "Recomendacion ${index + 1}",
                    message = message
                )

                Spacer(modifier = Modifier.height(7.dp))
            }
        }

        item {
            Spacer(modifier = Modifier.height(44.dp))
        }
    }
}

@Composable
private fun NeuralStatusCard(
    focus: Int,
    workload: String,
    pending: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp)
            .background(
                MindsAIColors.Card,
                RoundedCornerShape(24.dp)
            )
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "ESTADO NEURONAL",
            color = MindsAIColors.Cyan,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "$focus%",
            color = MindsAIColors.TextPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Enfoque estimado",
            color = MindsAIColors.TextSecondary,
            fontSize = 9.sp
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = "Carga: $workload",
            color = MindsAIColors.Orange,
            fontSize = 9.sp
        )

        Text(
            text = "$pending tareas pendientes",
            color = MindsAIColors.TextSecondary,
            fontSize = 8.sp
        )
    }
}

@Composable
private fun RecommendationCard(
    title: String,
    message: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp)
            .background(
                MindsAIColors.CardSecondary,
                RoundedCornerShape(20.dp)
            )
            .padding(12.dp)
    ) {
        Text(
            text = title,
            color = MindsAIColors.Purple,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = message,
            color = MindsAIColors.TextPrimary,
            fontSize = 10.sp,
            maxLines = 3
        )
    }
}
