package com.example.proyectodesdisint.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ListAlt
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.proyectodesdisint.ui.youtube.YouTubeSuggestionsCarousel
import com.example.proyectodesdisint.ui.components.LetterAvatar
import com.example.proyectodesdisint.utils.GravatarHelper
import com.example.proyectodesdisint.viewmodel.ProfileViewModel

@Composable
fun HomeWithVideosScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val profile by profileViewModel.profile.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // --- CABECERA ESTILO GLASSMORPHISM ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Bienvenida Personalizada con Inicial
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.clickable { navController.navigate("profile") }) {
                        LetterAvatar(
                            name = profile.nombre,
                            size = 42.dp
                        )
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Hola, ${profile.nombre.split(" ").firstOrNull() ?: "Usuario"}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "¡Qué bueno verte!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                // Botón de Tareas mejorado
                FilledTonalButton(
                    onClick = { /* Acción */ },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                    )
                ) {
                    Icon(Icons.AutoMirrored.Filled.ListAlt, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Tareas", style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        Text(
            text = "Videos recomendados para ti",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(
                start = 20.dp,
                end = 20.dp,
                top = 4.dp,
                bottom = 8.dp
            )
        )

        YouTubeSuggestionsCarousel(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 385.dp)
        )

        HomeScreen(
            navController = navController,
            modifier = Modifier.weight(1f)
        )
    }
}
