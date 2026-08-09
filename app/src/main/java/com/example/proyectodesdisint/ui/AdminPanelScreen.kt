package com.example.proyectodesdisint.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.proyectodesdisint.data.FirebaseService
import com.example.proyectodesdisint.model.Task
import com.example.proyectodesdisint.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser
    var currentUserProfile by remember { mutableStateOf<UserProfile?>(null) }
    var allUsers by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    
    var showAssignDialog by remember { mutableStateOf(false) }
    var selectedUserForTask by remember { mutableStateOf<UserProfile?>(null) }

    val filteredUsers = remember(allUsers, searchQuery) {
        if (searchQuery.isBlank()) allUsers
        else allUsers.filter { 
            it.nombre.contains(searchQuery, ignoreCase = true) || 
            it.email.contains(searchQuery, ignoreCase = true) 
        }
    }

    LaunchedEffect(Unit) {
        user?.let {
            db.collection("users").document(it.uid).get().addOnSuccessListener { doc ->
                currentUserProfile = doc.toObject(UserProfile::class.java)
            }
        }
        db.collection("users").addSnapshotListener { snapshot, _ ->
            allUsers = snapshot?.documents?.mapNotNull { it.toObject(UserProfile::class.java)?.copy(uid = it.id) } ?: emptyList()
            isLoading = false
        }
    }

    val isAdmin = currentUserProfile?.isAdmin == true

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Control Central", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                        Text(if (isAdmin) "Administrador" else "Directorio", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    Icon(
                        Icons.Default.AdminPanelSettings, 
                        contentDescription = null, 
                        modifier = Modifier.padding(end = 16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(strokeWidth = 3.dp)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // DASHBOARD SUMMARY
                item {
                    AdminStatsHeader(users = allUsers)
                }

                // SEARCH BAR
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        placeholder = { Text("Buscar usuario por nombre o correo...") },
                        leadingIcon = { Icon(Icons.Default.Search, null) },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                        ),
                        singleLine = true
                    )
                }

                item {
                    Text(
                        text = "Lista de Miembros",
                        modifier = Modifier.padding(start = 24.dp, bottom = 8.dp),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                if (filteredUsers.isEmpty() && searchQuery.isNotEmpty()) {
                    item {
                        Box(Modifier.fillMaxWidth().padding(40.dp), contentAlignment = Alignment.Center) {
                            Text("No se encontraron resultados", color = Color.Gray)
                        }
                    }
                }

                items(filteredUsers) { userItem ->
                    ModernUserCard(
                        user = userItem, 
                        canEdit = isAdmin,
                        isProfeOrAdmin = isAdmin || currentUserProfile?.role?.uppercase() == "PROFE",
                        onRoleChange = { newRole ->
                            db.collection("users").document(userItem.uid).update("role", newRole)
                        },
                        onAssignTask = {
                            selectedUserForTask = userItem
                            showAssignDialog = true
                        }
                    )
                }
            }
        }
    }

    if (showAssignDialog && selectedUserForTask != null) {
        AssignTaskDialog(
            targetUser = selectedUserForTask!!,
            onDismiss = { showAssignDialog = false },
            onConfirm = { task ->
                val service = FirebaseService()
                service.assignTaskToUser(selectedUserForTask!!.uid, task)
                showAssignDialog = false
            }
        )
    }
}

@Composable
private fun AdminStatsHeader(users: List<UserProfile>) {
    val admins = users.count { it.role.uppercase() == "ADMIN" }
    val profes = users.count { it.role.uppercase() == "PROFE" }
    val alumnos = users.count { it.role.uppercase() == "ALUMNO" }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
        ) {
            Row(
                modifier = Modifier.padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatBubble("Total", users.size.toString(), MaterialTheme.colorScheme.primary)
                Divider(modifier = Modifier.height(40.dp).width(1.dp), color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                StatBubble("Alumnos", alumnos.toString(), MaterialTheme.colorScheme.secondary)
                Divider(modifier = Modifier.height(40.dp).width(1.dp), color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                StatBubble("Docentes", profes.toString(), MaterialTheme.colorScheme.tertiary)
            }
        }
    }
}

@Composable
private fun StatBubble(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 24.sp, color = color)
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
    }
}

@Composable
fun ModernUserCard(
    user: UserProfile, 
    canEdit: Boolean, 
    isProfeOrAdmin: Boolean,
    onRoleChange: (String) -> Unit,
    onAssignTask: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = Brush.linearGradient(listOf(Color.Transparent, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // AVATAR SIMULADO
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            brush = Brush.linearGradient(
                                listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        user.nombre.take(1).uppercase(), 
                        color = Color.White, 
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = user.nombre.ifBlank { "Sin nombre" },
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = user.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                if (isProfeOrAdmin) {
                    FilledIconButton(
                        onClick = onAssignTask,
                        modifier = Modifier.size(36.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Assignment, null, modifier = Modifier.size(18.dp))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // ROLE CHIP
                Surface(
                    color = when(user.role.uppercase()) {
                        "ADMIN" -> Color(0xFFFFEBEE)
                        "PROFE" -> Color(0xFFE3F2FD)
                        else -> Color(0xFFF1F8E9)
                    },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (user.role.uppercase() == "ADMIN") Icons.Default.Security else Icons.Default.People,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = when(user.role.uppercase()) {
                                "ADMIN" -> Color(0xFFD32F2F)
                                "PROFE" -> Color(0xFF1976D2)
                                else -> Color(0xFF388E3C)
                            }
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            user.role.uppercase(),
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = when(user.role.uppercase()) {
                                "ADMIN" -> Color(0xFFD32F2F)
                                "PROFE" -> Color(0xFF1976D2)
                                else -> Color(0xFF388E3C)
                            }
                        )
                    }
                }

                if (canEdit) {
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        RoleActionChip("PROFE", user.role.uppercase() == "PROFE") { onRoleChange("PROFE") }
                        RoleActionChip("ADMIN", user.role.uppercase() == "ADMIN") { onRoleChange("ADMIN") }
                    }
                }
            }
        }
    }
}

@Composable
fun RoleActionChip(label: String, isSelected: Boolean, onClick: () -> Unit) {
    AssistChip(
        onClick = onClick,
        label = { Text(label, fontSize = 10.sp) },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
            labelColor = if (isSelected) Color.White else MaterialTheme.colorScheme.primary
        ),
        border = null
    )
}

@Composable
fun AssignTaskDialog(
    targetUser: UserProfile,
    onDismiss: () -> Unit,
    onConfirm: (Task) -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Add, null, tint = MaterialTheme.colorScheme.primary)
                Spacer(Modifier.width(8.dp))
                Text(
                    "Nueva Tarea para ${targetUser.nombre.split(" ").firstOrNull() ?: ""}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                ) 
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    "La tarea aparecerá en el móvil y smartwatch del alumno.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título") },
                    placeholder = { Text("Ej: Repaso de Cálculo") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Instrucciones") },
                    placeholder = { Text("Describe los pasos...") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (titulo.isNotBlank()) onConfirm(Task(titulo = titulo, descripcion = desc)) },
                shape = RoundedCornerShape(12.dp)
            ) { Text("Confirmar Envío") }
        },
        dismissButton = { 
            TextButton(onClick = onDismiss) { Text("Cancelar") } 
        },
        shape = RoundedCornerShape(28.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}
