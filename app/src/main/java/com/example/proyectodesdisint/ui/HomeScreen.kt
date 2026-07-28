package com.example.proyectodesdisint.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import android.os.VibrationEffect
import android.os.Vibrator
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectodesdisint.viewmodel.HomeViewModelFactory
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.graphics.Brush
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectodesdisint.model.Task
import com.example.proyectodesdisint.viewmodel.HomeViewModel

@Composable
fun HomeScreen(navController: NavController) {

    var filter by remember { mutableStateOf("TODAY") }
    var isCreatingTask by remember { mutableStateOf(false) }
    var selectedTask by remember { mutableStateOf<Task?>(null) }

    val context = LocalContext.current

    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(context)
    )

    val tasks by viewModel.tasks.collectAsState()
    val suggestion by viewModel.suggestion.collectAsState()
    val recommendedTime = viewModel.getRecommendedTime()

    val today = java.text.SimpleDateFormat("yyyy-MM-dd").format(java.util.Date())
    val showRecommendedTime = suggestion.isNotBlank() &&
        !suggestion.contains("La red neuronal recomienda", ignoreCase = true) &&
        recommendedTime.isNotBlank() &&
        !recommendedTime.startsWith("Sin suficiente historial", ignoreCase = true)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            )
            .padding(16.dp)
    ) {

        Text(
            text = suggestion,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        if (showRecommendedTime) {
            Text(
                text = recommendedTime,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { isCreatingTask = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Agregar tarea")
        }

        val total = tasks.size
        val completed = tasks.count { it.completado }
        val progress = if (total > 0) completed.toFloat() / total else 0f

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                Text(
                    "Progreso de tareas",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item { FilterButton("Hoy", filter == "TODAY") { filter = "TODAY" } }
                    item { FilterButton("Todas", filter == "ALL") { filter = "ALL" } }
                    item { FilterButton("Pendientes", filter == "PENDING") { filter = "PENDING" } }
                    item { FilterButton("Completadas", filter == "DONE") { filter = "DONE" } }
                }
            }
        }

        LazyColumn {
            when (filter) {
                "TODAY" -> {
                    val filteredTasks = tasks
                        .filter { it.fecha == today }
                        .sortedWith(
                            compareBy<Task> { parseHourForSorting(it.hora) == null }
                                .thenBy { parseHourForSorting(it.hora) ?: Int.MAX_VALUE }
                                .thenBy { it.titulo.lowercase() }
                        )

                    items(filteredTasks, key = { it.documentId }) { task ->
                        TaskItem(
                            task = task,
                            onToggle = { viewModel.toggleTaskCompletion(task) },
                            onDelete = { viewModel.deleteTask(task) },
                            onEdit = { selectedTask = it }
                        )
                    }
                }

                else -> {
                    val filteredTasks = when (filter) {
                        "PENDING" -> tasks.filter { !it.completado }
                        "DONE" -> tasks.filter { it.completado }
                        else -> tasks
                    }

                    val groupedTasks = filteredTasks
                        .sortedWith(compareBy<Task> { it.fecha.isBlank() }.thenBy { it.fecha })
                        .groupBy { it.fecha.ifBlank { "Sin fecha" } }

                    groupedTasks.forEach { (fecha, tasksForDay) ->
                        val sortedTasksForDay = tasksForDay.sortedWith(
                            compareBy<Task> { parseHourForSorting(it.hora) == null }
                                .thenBy { parseHourForSorting(it.hora) ?: Int.MAX_VALUE }
                                .thenBy { it.titulo.lowercase() }
                        )

                        item(key = "header-$fecha") {
                            Text(
                                text = fecha,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        items(sortedTasksForDay, key = { it.documentId }) { task ->
                            TaskItem(
                                task = task,
                                onToggle = { viewModel.toggleTaskCompletion(task) },
                                onDelete = { viewModel.deleteTask(task) },
                                onEdit = { selectedTask = it }
                            )
                        }
                    }
                }
            }
        }

        val isDialogOpen = selectedTask != null || isCreatingTask
        val editingTask = selectedTask

        if (isDialogOpen) {

            val taskToEdit = editingTask ?: Task(
                titulo = "",
                descripcion = "",
                fecha = "",
                hora = ""
            )

            var titulo by remember { mutableStateOf(taskToEdit.titulo) }
            var descripcion by remember { mutableStateOf(taskToEdit.descripcion) }
            var hora by remember { mutableStateOf(taskToEdit.hora) }

            AlertDialog(
                onDismissRequest = {
                    selectedTask = null
                    isCreatingTask = false
                },
                containerColor = MaterialTheme.colorScheme.surface,
                title = {
                    Text(
                        if (editingTask != null) "Editar tarea" else "Nueva tarea",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                text = {
                    Column {
                        TextField(
                            value = titulo,
                            onValueChange = { titulo = it },
                            label = { Text("Título") }
                        )
                        TextField(
                            value = descripcion,
                            onValueChange = { descripcion = it },
                            label = { Text("Descripción") }
                        )
                        TimePickerField(
                            value = hora,
                            label = "Hora",
                            onValueChange = { hora = it }
                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        if (editingTask != null) {
                            viewModel.updateTask(
                                taskToEdit.copy(
                                    titulo = titulo,
                                    descripcion = descripcion,
                                    hora = hora
                                )
                            )
                        } else {
                            viewModel.addTask(
                                Task(
                                    id = System.currentTimeMillis().toString(),
                                    titulo = titulo,
                                    descripcion = descripcion,
                                    fecha = today,
                                    hora = hora,
                                    completado = false
                                )
                            )
                        }
                        selectedTask = null
                        isCreatingTask = false
                    }) {
                        Text("Guardar")
                    }
                },
                dismissButton = {
                    Button(onClick = {
                        selectedTask = null
                        isCreatingTask = false
                    }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun FilterButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.surfaceVariant,
            contentColor = if (isSelected)
                MaterialTheme.colorScheme.onPrimary
            else
                MaterialTheme.colorScheme.onSurface
        )
    ) {
        Text(text)
    }
}

@Composable
fun TaskItem(
    task: Task,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
    onEdit: (Task) -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    val animatedOffset by animateFloatAsState(offsetX)
    val context = LocalContext.current
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    val showDelete = offsetX > 50f

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {

        if (showDelete) {
            Row(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Red)
                    .padding(end = 20.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar",
                    tint = Color.White
                )
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = animatedOffset.dp)
                .clickable { onEdit(task) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (offsetX > 150f) {

                                if (vibrator.hasVibrator()) {
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                                        vibrator.vibrate(
                                            VibrationEffect.createOneShot(60, VibrationEffect.DEFAULT_AMPLITUDE)
                                        )
                                    } else {
                                        vibrator.vibrate(60)
                                    }
                                }

                                onDelete()
                            } else {
                                offsetX = 0f
                            }
                        }
                    ) { _, dragAmount ->
                        offsetX += dragAmount
                        if (offsetX < 0f) offsetX = 0f
                    }
                },
            colors = CardDefaults.cardColors(
                containerColor = if (task.completado)
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                else
                    MaterialTheme.colorScheme.surface
            )
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(task.titulo, style = MaterialTheme.typography.titleMedium)
                    Text(task.descripcion, style = MaterialTheme.typography.bodyMedium)
                    Text("Hora: ${task.hora}", color = MaterialTheme.colorScheme.primary)
                }

                Checkbox(
                    checked = task.completado,
                    onCheckedChange = { onToggle() },
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }
    }
}

private fun parseHourForSorting(value: String): Int? {
    val parts = value.trim().split(":")
    if (parts.size != 2) return null

    val hour = parts[0].toIntOrNull() ?: return null
    val minute = parts[1].toIntOrNull() ?: return null

    if (hour !in 0..23 || minute !in 0..59) return null

    return hour * 60 + minute
}
