package com.example.proyectodesdisint.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectodesdisint.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser
    var currentUserProfile by remember { mutableStateOf<UserProfile?>(null) }
    var users by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        user?.let {
            db.collection("users").document(it.uid).get().addOnSuccessListener { doc ->
                currentUserProfile = doc.toObject(UserProfile::class.java)
            }
        }
        db.collection("users").addSnapshotListener { snapshot, _ ->
            users = snapshot?.documents?.mapNotNull { it.toObject(UserProfile::class.java)?.copy(uid = it.id) } ?: emptyList()
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (currentUserProfile?.isAdmin == true) "Panel de Administración" else "Panel de Usuarios", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(if (currentUserProfile?.isAdmin == true) "Gestión de Usuarios" else "Directorio de Usuarios", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                
                items(users) { user ->
                    UserAdminCard(
                        user = user, 
                        canEdit = currentUserProfile?.isAdmin == true,
                        onRoleChange = { newRole ->
                            db.collection("users").document(user.uid).update("role", newRole)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun UserAdminCard(user: UserProfile, canEdit: Boolean, onRoleChange: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(user.nombre.ifBlank { "Sin nombre" }, fontWeight = FontWeight.Bold)
                Text(user.email, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                
                Spacer(modifier = Modifier.height(8.dp))
                
                if (canEdit) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("ALUMNO", "PROFE", "ADMIN").forEach { role ->
                            FilterChip(
                                selected = user.role.uppercase() == role,
                                onClick = { onRoleChange(role) },
                                label = { Text(role, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }
                } else {
                    Text("Rol: ${user.role}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                }
            }
            
            Icon(
                imageVector = if (user.role.uppercase() == "ADMIN") Icons.Default.Security else Icons.Default.People,
                contentDescription = null,
                tint = if (user.role.uppercase() == "ADMIN") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
            )
        }
    }
}
