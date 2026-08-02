package com.example.proyectodesdisint.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectodesdisint.data.FirebaseService
import com.example.proyectodesdisint.model.User
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ManagementDashboard(navController: NavController) {
    val service = remember { FirebaseService() }
    var currentUserRole by remember { mutableStateOf("ALUMNO") }
    var users by remember { mutableStateOf<List<User>>(emptyList()) }
    var selectedTab by remember { mutableIntStateOf(0) }
    
    val authUser = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(Unit) {
        authUser?.let {
            val profile = service.getUserProfile(it.uid)
            currentUserRole = profile?.role ?: "ALUMNO"
        }
        service.listenAllUsers { users = it }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Surface(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = if (currentUserRole == "ADMIN") "Panel de Administración" else "Panel de Profesor",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Gestiona el contenido y los privilegios de la plataforma.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        // Tabs (Solo si es Admin ve la gestión de usuarios)
        TabRow(selectedTabIndex = selectedTab) {
            Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                Text("Contenido", modifier = Modifier.padding(12.dp))
            }
            if (currentUserRole == "ADMIN") {
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                    Text("Usuarios", modifier = Modifier.padding(12.dp))
                }
            }
        }

        when (selectedTab) {
            0 -> ContentManagement(navController)
            1 -> UserManagement(users, service)
        }
    }
}

@Composable
fun ContentManagement(navController: NavController) {
    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ManagementCard(
            title = "Material de Apoyo",
            subtitle = "Sube PDFs, guías y enlaces",
            icon = Icons.AutoMirrored.Filled.MenuBook,
            onClick = { navController.navigate("material") }
        )
        ManagementCard(
            title = "Moderación de Blog",
            subtitle = "Revisa publicaciones de la comunidad",
            icon = Icons.Default.ChatBubbleOutline,
            onClick = { navController.navigate("blog") }
        )
    }
}

@Composable
fun UserManagement(users: List<User>, service: FirebaseService) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(users) { user ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(user.nombre, fontWeight = FontWeight.Bold)
                        Text(user.email, style = MaterialTheme.typography.bodySmall)
                        Text("Rol actual: ${user.role}", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelSmall)
                    }
                    
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.MoreVert, null)
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            DropdownMenuItem(
                                text = { Text("Hacer Profe") },
                                onClick = { service.updateUserRole(user.uid, "PROFE"); expanded = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Hacer Admin") },
                                onClick = { service.updateUserRole(user.uid, "ADMIN"); expanded = false }
                            )
                            DropdownMenuItem(
                                text = { Text("Hacer Alumno") },
                                onClick = { service.updateUserRole(user.uid, "ALUMNO"); expanded = false }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ManagementCard(title: String, subtitle: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(16.dp))
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Filled.ArrowForwardIos, null, modifier = Modifier.size(16.dp))
        }
    }
}
