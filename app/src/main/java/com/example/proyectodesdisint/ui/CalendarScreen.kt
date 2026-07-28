package com.example.proyectodesdisint.ui

import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.proyectodesdisint.viewmodel.HomeViewModelFactory
import com.example.proyectodesdisint.model.Task
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectodesdisint.viewmodel.HomeViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(navController: NavController) {

    val context = LocalContext.current

    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(context)
    )
    val tasks by viewModel.tasks.collectAsState()

    val datePickerState = rememberDatePickerState()

    var selectedDate by remember { mutableStateOf("") }
    var selectedTime by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }

    val formatter = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }

    val millis = datePickerState.selectedDateMillis

    LaunchedEffect(millis) {
        selectedDate = if (millis != null) {
            formatter.format(Date(millis))
        } else ""
    }
    val calendar = Calendar.getInstance()

    val nextDays = (0..6).map { offset ->
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, offset)
        formatter.format(cal.time)
    }
    val today = SimpleDateFormat("yyyy-MM-dd").format(Date())

    val upcomingTasks = tasks.filter { it.fecha in nextDays }

    val fechasConTareas = tasks.map { it.fecha }.distinct()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Calendario",
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Calendario",
                    style = MaterialTheme.typography.headlineSmall
                )
            }
            Spacer(Modifier.height(16.dp))
            DatePicker(state = datePickerState)
            Spacer(Modifier.height(16.dp))
            Text("Fecha seleccionada: $selectedDate")
            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { showDialog = true },
                enabled = selectedDate.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Agregar tarea en esta fecha")
            }

            Spacer(Modifier.height(16.dp))

            Text("Próximos días", style = MaterialTheme.typography.titleMedium)

            Spacer(Modifier.height(16.dp))

        }

        if (upcomingTasks.isEmpty()) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No hay tareas próximas")
                }
            }
        } else {
            val groupedTasks = upcomingTasks.groupBy { it.fecha }

            groupedTasks.forEach { (fecha, tasksForDay) ->
                val sortedTasksForDay = tasksForDay.sortedWith(
                    compareBy<Task> { parseHourForSorting(it.hora) == null }
                        .thenBy { parseHourForSorting(it.hora) ?: Int.MAX_VALUE }
                        .thenBy { it.titulo.lowercase() }
                )

                item {
                    Text(
                        text = "📅 $fecha",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(sortedTasksForDay) { task ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(task.titulo, style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(4.dp))
                            Text(task.descripcion)
                            Spacer(Modifier.height(4.dp))
                            Text("🕒 ${task.hora}")
                        }
                    }
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Nueva tarea") },
            text = {
                Column {

                    OutlinedTextField(
                        value = titulo,
                        onValueChange = { titulo = it },
                        label = { Text("Título") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TimePickerField(
                        value = selectedTime,
                        label = "Hora",
                        onValueChange = { selectedTime = it }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (titulo.isNotBlank()) {
                            viewModel.addTask(
                                Task(
                                    id = System.currentTimeMillis().toString(),
                                    titulo = titulo,
                                    descripcion = descripcion,
                                    fecha = selectedDate,
                                    hora = selectedTime,
                                    completado = false
                                )
                            )
                            titulo = ""
                            descripcion = ""
                            selectedTime = ""
                            showDialog = false
                        }
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }
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
