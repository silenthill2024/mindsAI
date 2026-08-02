package com.example.proyectodesdisint.ui

import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.PriorityHigh
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.proyectodesdisint.viewmodel.HomeViewModelFactory
import com.example.proyectodesdisint.model.Task
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectodesdisint.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(navController: NavController) {

    val context = LocalContext.current
    val viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(context))
    val tasks by viewModel.tasks.collectAsState()

    val datePickerState = rememberDatePickerState()
    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var prioridad by remember { mutableStateOf("Media") }

    val formatter = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }

    LaunchedEffect(datePickerState.selectedDateMillis) {
        val millis = datePickerState.selectedDateMillis
        selectedDate = if (millis != null) formatter.format(Date(millis)) else ""
    }

    val nextDays = (0..6).map { offset ->
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, offset)
        formatter.format(cal.time)
    }

    val upcomingTasks = tasks.filter { it.fecha in nextDays }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarMonth, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text("Calendario", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(16.dp))
            
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                DatePicker(state = datePickerState, showModeToggle = false)
            }
            
            Spacer(Modifier.height(16.dp))

            Button(
                onClick = { showDialog = true },
                enabled = selectedDate.isNotEmpty(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Programar para el $selectedDate")
            }

            Spacer(Modifier.height(24.dp))
            Text("PrÃƒÂ³ximas Tareas", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
        }

        if (upcomingTasks.isEmpty()) {
            item {
                Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                    Text("No hay tareas prÃƒÂ³ximas", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
                }
            }
        } else {
            val groupedTasks = upcomingTasks.groupBy { it.fecha }
            groupedTasks.forEach { (fecha, tasksForDay) ->
                item {
                    Text(
                        text = "Ã°Å¸â€œâ€¦ $fecha",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(tasksForDay.sortedBy { it.hora }) { task ->
                    val pColor = when (task.prioridad) {
                        "Alta" -> Color(0xFFFF4B66)
                        "Baja" -> Color(0xFF00BFA5)
                        else -> MaterialTheme.colorScheme.primary
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.width(4.dp).height(30.dp).background(pColor, RoundedCornerShape(2.dp)))
                            Spacer(Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(task.titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text(task.hora, style = MaterialTheme.typography.labelSmall, color = pColor)
                            }
                            if (task.prioridad == "Alta") {
                                Icon(Icons.Default.PriorityHigh, null, tint = pColor, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Nueva Tarea") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = titulo, onValueChange = { titulo = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
                    TimePickerField(value = selectedTime, label = "Hora", onValueChange = { selectedTime = it })
                    
                    Text("Prioridad", style = MaterialTheme.typography.labelLarge)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        listOf("Baja", "Media", "Alta").forEach { p ->
                            FilterChip(selected = prioridad == p, onClick = { prioridad = p }, label = { Text(p) })
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (titulo.isNotBlank()) {
                            viewModel.addTask(Task(
                                id = System.currentTimeMillis().toString(),
                                titulo = titulo,
                                descripcion = descripcion,
                                fecha = selectedDate,
                                hora = selectedTime,
                                prioridad = prioridad
                            ))
                            titulo = ""; descripcion = ""; selectedTime = ""; showDialog = false
                        }
                    }
                ) { Text("Guardar") }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancelar") } }
        )
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
