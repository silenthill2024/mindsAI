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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectodesdisint.viewmodel.HomeViewModelFactory
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.example.mindsai.model.Task
import com.example.proyectodesdisint.viewmodel.HomeViewModel

@Composable
fun HomeScreen(navController: NavController) {

    var filter by remember { mutableStateOf("TODAY") }
    var searchQuery by remember { mutableStateOf("") }
    var isCreatingTask by remember { mutableStateOf(false) }
    var selectedTask by remember { mutableStateOf<Task?>(null) }

    val context = LocalContext.current
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(context))
    val tasks by viewModel.tasks.collectAsState()
    val suggestion by viewModel.suggestion.collectAsState()
    val recommendedTime = viewModel.getRecommendedTime()
    val today = java.text.SimpleDateFormat("yyyy-MM-dd").format(java.util.Date())

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            // AI Insight Section
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = suggestion, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    if (recommendedTime.isNotBlank() && !recommendedTime.startsWith("Sin suficiente")) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = recommendedTime, 
                            style = MaterialTheme.typography.bodySmall, 
                            color = Color.White.copy(alpha = 0.8f), 
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar tarea...") },
                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                shape = RoundedCornerShape(24.dp),
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                ),
                singleLine = true
            )

            // Detailed Progress Card
            val total = tasks.count { it.fecha == today }
            val completed = tasks.count { it.fecha == today && it.completado }
            val progress = if (total > 0) completed.toFloat() / total else 0f

            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.size(60.dp),
                            strokeWidth = 6.dp,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant,
                            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                        Text("${(progress * 100).toInt()}%", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(Modifier.width(20.dp))
                    
                    Column {
                        Text("Resumen de Hoy", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text("$completed de $total tareas completadas", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }
                }
            }

            // Coming Soon Brain Model
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp).clickable { navController.navigate("ai") },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Psychology, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(12.dp))
                    Text("Modelo Cerebral 3D - Ver Estado", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }

            // Filter Controls
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(bottom = 16.dp)) {
                item { FilterButton("Hoy", filter == "TODAY") { filter = "TODAY" } }
                item { FilterButton("Todas", filter == "ALL") { filter = "ALL" } }
                item { FilterButton("Pendientes", filter == "PENDING") { filter = "PENDING" } }
                item { FilterButton("Completadas", filter == "DONE") { filter = "DONE" } }
            }

            // Tasks List
            LazyColumn(
                modifier = Modifier.weight(1f), 
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 100.dp) // Espacio para que el FAB no tape la última tarea
            ) {
                val filteredTasks = when (filter) {
                    "TODAY" -> tasks.filter { it.fecha == today }
                    "PENDING" -> tasks.filter { !it.completado }
                    "DONE" -> tasks.filter { it.completado }
                    else -> tasks
                }.filter { 
                    it.titulo.contains(searchQuery, ignoreCase = true) || 
                    it.descripcion.contains(searchQuery, ignoreCase = true) 
                }.let { 
                    if (filter == "TODAY") it.sortedBy { t -> t.hora }
                    else if (filter == "DONE") it.sortedByDescending { t -> t.completionTime }
                    else it.sortedBy { t -> t.fecha }
                }

                if (filteredTasks.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillParentMaxWidth().padding(top = 40.dp), contentAlignment = Alignment.Center) {
                            Text("No hay tareas en esta categoría", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                        }
                    }
                } else {
                    items(filteredTasks, key = { it.documentId }) { task ->
                        EnhancedTaskItem(
                            modifier = Modifier.animateItem(),
                            task = task,
                            onToggle = { viewModel.toggleTaskCompletion(task) },
                            onDelete = { viewModel.deleteTask(task) },
                            onEdit = { selectedTask = it }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { isCreatingTask = true },
            containerColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Nueva Tarea", tint = Color.White)
        }
    }

    // Dialog for Add/Edit
    if (selectedTask != null || isCreatingTask) {
        val taskToEdit = selectedTask ?: Task(titulo = "", descripcion = "", fecha = today, hora = "", prioridad = "Media")
        var titulo by remember { mutableStateOf(taskToEdit.titulo) }
        var descripcion by remember { mutableStateOf(taskToEdit.descripcion) }
        var hora by remember { mutableStateOf(taskToEdit.hora) }
        var prioridad by remember { mutableStateOf(taskToEdit.prioridad) }

        AlertDialog(
            onDismissRequest = { selectedTask = null; isCreatingTask = false },
            title = { Text(if (selectedTask != null) "Editar Tarea" else "Nueva Tarea") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = titulo, onValueChange = { titulo = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
                    TimePickerField(value = hora, label = "Hora", onValueChange = { hora = it })
                    Text("Prioridad", style = MaterialTheme.typography.labelLarge)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        listOf("Baja", "Media", "Alta").forEach { p ->
                            FilterChip(selected = prioridad == p, onClick = { prioridad = p }, label = { Text(p) })
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val finalTask = taskToEdit.copy(titulo = titulo, descripcion = descripcion, hora = hora, prioridad = prioridad)
                    if (selectedTask != null) viewModel.updateTask(finalTask) else viewModel.addTask(finalTask)
                    selectedTask = null; isCreatingTask = false
                }) { Text("Guardar") }
            },
            dismissButton = { TextButton(onClick = { selectedTask = null; isCreatingTask = false }) { Text("Cancelar") } }
        )
    }
}

@Composable
fun EnhancedTaskItem(
    task: Task, 
    onToggle: () -> Unit, 
    onDelete: () -> Unit, 
    onEdit: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    var offsetX by remember { mutableStateOf(0f) }
    val animatedOffset by animateFloatAsState(offsetX)
    val priorityColor = when (task.prioridad) {
        "Alta" -> Color(0xFFFF4B66)
        "Baja" -> Color(0xFF00BFA5)
        else -> MaterialTheme.colorScheme.primary
    }

    Box(modifier = modifier) {
        // Delete Background
        Card(
            modifier = Modifier.matchParentSize().padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Red.copy(alpha = (offsetX / 200f).coerceIn(0f, 0.8f))
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(start = 16.dp), 
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (offsetX > 20f) {
                    Icon(Icons.Default.Delete, null, tint = Color.White.copy(alpha = (offsetX / 100f).coerceIn(0f, 1f)))
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = animatedOffset.dp)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(onDragEnd = { if (offsetX > 150f) onDelete() else offsetX = 0f }) 
                    { _, dragAmount -> offsetX += dragAmount; if (offsetX < 0f) offsetX = 0f }
                }
                .clickable { onEdit(task) },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                // Priority & Checkbox
                IconButton(onClick = onToggle) {
                    Icon(
                        imageVector = if (task.completado) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                        contentDescription = null,
                        tint = if (task.completado) MaterialTheme.colorScheme.primary else priorityColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                Spacer(Modifier.width(8.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        task.titulo, 
                        style = MaterialTheme.typography.titleMedium, 
                        fontWeight = FontWeight.Bold,
                        color = if (task.completado) Color.Gray else Color.Unspecified,
                        textDecoration = if (task.completado) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                    )
                    if (task.descripcion.isNotEmpty()) {
                        Text(task.descripcion, style = MaterialTheme.typography.bodySmall, color = Color.Gray, maxLines = 1)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(task.hora, style = MaterialTheme.typography.labelSmall, color = priorityColor)
                        if (task.prioridad == "Alta") {
                            Spacer(Modifier.width(4.dp))
                            Icon(Icons.Default.PriorityHigh, null, tint = priorityColor, modifier = Modifier.size(12.dp))
                        }
                    }
                }
                
                // Priority Strip
                Box(modifier = Modifier.width(4.dp).height(40.dp).background(priorityColor, RoundedCornerShape(2.dp)))
            }
        }
    }
}

@Composable
fun FilterButton(text: String, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
        border = if (!isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)) else null
    ) {
        Text(text, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), style = MaterialTheme.typography.labelLarge)
    }
}
