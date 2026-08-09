package com.example.proyectodesdisint.ui.homev3

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectodesdisint.data.FirebaseService
import com.example.proyectodesdisint.data.StudentAnalyticsEngine
import com.example.proyectodesdisint.data.TaskVideoRecommendationEngine
import com.example.proyectodesdisint.model.UserProfile
import com.example.proyectodesdisint.ui.homev4.HomeV4Dashboard
import com.example.proyectodesdisint.ui.youtube.YouTubeSuggestion
import com.example.proyectodesdisint.ui.youtube.YouTubeSuggestionsCarousel
import com.example.proyectodesdisint.viewmodel.HomeViewModel
import com.example.proyectodesdisint.viewmodel.HomeViewModelFactory
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeV3Screen(
    navController: NavController
) {
    val context = LocalContext.current
    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(context.applicationContext))
    val tasks by homeViewModel.tasks.collectAsState()
    
    // Obtener el perfil para saber el rol
    val profileViewModel: com.example.proyectodesdisint.viewmodel.ProfileViewModel = viewModel(
        factory = com.example.proyectodesdisint.viewmodel.ProfileViewModelFactory(context.applicationContext as android.app.Application)
    )
    val profile by profileViewModel.profile.collectAsState()

    // Clima real
    var weatherData by remember { mutableStateOf<com.example.proyectodesdisint.data.WeatherData?>(null) }
    LaunchedEffect(profile.ciudad) {
        // Por ahora usamos coordenadas fijas de Guadalajara si la ciudad es GDL, o CDMX como default
        val lat = if (profile.ciudad.contains("Guadalajara", true)) 20.6597 else 19.4326
        val lon = if (profile.ciudad.contains("Guadalajara", true)) -103.3496 else -99.1332
        weatherData = com.example.proyectodesdisint.data.WeatherService.fetchWeather(lat, lon)
    }

    val userRole = profile.role.uppercase()

    // Estado para Admin: cambiar entre vista de Alumno y Profesor
    var adminViewAsProfe by remember { mutableStateOf(false) }

    // Estado para lista de alumnos
    var allStudents by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var isLoadingStudents by remember { mutableStateOf(true) }

    // Cargar alumnos al iniciar
    LaunchedEffect(Unit) {
        FirebaseFirestore.getInstance().collection("users")
            .whereEqualTo("role", "ALUMNO")
            .get()
            .addOnSuccessListener { snapshot ->
                allStudents = snapshot.documents.mapNotNull { it.toObject(UserProfile::class.java)?.copy(uid = it.id) }
                isLoadingStudents = false
            }
            .addOnFailureListener {
                isLoadingStudents = false
            }
    }

    // Estado para asignación de tareas (Profesores)
    var showAssignDialog by remember { mutableStateOf(false) }
    var userList by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var isLoadingUsers by remember { mutableStateOf(false) }

    val recommendedVideos = remember(tasks) {
        TaskVideoRecommendationEngine.recommend(tasks = tasks)
    }

    val activeRole = if (userRole == "ADMIN" && adminViewAsProfe) "PROFE" else userRole

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                HomeV3Header(
                    userName = profile.nombre,
                    role = userRole,
                    activeRole = activeRole,
                    viewAsProfe = adminViewAsProfe,
                    pendingTasks = tasks.count { !it.completado },
                    weatherData = weatherData,
                    onViewTasks = { navController.navigate("tasks") },
                    onToggleAdminView = { adminViewAsProfe = !adminViewAsProfe }
                )
            }

            if (activeRole == "PROFE") {
                // VISTA ESPECÍFICA PARA PROFESOR
                item { ProfessorStatsDashboard(profile) }
                
                item { 
                    Text(
                        "Acciones Rápidas", 
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                item {
                    ProfessorQuickActions(
                        onAssignTask = {
                            isLoadingUsers = true
                            showAssignDialog = true
                            FirebaseFirestore.getInstance().collection("users").get().addOnSuccessListener { snapshot ->
                                userList = snapshot.documents.mapNotNull { it.toObject(UserProfile::class.java)?.copy(uid = it.id) }
                                isLoadingUsers = false
                            }
                        },
                        onManageMaterials = { navController.navigate("materials") },
                        onViewForum = { navController.navigate("blog") }
                    )
                }

                item {
                    SectionHeader("Alumnos Disponibles")
                }
                
                item {
                    AvailableStudentsList(students = allStudents, isLoading = isLoadingStudents)
                }

            } else if (activeRole == "ADMIN") {
                // VISTA ESPECÍFICA PARA ADMINISTRADOR
                item { AdminControlPanel(
                    onManageUsers = { navController.navigate("admin_panel") },
                    onManageMaterials = { navController.navigate("materials") },
                    onManageForum = { navController.navigate("blog") }
                ) }
                
                item { Text(
                    "Estado del Sistema", 
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                ) }
                
                item { SystemHealthCard() }

            } else {
                // VISTA PARA ALUMNO (O ADMIN EN MODO ALUMNO)
                item { HomeV4Dashboard(tasks = tasks, navController = navController) }
                item { YouTubeSection(videos = recommendedVideos) }
                item { 
                    DailyOverviewCard(
                        totalTasks = tasks.size,
                        pendingTasks = tasks.count { !it.completado },
                        completedTasks = tasks.count { it.completado }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    if (showAssignDialog) {
        AssignTaskFlow(
            users = userList,
            isLoading = isLoadingUsers,
            onDismiss = { showAssignDialog = false },
            onConfirm = { targetUids, task ->
                val service = FirebaseService()
                targetUids.forEach { uid ->
                    service.assignTaskToUser(uid, task)
                }
                showAssignDialog = false
            }
        )
    }
}

@Composable
private fun HomeV3Header(
    userName: String,
    role: String,
    activeRole: String,
    viewAsProfe: Boolean,
    pendingTasks: Int,
    weatherData: com.example.proyectodesdisint.data.WeatherData?,
    onViewTasks: () -> Unit,
    onToggleAdminView: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) { 
                Text(
                    text = if (userName.isNotBlank()) "¡Hola, $userName!" else "¡Hola!",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = when {
                        activeRole == "PROFE" -> "Gestión Docente"
                        activeRole == "ADMIN" -> "Panel de Control"
                        else -> if (pendingTasks == 0) "Todo al día por hoy" else "Tienes $pendingTasks tarea(s) pendiente(s)"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                WeatherWidget(weatherData)
                
                if (role == "ADMIN") {
                    Spacer(Modifier.width(8.dp))
                    IconButton(
                        onClick = onToggleAdminView,
                        modifier = Modifier
                            .size(40.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz, 
                            contentDescription = "Cambiar Vista",
                            tint = if (viewAsProfe) MaterialTheme.colorScheme.primary else Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        // Nuevo diseño del botón de tareas para alumnos, ubicado debajo del saludo para mejor accesibilidad
        if (role != "PROFE" && !(role == "ADMIN" && viewAsProfe)) {
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = onViewTasks,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Assignment, null)
                Spacer(Modifier.width(8.dp))
                Text(
                    "Ver mis Tareas Pendientes", 
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
    }
}

@Composable
private fun WeatherWidget(weather: com.example.proyectodesdisint.data.WeatherData?) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = weather?.temp ?: "24°C",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = weather?.description ?: "Cargando...",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
            Spacer(Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.WbSunny,
                contentDescription = null,
                tint = Color(0xFFFFB300),
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
private fun ProfessorStatsDashboard(profile: com.example.proyectodesdisint.model.UserProfile) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Analytics, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text("Panel de Rendimiento", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                ProfessorStatItem("Alumnos", "${profile.asesorias.size}", MaterialTheme.colorScheme.primary)
                ProfessorStatItem("Materiales", "${profile.materiasCount}", MaterialTheme.colorScheme.secondary)
                ProfessorStatItem("Tutorías", "12", MaterialTheme.colorScheme.tertiary)
                ProfessorStatItem("Calif.", "4.9", Color(0xFFFFB300))
            }
        }
    }
}

@Composable
private fun ProfessorQuickActions(
    onAssignTask: () -> Unit,
    onManageMaterials: () -> Unit,
    onViewForum: () -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            QuickActionCard(
                title = "Asignar\nTarea",
                icon = Icons.Default.Task,
                containerColor = MaterialTheme.colorScheme.primary,
                onClick = onAssignTask
            )
        }
        item {
            QuickActionCard(
                title = "Material\nApoyo",
                icon = Icons.Default.MenuBook,
                containerColor = MaterialTheme.colorScheme.secondary,
                onClick = onManageMaterials
            )
        }
        item {
            QuickActionCard(
                title = "Foro\nDocente",
                icon = Icons.Default.Forum,
                containerColor = MaterialTheme.colorScheme.tertiary,
                onClick = onViewForum
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    containerColor: Color,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(130.dp).height(120.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, tint = Color.White, modifier = Modifier.size(24.dp))
            }
            Text(
                title, 
                style = MaterialTheme.typography.titleSmall, 
                color = Color.White, 
                fontWeight = FontWeight.Bold, 
                lineHeight = androidx.compose.ui.unit.TextUnit.Unspecified
            )
        }
    }
}

@Composable
private fun AdminControlPanel(
    onManageUsers: () -> Unit,
    onManageMaterials: () -> Unit,
    onManageForum: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.AdminPanelSettings, null, tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(8.dp))
            Text("Gestión de Plataforma", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        }

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                QuickActionCard(
                    title = "Usuarios\ny Roles",
                    icon = Icons.Default.Groups,
                    containerColor = MaterialTheme.colorScheme.primary,
                    onClick = onManageUsers
                )
            }
            item {
                QuickActionCard(
                    title = "Gestionar\nMaterial",
                    icon = Icons.Default.MenuBook,
                    containerColor = MaterialTheme.colorScheme.secondary,
                    onClick = onManageMaterials
                )
            }
            item {
                QuickActionCard(
                    title = "Gestionar\nForo",
                    icon = Icons.Default.Forum,
                    containerColor = MaterialTheme.colorScheme.tertiary,
                    onClick = onManageForum
                )
            }
        }
    }
}


@Composable
private fun SectionHeader(title: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun AvailableStudentsList(students: List<UserProfile>, isLoading: Boolean) {
    if (isLoading) {
        Box(modifier = Modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else if (students.isEmpty()) {
        Text("No hay alumnos disponibles", modifier = Modifier.padding(20.dp), color = Color.Gray)
    } else {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(students) { student ->
                // Estado para almacenar las tareas de este alumno específico y calcular su rendimiento
                var studentTasks by remember { mutableStateOf<List<com.example.proyectodesdisint.model.Task>>(emptyList()) }
                var isFetching by remember { mutableStateOf(true) }

                LaunchedEffect(student.uid) {
                    val db = FirebaseFirestore.getInstance()
                    db.collection("users").document(student.uid).collection("tareas")
                        .get()
                        .addOnSuccessListener { snapshot ->
                            studentTasks = snapshot.documents.mapNotNull { it.toObject(com.example.proyectodesdisint.model.Task::class.java) }
                            isFetching = false
                        }
                        .addOnFailureListener { isFetching = false }
                }

                val performance = remember(studentTasks) {
                    StudentAnalyticsEngine.analyzeStudentPerformance(studentTasks)
                }

                Card(
                    modifier = Modifier.width(180.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    student.nombre.take(1).uppercase(),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(
                                    student.nombre.ifBlank { "Alumno" },
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                )
                                Text(
                                    "Nivel ${student.nivel}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        
                        Spacer(Modifier.height(16.dp))
                        
                        // Barra de Progreso Analítica
                        Text(
                            text = "Rendimiento: ${(performance.progress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { performance.progress },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                            color = Color(performance.statusColor),
                            trackColor = Color(performance.statusColor).copy(alpha = 0.2f)
                        )
                        
                        Spacer(Modifier.height(8.dp))
                        
                        // Status Badge
                        Surface(
                            color = Color(performance.statusColor).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = performance.performanceStatus,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(performance.statusColor),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(Modifier.height(8.dp))
                        
                        Text(
                            text = performance.insights,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray,
                            maxLines = 2,
                            lineHeight = androidx.compose.ui.unit.TextUnit.Unspecified,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SystemHealthCard() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(12.dp).background(Color(0xFF4CAF50), CircleShape))
            Spacer(Modifier.width(12.dp))
            Column {
                Text("Base de Datos Firebase", fontWeight = FontWeight.Bold)
                Text("El servicio de base de datos funciona correctamente", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        }
    }
}

@Composable
private fun ProfessorStatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold, color = color)
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
    }
}

@Composable
private fun YouTubeSection(
    videos: List<YouTubeSuggestion>
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Recursos Académicos",
            style =
                MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color =
                MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(
                horizontal = 20.dp,
                vertical = 8.dp
            )
        )

        YouTubeSuggestionsCarousel(
            videos = videos,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun DailyOverviewCard(
    totalTasks: Int,
    pendingTasks: Int,
    completedTasks: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .background(
                color =
                    MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(18.dp)
    ) {
        Text(
            text = "Resumen de hoy",
            style =
                MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color =
                MaterialTheme.colorScheme.onSurface
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text =
                "Total: $totalTasks  |  Pendientes: $pendingTasks  |  Completadas: $completedTasks",
            style =
                MaterialTheme.typography.bodyMedium,
            color =
                MaterialTheme.colorScheme
                    .onSurfaceVariant
        )
    }
}

@Composable
fun AssignTaskFlow(
    users: List<UserProfile>,
    isLoading: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (List<String>, com.example.proyectodesdisint.model.Task) -> Unit
) {
    var selectedUsers by remember { mutableStateOf(setOf<String>()) }
    var step by remember { mutableIntStateOf(0) } // 0: Select Users, 1: Task Info
    
    var titulo by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var fecha by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())) }
    var hora by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (step == 0) "Seleccionar Destinatarios" else "Detalles de la Tarea") },
        text = {
            if (step == 0) {
                if (isLoading) {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    Column {
                        Text("Elige a quién enviar esta tarea:", style = MaterialTheme.typography.bodySmall)
                        Spacer(Modifier.height(8.dp))
                        LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                            items(users.size) { index ->
                                val user = users[index]
                                val isSelected = selectedUsers.contains(user.uid)
                                Row(
                                    modifier = Modifier.fillMaxWidth().clickable {
                                        selectedUsers = if (isSelected) selectedUsers - user.uid else selectedUsers + user.uid
                                    }.padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(checked = isSelected, onCheckedChange = {
                                        selectedUsers = if (it) selectedUsers + user.uid else selectedUsers - user.uid
                                    })
                                    Text(user.nombre.ifBlank { user.email }, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = titulo, onValueChange = { titulo = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = descripcion, onValueChange = { descripcion = it }, label = { Text("Instrucciones") }, modifier = Modifier.fillMaxWidth(), minLines = 2)
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = fecha, 
                            onValueChange = { fecha = it }, 
                            label = { Text("Fecha (AAAA-MM-DD)") }, 
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("2024-12-31") }
                        )
                        OutlinedTextField(
                            value = hora, 
                            onValueChange = { newValue ->
                                // Solo permitir números y ':'
                                if (newValue.all { it.isDigit() || it == ':' } && newValue.length <= 5) {
                                    hora = newValue
                                }
                            }, 
                            label = { Text("Hora (HH:MM)") }, 
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("14:00") },
                            singleLine = true
                        )
                    }
                }
            }
        },
        confirmButton = {
            if (step == 0) {
                Button(
                    onClick = { step = 1 },
                    enabled = selectedUsers.isNotEmpty()
                ) { Text("Siguiente (${selectedUsers.size})") }
            } else {
                Button(
                    onClick = {
                        if (titulo.isNotBlank()) {
                            onConfirm(selectedUsers.toList(), com.example.proyectodesdisint.model.Task(
                                titulo = titulo, 
                                descripcion = descripcion,
                                fecha = fecha,
                                hora = hora
                            ))
                        }
                    }
                ) { Text("Asignar Tarea") }
            }
        },
        dismissButton = {
            TextButton(onClick = { if (step == 1) step = 0 else onDismiss() }) {
                Text(if (step == 1) "Atrás" else "Cancelar")
            }
        }
    )
}