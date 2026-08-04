package com.example.proyectodesdisint.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.SubcomposeAsyncImage
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectodesdisint.viewmodel.AuthViewModel
import com.example.proyectodesdisint.ui.theme.ThemeState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import com.example.proyectodesdisint.viewmodel.HomeViewModel
import com.example.proyectodesdisint.viewmodel.HomeViewModelFactory
import com.google.firebase.auth.FirebaseAuth

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory(context))
    val tasks by homeViewModel.tasks.collectAsState()
    val completedCount = tasks.count { it.completado }

    val userProfile by authViewModel.userProfile.collectAsState()
    val scrollState = rememberScrollState()
    var isUploadingPhoto by remember { mutableStateOf(false) }

    var isEditing by remember { mutableStateOf(false) }
    var editNombre by remember { mutableStateOf("") }
    var editDescripcion by remember { mutableStateOf("") }
    
    val photoLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            isUploadingPhoto = true
            authViewModel.updateProfile(
                nombre = userProfile?.nombre ?: "",
                descripcion = userProfile?.descripcion ?: "",
                profileImageUri = it
            )
        }
    }

    // Reset uploading state when profile updates
    LaunchedEffect(userProfile) {
        isUploadingPhoto = false
        userProfile?.let {
            editNombre = it.nombre
            editDescripcion = it.descripcion
        }
    }

    val level = (completedCount / 5) + 1
    val progressToNext = (completedCount % 5) / 5.0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- SECCIÓN HEADER ---
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .clickable { photoLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (userProfile?.profileImageUrl != null) {
                SubcomposeAsyncImage(
                    model = userProfile?.profileImageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    loading = {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        }
                    },
                    error = {
                        Icon(Icons.Default.Person, null, modifier = Modifier.size(70.dp), tint = MaterialTheme.colorScheme.primary)
                    }
                )
            } else {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(70.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            if (isUploadingPhoto) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(30.dp))
                }
            }

            Surface(
                modifier = Modifier.align(Alignment.BottomEnd).size(36.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                tonalElevation = 6.dp
            ) {
                Icon(Icons.Default.CameraAlt, null, modifier = Modifier.padding(8.dp).size(20.dp), tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            color = when(userProfile?.role) {
                "ADMIN" -> Color(0xFFFFD700) 
                "PROFE" -> MaterialTheme.colorScheme.secondary
                else -> MaterialTheme.colorScheme.tertiaryContainer
            },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(bottom = 4.dp)
        ) {
            Text(
                text = userProfile?.role ?: "ALUMNO",
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if(userProfile?.role == "ADMIN") Color.Black else MaterialTheme.colorScheme.onTertiaryContainer
            )
        }

        if (isEditing) {
            OutlinedTextField(
                value = editNombre,
                onValueChange = { editNombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = editDescripcion,
                onValueChange = { editDescripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
                minLines = 2
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    authViewModel.updateProfile(editNombre, editDescripcion)
                    isEditing = false
                }) { Text("Guardar") }
                OutlinedButton(onClick = { isEditing = false }) { Text("Cancelar") }
            }
        } else {
            Text(
                text = userProfile?.nombre ?: "Usuario MindsAI",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = userProfile?.email ?: FirebaseAuth.getInstance().currentUser?.email ?: "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = userProfile?.descripcion ?: "Explorador de MindsAI. Optimizando mi productividad y claridad mental cada día.",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                    IconButton(onClick = { isEditing = true }, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Stars, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.width(8.dp))
                    Text("Nivel MindsAI: $level", fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
                }
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { progressToNext },
                    modifier = Modifier.fillMaxWidth().height(8.dp),
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                )
                Text("Tareas completadas: $completedCount", style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = 4.dp))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Preferencias",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.align(Alignment.Start),
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (ThemeState.isDarkTheme) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(12.dp))
                        Text("Modo Oscuro", style = MaterialTheme.typography.bodyLarge)
                    }
                    Switch(
                        checked = ThemeState.isDarkTheme,
                        onCheckedChange = { ThemeState.isDarkTheme = it }
                    )
                }

                if (userProfile?.role == "ADMIN" || userProfile?.role == "PROFE") {
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { navController.navigate("management") }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.AdminPanelSettings, null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(12.dp))
                        Text("Panel de Gestión", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = {
                authViewModel.logout()
                navController.navigate("login") {
                    popUpTo("home") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.error),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Logout, null)
            Spacer(Modifier.width(8.dp))
            Text("Cerrar Sesión", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}
