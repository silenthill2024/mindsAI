package com.example.proyectodesdisint.ui

import android.app.Application
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.proyectodesdisint.model.UserProfile
import com.example.proyectodesdisint.ui.theme.ThemeState
import com.example.proyectodesdisint.ui.theme.ColorblindType
import com.example.proyectodesdisint.ui.components.LetterAvatar
import com.example.proyectodesdisint.utils.GravatarHelper
import com.example.proyectodesdisint.viewmodel.AuthViewModel
import com.example.proyectodesdisint.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val profileViewModel: ProfileViewModel = viewModel(
        factory = com.example.proyectodesdisint.viewmodel.ProfileViewModelFactory(context.applicationContext as Application)
    )

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let { profileViewModel.uploadProfileImage(it) }
    }

    val savedProfile by profileViewModel.profile.collectAsState()
    val isLoading by profileViewModel.isLoading.collectAsState()
    val message by profileViewModel.message.collectAsState()

    var initialized by remember { mutableStateOf(false) }

    var nombre by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var universidad by remember { mutableStateOf("") }
    var carrera by remember { mutableStateOf("") }
    var gradoEstudio by remember { mutableStateOf("") }
    var semestre by remember { mutableStateOf("") }
    var grupo by remember { mutableStateOf("") }
    var matricula by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }
    var biografia by remember { mutableStateOf("") }
    var materiasInteres by remember { mutableStateOf("") }
    var materiasActuales by remember { mutableStateOf("") }
    var asesorias by remember { mutableStateOf("") }
    var objetivos by remember { mutableStateOf("") }
    var github by remember { mutableStateOf("") }
    var linkedin by remember { mutableStateOf("") }

    var showAcademicDetails by remember { mutableStateOf(false) }

    LaunchedEffect(savedProfile) {
        if (
            savedProfile.uid.isNotBlank() ||
            savedProfile.email.isNotBlank()
        ) {
            nombre = savedProfile.nombre
            username = savedProfile.username
            universidad = savedProfile.universidad
            carrera = savedProfile.carrera
            gradoEstudio = savedProfile.gradoEstudio
            semestre = savedProfile.semestre
            grupo = savedProfile.grupo
            matricula = savedProfile.matricula
            ciudad = savedProfile.ciudad
            biografia = savedProfile.biografia
            materiasInteres =
                savedProfile.materiasInteres.joinToString(", ")
            materiasActuales =
                savedProfile.materiasActuales.joinToString(", ")
            asesorias =
                savedProfile.asesorias.joinToString(", ")
            objetivos =
                savedProfile.objetivos.joinToString(", ")
            github = savedProfile.github
            linkedin = savedProfile.linkedin
            initialized = true
        }
    }

    LaunchedEffect(message) {
        message?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            profileViewModel.clearMessage()
        }
    }

    fun textToList(value: String): List<String> {
        return value
            .split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: "Perfil"
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Perfil",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            // Main Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profile Image with Edit Button
                    Box(
                        modifier = Modifier.size(100.dp),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            if (savedProfile.photoUrl.isNotBlank()) {
                                AsyncImage(
                                    model = savedProfile.photoUrl,
                                    contentDescription = "Perfil",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                LetterAvatar(
                                    name = nombre.ifBlank { "M" },
                                    size = 100.dp
                                )
                            }
                        }
                        // Edit pencil button
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                                .clickable { imagePicker.launch("image/*") }
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Editar",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // User Details
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = nombre.ifBlank { "Usuario MindsAI" },
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Estudiante",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Email, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = savedProfile.email, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(14.dp), tint = Color.Gray)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = if (ciudad.isNotBlank()) ciudad else "México, MX", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                        }
                        if (savedProfile.role.uppercase() != "PROFE") {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.School, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (savedProfile.asesorias.isNotEmpty()) "Tutor: ${savedProfile.asesorias.first()}" else "Sin Asesoría",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Level Badge
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CheckCircle, null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Text("Nivel ${savedProfile.nivel}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        Text("Estudioso", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${savedProfile.xp} XP", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        LinearProgressIndicator(
                            progress = { savedProfile.xp.toFloat() / savedProfile.xpMax },
                            modifier = Modifier.width(50.dp).height(4.dp).clip(CircleShape),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Navigation Button to Tasks (Move above stats)
            Button(
                onClick = { navController.navigate("tasks") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer, contentColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Assignment, null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Gestionar mis Tareas", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Stats Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = if (savedProfile.role.uppercase() == "PROFE") Icons.Default.People else Icons.Default.Book,
                    value = if (savedProfile.role.uppercase() == "PROFE") "${savedProfile.asesorias.size}" else "${savedProfile.materiasCount}",
                    label = if (savedProfile.role.uppercase() == "PROFE") "Alumnos" else "Materias",
                    color = Color(0xFF9C27B0)
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = if (savedProfile.role.uppercase() == "PROFE") Icons.Default.Assignment else Icons.Default.CheckCircle,
                    value = if (savedProfile.role.uppercase() == "PROFE") "${savedProfile.materiasCount}" else "${savedProfile.tareasCompletadas}",
                    label = if (savedProfile.role.uppercase() == "PROFE") "Materiales" else "Tareas",
                    color = Color(0xFF4CAF50),
                    onClick = { if (savedProfile.role.uppercase() != "PROFE") navController.navigate("tasks") }
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Schedule,
                    value = if (savedProfile.role.uppercase() == "PROFE") "Tutor" else "${savedProfile.horasEstudio}h",
                    label = if (savedProfile.role.uppercase() == "PROFE") "Rol" else "Horas",
                    color = Color(0xFFFF9800)
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.TrendingUp,
                    value = if (savedProfile.role.uppercase() == "PROFE") "4.9" else "${savedProfile.promedioGeneral}",
                    label = if (savedProfile.role.uppercase() == "PROFE") "Rating" else "Promedio",
                    color = Color(0xFF2196F3)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Bio Section (User Description)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Sobre mí",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = biografia.ifBlank { "Aún no has añadido una biografía. Cuéntanos sobre tus metas académicas." },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    if (github.isNotBlank() || linkedin.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            if (github.isNotBlank()) {
                                SocialBadge(label = "GitHub", color = Color.Black)
                            }
                            if (linkedin.isNotBlank()) {
                                SocialBadge(label = "LinkedIn", color = Color(0xFF0077B5))
                            }
                        }
                    }
                }
            }

            if (savedProfile.role.uppercase() != "PROFE") {
                Spacer(modifier = Modifier.height(20.dp))
                
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.School, null, tint = MaterialTheme.colorScheme.secondary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Estatus de Asesorías",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        val tutoriasList = savedProfile.asesorias
                        if (tutoriasList.isEmpty()) {
                            Text(
                                "Actualmente no cuentas con asesorías activas.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        } else {
                            tutoriasList.forEach { asesoria ->
                                Row(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CheckCircle, null, modifier = Modifier.size(16.dp), tint = Color(0xFF4CAF50))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(asesoria, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Save Profile Button
                Button(
                    onClick = {
                        val profile = UserProfile(
                            uid = savedProfile.uid,
                            nombre = nombre.trim(),
                            username = username.trim(),
                            email = savedProfile.email,
                            universidad = universidad.trim(),
                            carrera = carrera.trim(),
                            gradoEstudio = gradoEstudio,
                            semestre = semestre.trim(),
                            grupo = grupo.trim(),
                            matricula = matricula.trim(),
                            ciudad = ciudad.trim(),
                            biografia = biografia.trim(),
                            materiasInteres = textToList(materiasInteres),
                            materiasActuales = textToList(materiasActuales),
                            asesorias = textToList(asesorias),
                            objetivos = textToList(objetivos),
                            github = github.trim(),
                            linkedin = linkedin.trim(),
                            photoUrl = savedProfile.photoUrl,
                            perfilCompleto = savedProfile.perfilCompleto,
                            role = savedProfile.role // Preserve role
                        )
                        profileViewModel.saveProfile(profile)
                    },
                    enabled = initialized && !isLoading,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isLoading) "Guardando..." else "Guardar Cambios", fontWeight = FontWeight.Bold)
                }

                // Academic Info Toggle
                OutlinedButton(
                    onClick = { showAcademicDetails = !showAcademicDetails },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(if (showAcademicDetails) Icons.Default.Edit else Icons.Default.AccountCircle, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (showAcademicDetails) "Finalizar Edición" else "Modificar mis Datos", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            AnimatedVisibility(
                visible = showAcademicDetails,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    ProfileSection(title = "Información Personal") {
                        ProfileField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            label = "Nombre completo"
                        )

                        ProfileField(
                            value = username,
                            onValueChange = { username = it },
                            label = "Nombre de usuario",
                            placeholder = "@usuario"
                        )

                        ProfileField(
                            value = ciudad,
                            onValueChange = { ciudad = it },
                            label = "Ciudad"
                        )

                        ProfileField(
                            value = biografia,
                            onValueChange = { if (it.length <= 300) biografia = it },
                            label = "Biografía",
                            placeholder = "Describe tus intereses y metas académicas",
                            singleLine = false,
                            minLines = 3,
                            supportingText = "${biografia.length}/300 caracteres"
                        )
                    }

                    ProfileSection(title = "Información Académica") {
                        ProfileField(
                            value = universidad,
                            onValueChange = { universidad = it },
                            label = "Universidad"
                        )

                        ProfileField(
                            value = carrera,
                            onValueChange = { carrera = it },
                            label = "Carrera"
                        )
                        
                        if (savedProfile.role.uppercase() != "PROFE") {
                            ProfileField(
                                value = asesorias,
                                onValueChange = { asesorias = it },
                                label = "Tutor o Asesorías actuales",
                                placeholder = "Ej: Matemáticas con Prof. Ruiz"
                            )
                        }

                        StudyLevelSelector(
                            selectedLevel = gradoEstudio,
                            onLevelSelected = { gradoEstudio = it }
                        )

                        ProfileField(
                            value = semestre,
                            onValueChange = { semestre = it },
                            label = "Cuatrimestre o semestre"
                        )

                        ProfileField(
                            value = grupo,
                            onValueChange = { grupo = it },
                            label = "Grupo"
                        )

                        ProfileField(
                            value = matricula,
                            onValueChange = { matricula = it },
                            label = "Matrícula"
                        )
                    }

                    ProfileSection(title = "Intereses y materias") {
                        ProfileField(
                            value = materiasInteres,
                            onValueChange = { materiasInteres = it },
                            label = "Materias de interés",
                            placeholder = "IA, Android, Python, Bases de datos",
                            singleLine = false,
                            minLines = 2,
                            supportingText = "Separa cada elemento con una coma"
                        )

                        ProfileField(
                            value = materiasActuales,
                            onValueChange = { materiasActuales = it },
                            label = "Materias actuales",
                            placeholder = "Desarrollo móvil, IA, Redes",
                            singleLine = false,
                            minLines = 2,
                            supportingText = "Separa cada elemento con una coma"
                        )
                    }

                    ProfileSection(title = "Asesorías y objetivos") {
                        ProfileField(
                            value = asesorias,
                            onValueChange = { asesorias = it },
                            label = "Asesorías que cursas",
                            placeholder = "Matemáticas martes 5 PM, Android jueves 6 PM",
                            singleLine = false,
                            minLines = 2,
                            supportingText = "Separa cada asesoría con una coma"
                        )

                        ProfileField(
                            value = objetivos,
                            onValueChange = { objetivos = it },
                            label = "Objetivos académicos",
                            placeholder = "Graduarme, aprender IA, conseguir estadías",
                            singleLine = false,
                            minLines = 2,
                            supportingText = "Separa cada objetivo con una coma"
                        )
                    }

                    ProfileSection(title = "Perfil profesional") {
                        ProfileField(
                            value = github,
                            onValueChange = { github = it },
                            label = "GitHub",
                            placeholder = "github.com/usuario"
                        )

                        ProfileField(
                            value = linkedin,
                            onValueChange = { linkedin = it },
                            label = "LinkedIn",
                            placeholder = "linkedin.com/in/usuario"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            ProfileSection(title = "Configuración") {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (ThemeState.isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Modo oscuro")
                    }
                    Switch(checked = ThemeState.isDarkTheme, onCheckedChange = { ThemeState.isDarkTheme = it })
                }

                // Configuración de Daltonismo
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                
                Text(
                    "Modo Daltonismo",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    listOf(
                        "Normal" to ColorblindType.NONE,
                        "Prot." to ColorblindType.PROTANOPIA,
                        "Deut." to ColorblindType.DEUTERANOPIA,
                        "Trit." to ColorblindType.TRITANOPIA
                    ).forEach { (label, type) ->
                        FilterChip(
                            selected = ThemeState.colorblindType == type,
                            onClick = { ThemeState.colorblindType = type },
                            label = { Text(label, style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Admin/Professor Panel moved here
                val userRole = savedProfile.role.uppercase()
                val isAdminOrProfe = userRole == "ADMIN" || userRole == "PROFE" || savedProfile.email == "admin@mindsai.com"
                
                if (isAdminOrProfe) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant)
                    
                    TextButton(
                        onClick = { navController.navigate("admin_panel") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = if (userRole == "ADMIN" || savedProfile.email == "admin@mindsai.com") 
                                MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = if (userRole == "ADMIN" || savedProfile.email == "admin@mindsai.com") Icons.Default.Security else Icons.Default.Settings, 
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            if (userRole == "ADMIN" || savedProfile.email == "admin@mindsai.com") "Panel de Administración" else "Panel de Profesor",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = {
                    authViewModel.logout()
                    navController.navigate("login") { popUpTo("home") { inclusive = true } }
                },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cerrar sesión", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(100.dp))
        }

        if (isLoading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
private fun SocialBadge(label: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.2f))
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    value: String,
    label: String,
    color: Color,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .height(120.dp)
            .clickable(enabled = onClick != null) { onClick?.invoke() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = androidx.compose.ui.unit.TextUnit.Unspecified
            )
        }
    }
}

@Composable
private fun ProfileSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            content()
        }
    }
}

@Composable
private fun ProfileField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    singleLine: Boolean = true,
    minLines: Int = 1,
    supportingText: String? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(label)
        },
        placeholder = {
            if (placeholder.isNotBlank()) {
                Text(placeholder)
            }
        },
        supportingText = {
            if (!supportingText.isNullOrBlank()) {
                Text(supportingText)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        singleLine = singleLine,
        minLines = minLines,
        shape = RoundedCornerShape(12.dp),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Sentences
        )
    )
}

@Composable
private fun StudyLevelSelector(
    selectedLevel: String,
    onLevelSelected: (String) -> Unit
) {
    Text(
        text = "Grado de estudio",
        style = MaterialTheme.typography.labelLarge,
        modifier = Modifier.padding(
            top = 8.dp,
            bottom = 6.dp
        )
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf("TSU", "Ingeniería", "Licenciatura").forEach {
            level ->

            FilterChip(
                selected = selectedLevel == level,
                onClick = {
                    onLevelSelected(level)
                },
                label = {
                    Text(level)
                }
            )
        }
    }
}

@Composable
private fun ProfileCompletionCard(
    percentage: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Perfil académico",
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "$percentage%",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = {
                    percentage.coerceIn(0, 100) / 100f
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text =
                    "Completa tu perfil para recibir mejores recomendaciones.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
