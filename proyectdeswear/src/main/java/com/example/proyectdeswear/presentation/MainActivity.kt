package com.example.proyectdeswear.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import com.example.proyectdeswear.presentation.theme.ProyectoDesDisIntTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProyectoDesDisIntTheme {
                WearApp()
            }
        }
    }
}

@Composable
fun WearApp() {
    var tasks by remember { mutableStateOf(listOf<Task>()) }
    var expandedTaskId by remember { mutableStateOf<String?>(null) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    val service = remember { FirebaseServiceWear() }

    LaunchedEffect(Unit) {
        service.listenTasks { updatedTasks ->
            tasks = updatedTasks

            if (expandedTaskId != null && updatedTasks.none { it.documentId == expandedTaskId }) {
                expandedTaskId = null
            }
        }
    }

    LaunchedEffect(feedbackMessage) {
        if (feedbackMessage != null) {
            delay(1800)
            feedbackMessage = null
        }
    }

    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        item {
            Text(
                text = "Tareas de Hoy",
                color = MaterialTheme.colors.primary
            )
        }

        feedbackMessage?.let { message ->
            item {
                Text(
                    text = message,
                    color = MaterialTheme.colors.secondary
                )
            }
        }

        if (tasks.isEmpty()) {
            item {
                Text(
                    text = "No hay tareas pendientes",
                    color = MaterialTheme.colors.onBackground
                )
            }
        } else {
            tasks.forEach { task ->
                item {
                    Chip(
                        label = {
                            Text(
                                text = task.titulo,
                                color = MaterialTheme.colors.onPrimary
                            )
                        },
                        secondaryLabel = {
                            TaskChipSubtitle(
                                task = task,
                                isExpanded = expandedTaskId == task.documentId
                            )
                        },
                        colors = ChipDefaults.primaryChipColors(
                            backgroundColor = MaterialTheme.colors.primary,
                            contentColor = MaterialTheme.colors.onPrimary
                        ),
                        onClick = {
                            expandedTaskId = if (expandedTaskId == task.documentId) {
                                null
                            } else {
                                task.documentId
                            }
                        }
                    )
                }

                if (expandedTaskId == task.documentId) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 6.dp)
                                .background(
                                    color = MaterialTheme.colors.surface,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = "Descripcion",
                                color = MaterialTheme.colors.primary
                            )
                            Text(
                                text = task.descripcion.ifBlank { "Sin descripcion" },
                                color = MaterialTheme.colors.onSurface
                            )
                            Text(
                                text = "Fecha: ${task.fecha.ifBlank { "Sin fecha" }}",
                                color = MaterialTheme.colors.onSurface
                            )
                            Text(
                                text = "Hora: ${task.hora.ifBlank { "Sin hora" }}",
                                color = MaterialTheme.colors.onSurface
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 10.dp)
                                    .clickable {
                                        service.markTaskAsCompleted(
                                            task = task,
                                            onSuccess = {
                                                expandedTaskId = null
                                                feedbackMessage = "\"${task.titulo}\" completada"
                                            },
                                            onFailure = {
                                                feedbackMessage = "No se pudo completar la tarea"
                                            }
                                        )
                                    },
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "✓ Completar",
                                    color = MaterialTheme.colors.secondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskChipSubtitle(task: Task, isExpanded: Boolean) {
    val time = task.hora.ifBlank { "Sin hora" }
    val action = if (isExpanded) "Ocultar detalles" else "Ver detalles"

    Row {
        Box(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colors.secondary,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = time,
                color = MaterialTheme.colors.onPrimary
            )
        }

        Box(modifier = Modifier.width(6.dp))

        Text(
            text = action,
            color = MaterialTheme.colors.onPrimary
        )
    }
}
