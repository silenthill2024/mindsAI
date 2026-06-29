package com.example.mindsai.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Encabezado
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("¡Hola, José! 👋", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("Tienes 3 tareas pendientes para hoy.", color = Color.Gray)
            }
            // Simulación de icono de notificaciones y avatar
            Surface(modifier = Modifier.size(40.dp), shape = RoundedCornerShape(20.dp), color = Color.LightGray) {}
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Card Próximo evento
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F5FF)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Próximo evento", color = Color(0xFF673AB7), fontWeight = FontWeight.Bold)
                    Text("Examen de Programación", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Viernes, 7 de junio • 10:00 AM", fontSize = 12.sp)
                }
                Text("2\ndías\nrestantes", fontWeight = FontWeight.Bold, color = Color(0xFF673AB7))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Actividades de hoy
        Text("Actividades de hoy", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(10.dp))

        // Items de actividades (puedes crear un componente para esto)
        ActividadItem("Matemáticas", "Tarea: Ejercicios 1-20", "4:00 PM")
        ActividadItem("Bases de Datos", "Proyecto final", "6:00 PM")
        ActividadItem("Física", "Repaso para parcial", "8:00 PM")
    }
}

@Composable
fun ActividadItem(titulo: String, sub: String, hora: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(40.dp), shape = RoundedCornerShape(8.dp), color = Color(0xFFF0F0F0)) {}
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(titulo, fontWeight = FontWeight.Bold)
                Text(sub, fontSize = 12.sp, color = Color.Gray)
            }
            Text(hora, fontWeight = FontWeight.Bold, color = Color(0xFF673AB7))
        }
    }
}