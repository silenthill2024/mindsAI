package com.example.proyectodesdisint.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectodesdisint.model.UserProfile
import com.example.proyectodesdisint.ui.theme.ThemeState
import com.example.proyectodesdisint.viewmodel.AuthViewModel
import com.example.proyectodesdisint.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(),
    profileViewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current

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
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(105.dp)
                    .clip(CircleShape)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Perfil",
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = nombre.ifBlank { "Usuario MindsAI" },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = savedProfile.email.ifBlank {
                    "Correo no disponible"
                },
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            ProfileCompletionCard(
                percentage = savedProfile.perfilCompleto
            )

            Spacer(modifier = Modifier.height(20.dp))

            ProfileSection(title = "Información personal") {
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
                    onValueChange = {
                        if (it.length <= 300) {
                            biografia = it
                        }
                    },
                    label = "Biografía",
                    placeholder =
                        "Describe tus intereses y metas académicas",
                    singleLine = false,
                    minLines = 3,
                    supportingText =
                        "${biografia.length}/300 caracteres"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            ProfileSection(title = "Información académica") {
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

            Spacer(modifier = Modifier.height(16.dp))

            ProfileSection(title = "Intereses y materias") {
                ProfileField(
                    value = materiasInteres,
                    onValueChange = { materiasInteres = it },
                    label = "Materias de interés",
                    placeholder = "IA, Android, Python, Bases de datos",
                    singleLine = false,
                    minLines = 2,
                    supportingText =
                        "Separa cada elemento con una coma"
                )

                ProfileField(
                    value = materiasActuales,
                    onValueChange = { materiasActuales = it },
                    label = "Materias actuales",
                    placeholder =
                        "Desarrollo móvil, IA, Redes",
                    singleLine = false,
                    minLines = 2,
                    supportingText =
                        "Separa cada elemento con una coma"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            ProfileSection(title = "Asesorías y objetivos") {
                ProfileField(
                    value = asesorias,
                    onValueChange = { asesorias = it },
                    label = "Asesorías que cursas",
                    placeholder =
                        "Matemáticas martes 5 PM, Android jueves 6 PM",
                    singleLine = false,
                    minLines = 2,
                    supportingText =
                        "Separa cada asesoría con una coma"
                )

                ProfileField(
                    value = objetivos,
                    onValueChange = { objetivos = it },
                    label = "Objetivos académicos",
                    placeholder =
                        "Graduarme, aprender IA, conseguir estadías",
                    singleLine = false,
                    minLines = 2,
                    supportingText =
                        "Separa cada objetivo con una coma"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

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

            Spacer(modifier = Modifier.height(16.dp))

            ProfileSection(title = "Configuración") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement =
                        Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector =
                                if (ThemeState.isDarkTheme) {
                                    Icons.Default.DarkMode
                                } else {
                                    Icons.Default.LightMode
                                },
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Text("Modo oscuro")
                    }

                    Switch(
                        checked = ThemeState.isDarkTheme,
                        onCheckedChange = {
                            ThemeState.isDarkTheme = it
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

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
                        materiasInteres =
                            textToList(materiasInteres),
                        materiasActuales =
                            textToList(materiasActuales),
                        asesorias = textToList(asesorias),
                        objetivos = textToList(objetivos),
                        github = github.trim(),
                        linkedin = linkedin.trim(),
                        perfilCompleto =
                            savedProfile.perfilCompleto
                    )

                    profileViewModel.saveProfile(profile)
                },
                enabled = initialized && !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    if (isLoading) {
                        "Guardando..."
                    } else {
                        "Guardar perfil"
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    authViewModel.logout()

                    navController.navigate("login") {
                        popUpTo("home") {
                            inclusive = true
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor =
                        MaterialTheme.colorScheme.error
                )
            ) {
                Icon(
                    imageVector =
                        Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text("Cerrar sesión")
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
