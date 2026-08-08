package com.example.proyectdeswear.presentation.agenda

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import com.example.proyectdeswear.presentation.Task
import com.example.proyectdeswear.presentation.homepro.MindsAIColors

@Composable
fun AgendaScreen(
    tasks: List<Task>,
    onDelete: (Task) -> Unit,
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
                    AgendaTaskCard(
                        task = task,
                        onDelete = {
                            onDelete(task)
                        }
                    )

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
    task: Task,
    onDelete: () -> Unit
) {
    var confirmDelete by remember(task.documentId) {
        mutableStateOf(false)
    }

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
                "Tarea sin título"
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

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        if (!confirmDelete) {
            DeleteButton(
                text = "Eliminar tarea",
                background = Color(0x33FF526D),
                textColor = Color(0xFFFF526D),
                onClick = {
                    confirmDelete = true
                }
            )
        } else {
            Text(
                text = "¿Confirmar eliminación?",
                color = MindsAIColors.TextPrimary,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(
                modifier = Modifier.height(6.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                DeleteButton(
                    text = "Cancelar",
                    background = MindsAIColors.Card,
                    textColor = MindsAIColors.TextSecondary,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        confirmDelete = false
                    }
                )

                DeleteButton(
                    text = "Eliminar",
                    background = Color(0xFFFF526D),
                    textColor = Color.White,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        confirmDelete = false
                        onDelete()
                    }
                )
            }
        }
    }
}

@Composable
private fun DeleteButton(
    text: String,
    background: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                background,
                RoundedCornerShape(14.dp)
            )
            .clickable(
                interactionSource = remember {
                    MutableInteractionSource()
                },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}