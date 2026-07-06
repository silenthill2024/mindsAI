package com.example.mindsai.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen() {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Pendientes", "En Progreso", "Completados")

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF0F2F5))) {
        // Header Complejo
        Row(
            modifier = Modifier.fillMaxWidth().background(Color(0xFF673AB7)).padding(20.dp).padding(top = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Bienvenido de nuevo", color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                Text("José Pablo Chávez", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                Text("Ing. en Desarrollo y Gestión de Software", color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
            }
            Surface(modifier = Modifier.size(50.dp).clip(CircleShape), color = Color(0xFFEDE7F6)) {
                Icon(Icons.Default.Person, contentDescription = "Perfil", modifier = Modifier.padding(12.dp), tint = Color(0xFF673AB7))
            }
        }

        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 80.dp)) {
            // Sección: Proyectos Destacados (Carrusel Horizontal)
            item {
                Text("Proyectos Activos", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(16.dp))
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    item { ProyectoCard("Cápsula Prometheus", "Telemetría y Biología", 0.6f, Color(0xFFE53935)) }
                    item { ProyectoCard("ReciclaToro", "Visión Artificial", 0.85f, Color(0xFF43A047)) }
                    item { ProyectoCard("App Fatiga Digital", "Machine Learning", 0.4f, Color(0xFF1E88E5)) }
                }
            }

            // Sección: Tabs Interactivas
            item {
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title, fontWeight = FontWeight.Bold) }
                        )
                    }
                }
            }

            // Sección: Lista de Tareas Interactiva (Tarjetas Expandibles)
            item {
                if (selectedTab == 0) {
                    TareaExpandible("Finalizar pkg_sudoku", "Revisar lógica de backtracking en PL/SQL (DBeaver) y detección de victoria.", "Alta")
                    TareaExpandible("Diseño de Interfaz", "Crear mockups para la tablet de monitoreo de Prometheus.", "Media")
                } else if (selectedTab == 1) {
                    TareaExpandible("Entrenamiento CRISP-DM", "Ajustar modelo de clasificación de fatiga digital.", "Alta")
                } else {
                    TareaExpandible("Migración PS3", "Respaldar carpeta home por FTP y cambiar a SSD 480GB.", "Completada")
                }
            }
        }
    }
}

@Composable
fun ProyectoCard(titulo: String, subtitulo: String, progreso: Float, color: Color) {
    Card(modifier = Modifier.width(260.dp), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(12.dp).clip(CircleShape), color = color) {}
                Spacer(modifier = Modifier.width(8.dp))
                Text(subtitulo, color = Color.Gray, fontSize = 12.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(titulo, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))
            LinearProgressIndicator(progress = { progreso }, modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)), color = color, trackColor = color.copy(alpha = 0.2f))
            Text("${(progreso * 100).toInt()}% Completado", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 8.dp).align(Alignment.End))
        }
    }
}

@Composable
fun TareaExpandible(titulo: String, detalle: String, prioridad: String) {
    var expandido by remember { mutableStateOf(false) }
    var completado by remember { mutableStateOf(prioridad == "Completada") }
    var sliderValue by remember { mutableStateOf(0f) }

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).clickable { expandido = !expandido },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = completado, onCheckedChange = { completado = it })
                Column(modifier = Modifier.weight(1f)) {
                    Text(titulo, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = if (completado) Color.Gray else Color.Black)
                    Text("Prioridad: $prioridad", fontSize = 12.sp, color = if (prioridad == "Alta") Color.Red else Color.Gray)
                }
                Icon(if (expandido) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown, contentDescription = null, tint = Color.Gray)
            }

            AnimatedVisibility(visible = expandido) {
                Column(modifier = Modifier.padding(start = 48.dp, top = 8.dp)) {
                    HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))
                    Text(detalle, fontSize = 14.sp, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Progreso de la tarea: ${(sliderValue * 100).toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Slider(value = sliderValue, onValueChange = { sliderValue = it }, modifier = Modifier.fillMaxWidth())
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        OutlinedButton(onClick = { expandido = false }, modifier = Modifier.padding(end = 8.dp)) { Text("Cerrar") }
                        Button(onClick = { completado = true; expandido = false }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7))) { Text("Finalizar") }
                    }
                }
            }
        }
    }
}