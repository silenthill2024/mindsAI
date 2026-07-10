package com.example.mindsai.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumScreen() {
    var searchQuery by remember { mutableStateOf("") }
    var isMenuExpanded by remember { mutableStateOf(false) }
    var selectedOrder by remember { mutableStateOf("Más recientes") }
    var selectedCategory by remember { mutableStateOf("Hardware") }

    val categories = listOf("Todos", "Hardware", "Software", "Machine Learning", "Bases de Datos")

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF0F2F5))) {
        // Cabecera estática con búsqueda y orden
        Column(modifier = Modifier.background(Color.White).padding(16.dp)) {
            Text("Comunidad Académica", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar discusiones...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.weight(1f)) {
                    items(categories.size) { index ->
                        FilterChip(
                            selected = selectedCategory == categories[index],
                            onClick = { selectedCategory = categories[index] },
                            label = { Text(categories[index]) }
                        )
                    }
                }

                Box {
                    IconButton(onClick = { isMenuExpanded = true }) { Icon(Icons.Default.MoreVert, contentDescription = "Ordenar") }
                    DropdownMenu(expanded = isMenuExpanded, onDismissRequest = { isMenuExpanded = false }) {
                        DropdownMenuItem(text = { Text("Más recientes") }, onClick = { selectedOrder = "Más recientes"; isMenuExpanded = false })
                        DropdownMenuItem(text = { Text("Más votados") }, onClick = { selectedOrder = "Más votados"; isMenuExpanded = false })
                        DropdownMenuItem(text = { Text("Sin respuesta") }, onClick = { selectedOrder = "Sin respuesta"; isMenuExpanded = false })
                    }
                }
            }
        }

        // Lista de Posteos Interactivos
        LazyColumn(contentPadding = PaddingValues(16.dp, bottom = 80.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item { ForoPost(156, "Duda con Jetpack Compose y ViewModels", "Estoy intentando mantener el estado de unos checkboxes pero al hacer scroll en el LazyColumn se pierden. ¿Alguien sabe cómo solucionarlo?", "Software", "Erick") }
            item { ForoPost(42, "¿Archivos correctos para RB3Enhanced en Wii?", "Estoy modificando mi consola. Sé que la carpeta no es rb3_dlc, pero ¿cuáles son los archivos .dol correctos? ¿SZBE69 y SZBP69?", "Hardware", "Rafa") }
            item { ForoPost(89, "Ayuda con módulo inalámbrico", "Tengo problemas para desoldar el módulo WiFi de una placa de Xbox 360, ¿qué temperatura de cautín recomiendan?", "Hardware", "Alex") }
            item { ForoPost(210, "Optimización de consultas PL/SQL", "Mi paquete está tardando mucho en ejecutar el backtracking. ¿Es mejor usar cursores o tablas temporales?", "Bases de Datos", "Diana") }
        }
    }
}

@Composable
fun ForoPost(votosIniciales: Int, titulo: String, cuerpo: String, etiqueta: String, autor: String) {
    var votos by remember { mutableStateOf(votosIniciales) }
    var votado by remember { mutableStateOf(0) } // 1 up, -1 down, 0 nada
    var guardado by remember { mutableStateOf(false) }

    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp)) {
            // Columna de Votación
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(end = 12.dp)) {
                IconButton(onClick = { if (votado != 1) { votos += (if (votado == -1) 2 else 1); votado = 1 } else { votos--; votado = 0 } }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.KeyboardArrowUp, tint = if (votado == 1) Color(0xFFFF5722) else Color.Gray, contentDescription = "Upvote")
                }
                Text("$votos", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = if (votado == 1) Color(0xFFFF5722) else if (votado == -1) Color(0xFF2196F3) else Color.Black)
                IconButton(onClick = { if (votado != -1) { votos -= (if (votado == 1) 2 else 1); votado = -1 } else { votos++; votado = 0 } }, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.KeyboardArrowDown, tint = if (votado == -1) Color(0xFF2196F3) else Color.Gray, contentDescription = "Downvote")
                }
            }

            // Contenido del post
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Badge(containerColor = Color(0xFFE3F2FD), contentColor = Color(0xFF1976D2)) { Text(etiqueta, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)) }
                    IconToggleButton(checked = guardado, onCheckedChange = { guardado = it }) {
                        Icon(if (guardado) Icons.Default.Star else Icons.Default.Star, contentDescription = "Guardar", tint = if (guardado) Color(0xFFFFC107) else Color.LightGray)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(titulo, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(cuerpo, fontSize = 14.sp, color = Color.DarkGray, lineHeight = 20.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Publicado por $autor", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("12 comentarios", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}