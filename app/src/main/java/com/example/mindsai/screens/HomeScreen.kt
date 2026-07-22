package com.example.mindsai.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mindsai.local.UserSession
import com.example.mindsai.local.data.TaskEntity
import com.example.mindsai.viewmodel.HomeViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel, onNavigateToTasks: () -> Unit) {
    val user = UserSession.currentUser
    val tasks by viewModel.tasks.collectAsState()
    val scrollState = rememberScrollState()
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var taskFilter by remember { mutableIntStateOf(0) } // 0: Todas, 1: Pendientes, 2: Completadas
    
    val pendingTasksCount = tasks.count { !it.isCompleted }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddTaskDialog = true },
                containerColor = Color(0xFF673AB7),
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF8F9FF))
                .verticalScroll(scrollState)
        ) {
            // 1. Header Premium
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "¡Hola, ${user?.nombre ?: "Usuario"}! 👋",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1A1C1E)
                    )
                    Text(
                        text = "Tienes $pendingTasksCount tareas pendientes para hoy.",
                        fontSize = 14.sp,
                        color = Color(0xFF6B6E74)
                    )
                }
                
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = Color(0xFF673AB7).copy(alpha = 0.1f)
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(8.dp), tint = Color(0xFF673AB7))
                }
            }

            // 2. Próximo Evento Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFEDE7F6)
                    ) {
                        Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color(0xFF673AB7), modifier = Modifier.padding(12.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Próximo evento", fontSize = 12.sp, color = Color.Gray)
                        Text("Examen de Programación", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Mañana • 10:00 AM", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            // 3. Filtros de Tareas
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mis Tareas",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                TextButton(onClick = onNavigateToTasks) {
                    Text("Ver todas", color = Color(0xFF673AB7), fontSize = 12.sp)
                }
            }

            ScrollableTabRow(
                selectedTabIndex = taskFilter,
                containerColor = Color.Transparent,
                contentColor = Color(0xFF673AB7),
                edgePadding = 24.dp,
                divider = {},
                indicator = {}
            ) {
                val filters = listOf("Todas", "Pendientes", "Completadas")
                filters.forEachIndexed { index, label ->
                    val isSelected = taskFilter == index
                    Tab(
                        selected = isSelected,
                        onClick = { taskFilter = index },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .height(40.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isSelected) Color(0xFF673AB7) else Color.White),
                        text = {
                            Text(
                                label, 
                                color = if (isSelected) Color.White else Color.Gray,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // 4. Lista de Tareas Filtrada
            val filteredTasks = when (taskFilter) {
                1 -> tasks.filter { !it.isCompleted }
                2 -> tasks.filter { it.isCompleted }
                else -> tasks
            }

            Column(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (filteredTasks.isEmpty()) {
                    Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
                        Text("No hay tareas para mostrar", color = Color.Gray, fontSize = 14.sp)
                    }
                } else {
                    filteredTasks.forEach { task ->
                        ModernTaskCard(
                            task = task, 
                            onProgressChange = { viewModel.updateTaskProgress(task, it) }
                        )
                    }
                }
            }

            // 5. Estadísticas Rápidas
            Text(
                text = "Estadísticas Semanales",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 24.dp, top = 8.dp, bottom = 12.dp)
            )

            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    StatCardMini("${user?.studyHours ?: 0}h", "Horas estudio", Icons.Default.Schedule, Color(0xFF673AB7))
                }
                item {
                    StatCardMini("${user?.tasksCompleted ?: 0}", "Tareas completas", Icons.Default.CheckCircle, Color(0xFF4CAF50))
                }
                item {
                    StatCardMini("${user?.xp ?: 0}", "Puntos XP", Icons.Default.Star, Color(0xFFFF9800))
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    if (showAddTaskDialog) {
        AddTaskDialog(
            onDismiss = { showAddTaskDialog = false },
            onConfirm = { t, d ->
                viewModel.addTask(t, d)
                showAddTaskDialog = false
            }
        )
    }
}

@Composable
fun ModernTaskCard(task: TaskEntity, onProgressChange: (Float) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = Color.Black
        ),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = if (task.isCompleted) Color(0xFF4CAF50).copy(alpha = 0.1f) else Color(0xFF673AB7).copy(alpha = 0.1f)
                ) {
                    Icon(
                        if (task.isCompleted) Icons.Default.CheckCircle else Icons.Default.Pending,
                        contentDescription = null,
                        tint = if (task.isCompleted) Color(0xFF4CAF50) else Color(0xFF673AB7),
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        task.title, 
                        fontWeight = FontWeight.Bold, 
                        fontSize = 16.sp,
                        color = if (task.isCompleted) Color.Gray else Color.Black
                    )
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = null)
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Text(task.description, fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Progreso: ${(task.progress * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF673AB7))
                    Slider(
                        value = task.progress,
                        onValueChange = onProgressChange,
                        modifier = Modifier.fillMaxWidth(),
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFF673AB7),
                            activeTrackColor = Color(0xFF673AB7)
                        )
                    )
                }
            }
        }
    }
}

@Composable
fun AddTaskDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Tarea Académica") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = { if (title.isNotBlank()) onConfirm(title, desc) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7))) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

@Composable
fun StatCardMini(value: String, label: String, icon: ImageVector, color: Color) {
    Surface(
        modifier = Modifier.width(130.dp).height(85.dp),
        shape = RoundedCornerShape(20.dp),
        color = Color.White,
        shadowElevation = 1.dp
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.Center) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            }
            Text(label, fontSize = 10.sp, color = Color.Gray)
        }
    }
}
