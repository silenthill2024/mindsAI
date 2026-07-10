package com.example.mindsai.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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

// Agregamos los parámetros aquí arriba
@Composable
fun ProfileScreen(isDarkMode: Boolean = false, onThemeChange: (Boolean) -> Unit = {}) {
    var nombre by remember { mutableStateOf("José Pablo Chávez Ortiz") }
    var biografia by remember { mutableStateOf("Estudiante de Ingeniería UTCJ. Entusiasta del modding (PS3, Wii, Xbox 360) y desarrollo en Kotlin/Python.") }
    var notificaciones by remember { mutableStateOf(true) }
    var horasEstudio by remember { mutableFloatStateOf(4f) }

    // Usamos MaterialTheme.colorScheme.background en lugar de un color estático
    Column(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).verticalScroll(rememberScrollState())) {

        Box(modifier = Modifier.fillMaxWidth().height(220.dp)) {
            Box(modifier = Modifier.fillMaxWidth().height(140.dp).background(MaterialTheme.colorScheme.primary))
            Card(
                shape = CircleShape,
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                modifier = Modifier.size(120.dp).align(Alignment.BottomCenter).offset(y = (-20).dp)
            ) {
                Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(80.dp), tint = MaterialTheme.colorScheme.primary)
                }
            }
        }

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre Completo") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = biografia,
                onValueChange = { biografia = it },
                label = { Text("Biografía / Intereses") },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                shape = RoundedCornerShape(12.dp),
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(24.dp))
            Text("Meta de Estudio Diario", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(8.dp))

            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(16.dp)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Horas planeadas:", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                        Text("${horasEstudio.toInt()} hrs/día", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                    Slider(
                        value = horasEstudio,
                        onValueChange = { horasEstudio = it },
                        valueRange = 1f..12f,
                        steps = 10,
                        colors = SliderDefaults.colors(thumbColor = MaterialTheme.colorScheme.primary, activeTrackColor = MaterialTheme.colorScheme.primary)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Preferencias del Sistema", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onBackground)
            Spacer(modifier = Modifier.height(8.dp))

            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Notifications, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Notificaciones Push", color = MaterialTheme.colorScheme.onSurface)
                        }
                        Switch(checked = notificaciones, onCheckedChange = { notificaciones = it })
                    }
                    HorizontalDivider()
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Settings, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f))
                            Spacer(modifier = Modifier.width(16.dp))
                            Text("Modo Oscuro", color = MaterialTheme.colorScheme.onSurface)
                        }
                        // ¡AQUÍ ESTÁ LA MAGIA! Activamos la función que nos enviaron desde MainActivity
                        Switch(checked = isDarkMode, onCheckedChange = onThemeChange)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { /* Acción de guardar */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Guardar Cambios", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}