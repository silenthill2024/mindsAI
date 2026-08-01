package com.example.proyectdeswear.presentation.agenda

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
fun AgendaScreen(
    tasks: List<Task>,
    modifier: Modifier = Modifier
) {
    val pendingTasks = tasks
        .filter { !it.completado }
        .sortedWith(
            compareBy<Task>(
                { it.fecha.trim() },
                { it.hora.trim() }
            )
        )

    ScalingLazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MindsAIColors.Background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Agenda",
                    color = MindsAIColors.TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${pendingTasks.size} pendientes",
                    color = MindsAIColors.Cyan,
                    fontSize = 9.sp
                )

                Spacer(
                    modifier = Modifier.height(10.dp)
                )
            }
        }

        if (pendingTasks.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .background(
                            color = MindsAIColors.Card,
                            shape = RoundedCornerShape(22.dp)
                        )
                        .padding(15.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Agenda libre",
                        color = MindsAIColors.Green,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "No tienes actividades pendientes",
                        color = MindsAIColors.TextSecondary,
                        fontSize = 9.sp
                    )
                }
            }
        } else {
            pendingTasks.take(10).forEach { task ->
                item {
                    AgendaTaskCard(task)

                    Spacer(
                        modifier = Modifier.height(7.dp)
                    )
                }
            }
        }

        item {
            Spacer(
                modifier = Modifier.height(50.dp)
            )
        }
    }
}

@Composable
private fun AgendaTaskCard(
    task: Task
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp)
            .background(
                color = MindsAIColors.Card,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(12.dp)
    ) {
        Text(
            text = task.hora.ifBlank {
                "Sin hora"
            },
            color = MindsAIColors.Purple,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = task.titulo.ifBlank {
                "Tarea sin titulo"
            },
            color = MindsAIColors.TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2
        )

        Text(
            text = task.fecha.ifBlank {
                "Sin fecha"
            },
            color = MindsAIColors.TextSecondary,
            fontSize = 8.sp
        )
    }
}
