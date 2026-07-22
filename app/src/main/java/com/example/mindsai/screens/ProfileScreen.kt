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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mindsai.local.UserSession
import com.example.mindsai.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    isDarkMode: Boolean = false, 
    onThemeChange: (Boolean) -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val user = UserSession.currentUser
    val scrollState = rememberScrollState()
    var showEditDialog by remember { mutableStateOf(false) }
    
    val headerGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFFEDE7F6), Color(0xFFF8F9FF))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FF))
            .verticalScroll(scrollState)
    ) {
        // 1. Header de Perfil
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(headerGradient)
                .padding(24.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Settings, contentDescription = null, tint = Color.Gray)
                    }
                }
                
                Box(contentAlignment = Alignment.BottomEnd) {
                    Surface(
                        modifier = Modifier.size(100.dp),
                        shape = CircleShape,
                        color = Color(0xFF673AB7).copy(alpha = 0.1f)
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(20.dp), tint = Color(0xFF673AB7))
                    }
                    IconButton(
                        onClick = { showEditDialog = true },
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color.White, CircleShape)
                            .clip(CircleShape)
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF673AB7))
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                Text(user?.nombre ?: "Usuario", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                Text("Nivel ${user?.level ?: 1} - Estudiante", fontSize = 14.sp, color = Color(0xFF673AB7), fontWeight = FontWeight.Bold)
                
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (user?.description.isNullOrBlank()) "Añade una descripción sobre ti..." else user!!.description,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(horizontal = 32.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 12.dp)) {
                    Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(user?.correo ?: "correo@mindsai.com", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }

        // 2. XP & Level Card
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(45.dp), shape = CircleShape, color = Color(0xFF673AB7)) {
                    Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color.White, modifier = Modifier.padding(10.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Progreso de Nivel", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    val xpProgress = (user?.xp?.toFloat() ?: 0f) / 1000f // Ejemplo 1000 XP por nivel
                    LinearProgressIndicator(
                        progress = { xpProgress.coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                        color = Color(0xFF673AB7),
                        trackColor = Color(0xFFEDE7F6)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text("${user?.xp ?: 0} XP", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = Color(0xFF673AB7))
            }
        }

        // 3. Stats Row
        Row(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StatItemLarge(Icons.Default.CheckCircle, "${user?.tasksCompleted ?: 0}", "Tareas")
            StatItemLarge(Icons.Default.Schedule, "${user?.studyHours ?: 0}h", "Estudio")
            StatItemLarge(Icons.Default.TrendingUp, "4.8", "Promedio")
        }

        // 6. Cuenta
        Text("Mi Cuenta", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(horizontal = 24.dp))
        Card(
            modifier = Modifier.fillMaxWidth().padding(24.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                AccountRow(Icons.Default.Badge, "Editar información", onClick = { showEditDialog = true })
                AccountRow(Icons.Default.NotificationsNone, "Notificaciones")
                AccountRow(Icons.Default.Shield, "Seguridad")
            }
        }

        // 7. Cerrar Sesión
        Button(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth().padding(24.dp).height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE), contentColor = Color.Red)
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cerrar sesión", fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(100.dp))
    }

    if (showEditDialog) {
        EditProfileDialog(
            currentName = user?.nombre ?: "",
            currentDesc = user?.description ?: "",
            onDismiss = { showEditDialog = false },
            onSave = { name, desc ->
                viewModel.updateProfile(name, desc)
                showEditDialog = false
            }
        )
    }
}

@Composable
fun EditProfileDialog(
    currentName: String,
    currentDesc: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var desc by remember { mutableStateOf(currentDesc) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Editar Perfil", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(name, desc) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF673AB7))
            ) {
                Text("Guardar Cambios")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = Color.Gray)
            }
        }
    )
}

@Composable
fun StatItemLarge(icon: ImageVector, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(modifier = Modifier.size(45.dp), shape = RoundedCornerShape(12.dp), color = Color(0xFFEDE7F6)) {
            Icon(icon, contentDescription = null, tint = Color(0xFF673AB7), modifier = Modifier.padding(12.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(label, fontSize = 10.sp, color = Color.Gray)
    }
}

@Composable
fun AccountRow(icon: ImageVector, label: String, onClick: () -> Unit = {}) {
    Surface(
        onClick = onClick,
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Text(label, fontSize = 14.sp)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}
