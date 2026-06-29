package com.example.mindsai.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ForumScreen() {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Foro", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text("Pregunta, aprende y comparte conocimientos", color = Color.Gray, fontSize = 14.sp)

        Spacer(modifier = Modifier.height(16.dp))

        // Barra de búsqueda
        OutlinedTextField(
            value = "",
            onValueChange = {},
            placeholder = { Text("Buscar preguntas o temas...") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de preguntas
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                PreguntaItem(
                    nombre = "María González",
                    rol = "Estudiante",
                    tema = "Matemáticas",
                    pregunta = "¿Alguien puede explicar la regla de la cadena?",
                    respuestas = "5 respuestas"
                )
                PreguntaItem(
                    nombre = "Carlos Herrera",
                    rol = "Estudiante",
                    tema = "Programación",
                    pregunta = "Error al ejecutar código en Python",
                    respuestas = "3 respuestas"
                )
            }
        }
    }
}

@Composable
fun PreguntaItem(nombre: String, rol: String, tema: String, pregunta: String, respuestas: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(nombre, fontWeight = FontWeight.Bold)
            Text(rol, fontSize = 12.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(8.dp))

            Surface(color = Color(0xFFEDE7F6), shape = RoundedCornerShape(4.dp)) {
                Text(tema, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    fontSize = 12.sp, color = Color(0xFF673AB7))
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(pregunta, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(respuestas, fontSize = 12.sp, color = Color.Gray)
        }
    }
}