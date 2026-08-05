package com.example.proyectodesdisint.ui.homev3

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.proyectodesdisint.model.Task
import com.example.proyectodesdisint.ui.TaskEditorDialog
import com.example.proyectodesdisint.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TaskListSection(
    tasks: List<Task>,
    viewModel: HomeViewModel
) {
    var showCreateDialog by remember {
        mutableStateOf(false)
    }

    var taskToEdit by remember {
        mutableStateOf<Task?>(null)
    }

    val orderedTasks = remember(tasks) {
        tasks.sortedWith(
            compareBy<Task> { it.completado }
                .thenBy {
                    when (it.prioridad.uppercase()) {
                        "ALTA" -> 0
                        "MEDIA" -> 1
                        "BAJA" -> 2
                        else -> 3
                    }
                }
                .thenBy { it.fecha }
                .thenBy { it.hora }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Tus tareas",
                    style =
                        MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color =
                        MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "${tasks.count { !it.completado }} pendientes",
                    style =
                        MaterialTheme.typography.bodySmall,
                    color =
                        MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(
                onClick = {
                    showCreateDialog = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )

                Text(
                    text = "Nueva",
                    modifier = Modifier.padding(start = 5.dp)
                )
            }
        }

        if (orderedTasks.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(22.dp)
                ) {
                    Text(
                        text = "No hay tareas guardadas",
                        style =
                            MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )

                    Text(
                        text = "Presiona Nueva para agregar tu primera tarea.",
                        color =
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            orderedTasks.forEach { task ->
                TaskV3Card(
                    task = task,
                    onToggle = {
                        viewModel.toggleTaskCompletion(task)
                    },
                    onEdit = {
                        taskToEdit = task
                    },
                    onDelete = {
                        viewModel.deleteTask(task)
                    }
                )
            }
        }
    }

    if (showCreateDialog) {
        val today = remember {
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            ).format(Date())
        }

        TaskEditorDialog(
            task = Task(
                titulo = "",
                descripcion = "",
                fecha = today,
                hora = "",
                prioridad = "Media"
            ),
            isEditing = false,
            onDismiss = {
                showCreateDialog = false
            },
            onSave = { task ->
                viewModel.addTask(task)
                showCreateDialog = false
            }
        )
    }

    taskToEdit?.let { selectedTask ->
        TaskEditorDialog(
            task = selectedTask,
            isEditing = true,
            onDismiss = {
                taskToEdit = null
            },
            onSave = { updatedTask ->
                viewModel.updateTask(updatedTask)
                taskToEdit = null
            }
        )
    }
}

@Composable
private fun TaskV3Card(
    task: Task,
    onToggle: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val priorityColor = when (
        task.prioridad.uppercase()
    ) {
        "ALTA" -> Color(0xFFFF5252)
        "BAJA" -> Color(0xFF44D17A)
        else -> Color(0xFFFFC107)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.completado,
                onCheckedChange = {
                    onToggle()
                }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Text(
                        text = task.titulo.ifBlank {
                            "Tarea sin título"
                        },
                        style =
                            MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        textDecoration =
                            if (task.completado) {
                                TextDecoration.LineThrough
                            } else {
                                null
                            },
                        color =
                            if (task.completado) {
                                MaterialTheme.colorScheme
                                    .onSurfaceVariant
                            } else {
                                MaterialTheme.colorScheme
                                    .onSurface
                            }
                    )

                    if (
                        task.prioridad.equals(
                            "Alta",
                            ignoreCase = true
                        )
                    ) {
                        Icon(
                            imageVector =
                                Icons.Default.PriorityHigh,
                            contentDescription = null,
                            tint = priorityColor
                        )
                    }
                }

                if (task.descripcion.isNotBlank()) {
                    Text(
                        text = task.descripcion,
                        style =
                            MaterialTheme.typography.bodySmall,
                        color =
                            MaterialTheme.colorScheme
                                .onSurfaceVariant,
                        maxLines = 2
                    )
                }

                Text(
                    text = buildString {
                        append(task.fecha)

                        if (task.hora.isNotBlank()) {
                            append(" · ")
                            append(task.hora)
                        }

                        append(" · ")
                        append(task.prioridad)
                    },
                    style =
                        MaterialTheme.typography.labelMedium,
                    color = priorityColor
                )
            }

            IconButton(
                onClick = onEdit
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Editar tarea",
                    tint =
                        MaterialTheme.colorScheme.primary
                )
            }

            IconButton(
                onClick = onDelete
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar tarea",
                    tint =
                        MaterialTheme.colorScheme.error
                )
            }
        }
    }
}