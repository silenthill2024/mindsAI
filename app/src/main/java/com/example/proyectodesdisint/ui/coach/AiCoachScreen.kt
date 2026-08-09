package com.example.proyectodesdisint.ui.coach

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectodesdisint.data.StudentAnalyticsEngine
import com.example.proyectodesdisint.model.Task
import com.example.proyectodesdisint.model.UserProfile
import com.example.proyectodesdisint.viewmodel.HomeViewModel
import com.example.proyectodesdisint.viewmodel.HomeViewModelFactory
import com.example.proyectodesdisint.viewmodel.ProfileViewModel
import com.example.proyectodesdisint.viewmodel.ProfileViewModelFactory
import kotlinx.coroutines.launch

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val time: Long = System.currentTimeMillis()
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiCoachScreen(navController: NavController) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val profileViewModel: ProfileViewModel = viewModel(
        factory = ProfileViewModelFactory(context.applicationContext as android.app.Application)
    )
    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(context.applicationContext))
    
    val profile by profileViewModel.profile.collectAsState()
    val tasks by homeViewModel.tasks.collectAsState()

    val messages = remember { mutableStateListOf<ChatMessage>(
        ChatMessage("¡Hola! Soy MindsAI Coach. Estoy analizando tu perfil académico actual...", false)
    ) }
    
    var inputText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    // Bienvenida personalizada basada en datos reales
    LaunchedEffect(profile.nombre) {
        if (profile.nombre.isNotBlank() && messages.size == 1) {
            messages.add(ChatMessage("¡Hola ${profile.nombre}! Veo que estás en el nivel ${profile.nivel}. ¿En qué puedo ayudarte hoy?", false))
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(8.dp))
                        Text("Coach MindsAI", fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 2.dp,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        placeholder = { Text("Pregúntame algo...") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 4
                    )
                    Spacer(Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                val userMsg = inputText
                                messages.add(ChatMessage(userMsg, true))
                                inputText = ""
                                scope.launch {
                                    listState.animateScrollToItem(messages.size - 1)
                                    messages.add(ChatMessage("Consultando Red Neuronal...", false))
                                    kotlinx.coroutines.delay(1200)
                                    messages.removeAt(messages.size - 1)
                                    
                                    val response = generateRealAiResponse(userMsg, profile, tasks)
                                    messages.add(ChatMessage(response, false))
                                    listState.animateScrollToItem(messages.size - 1)
                                }
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, null)
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { message ->
                ChatBubble(message)
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val alignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    val bgColor = if (message.isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (message.isUser) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
    val shape = if (message.isUser) {
        RoundedCornerShape(16.dp, 16.dp, 2.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 2.dp)
    }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = alignment) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start
        ) {
            if (!message.isUser) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AutoAwesome, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                }
                Spacer(Modifier.width(8.dp))
            }

            Surface(
                color = bgColor,
                shape = shape,
                shadowElevation = 1.dp
            ) {
                Text(
                    text = message.text,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    color = textColor,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (message.isUser) {
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.secondary)
                }
            }
        }
    }
}

private fun generateRealAiResponse(query: String, profile: UserProfile, tasks: List<Task>): String {
    val q = query.lowercase()
    val performance = StudentAnalyticsEngine.analyzeStudentPerformance(tasks)
    val pendingCount = tasks.count { !it.completado }

    return when {
        q.contains("rendimiento") || q.contains("progreso") || q.contains("cómo voy") -> {
            "Según mi análisis neural, tu rendimiento es ${performance.performanceStatus}. Tienes un progreso del ${(performance.progress * 100).toInt()}% y mi consejo es: ${performance.insights}"
        }
        q.contains("tarea") || q.contains("pendiente") -> {
            if (pendingCount > 0) {
                "Tienes $pendingCount tareas pendientes. La red neuronal sugiere enfocarte en terminar al menos una hoy para mantener tu racha."
            } else {
                "¡Excelente! No tienes tareas pendientes. Es un buen momento para repasar tus materias de interés: ${profile.materiasInteres.joinToString(", ")}."
            }
        }
        q.contains("nivel") || q.contains("xp") -> {
            "Estás en el nivel ${profile.nivel} con ${profile.xp} XP. Te faltan ${profile.xpMax - profile.xp} XP para subir de nivel. ¡Sigue así!"
        }
        q.contains("consejo") || q.contains("ayuda") -> {
            "Basado en tu perfil de ${profile.carrera}, te recomiendo dedicar 30 minutos extras a ${if (profile.materiasActuales.isNotEmpty()) profile.materiasActuales.random() else "tus estudios"} hoy."
        }
        else -> "Analizando tu consulta... Como estudiante de ${profile.universidad}, mi recomendación es mantener el equilibrio entre tus $pendingCount tareas y tus objetivos académicos."
    }
}
