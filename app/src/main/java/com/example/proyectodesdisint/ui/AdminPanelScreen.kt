package com.example.proyectodesdisint.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assignment
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
import com.example.proyectodesdisint.data.FirebaseService
import com.example.proyectodesdisint.model.Task
import com.example.proyectodesdisint.model.UserProfile
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(navController: NavController) {
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val user = auth.currentUser
    var currentUserProfile by remember { mutableStateOf<UserProfile?>(null) }
    var userList by remember { mutableStateOf<List<UserProfile>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    
    var showAssignDialog by remember { mutableStateOf(false) }
    var selectedUserForTask by remember { mutableStateOf<UserProfile?>(null) }

    LaunchedEffect(Unit) {
        user?.let {
            db.collection("users").document(it.uid).get().addOnSuccessListener { doc ->
                currentUserProfile = doc.toObject(UserProfile::class.java)
            }
        }
        db.collection("users").addSnapshotListener { snapshot, error ->
            if (error != null) {
                isLoading = false
                return@addSnapshotListener
            }
            if (snapshot != null) {
                userList = snapshot.toObjects(UserProfile::class.java)
            }
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Panel de Administración") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(userList) { itemUser ->
                    UserItem(
                        user = itemUser,
                        canEdit = currentUserProfile?.role == "ADMIN",
                        onAssignTask = {
                            selectedUserForTask = itemUser
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
fun UserItem(
    user: UserProfile, 
    canEdit: Boolean, 
    onAssignTask: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (user.role.uppercase() == "ADMIN") Icons.Default.Security else Icons.Default.People,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = if (user.role.uppercase() == "ADMIN") MaterialTheme.colorScheme.primary else Color.Gray
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = user.nombre.ifEmpty { "Usuario sin nombre" }, fontWeight = FontWeight.Bold)
                Text(text = user.email, style = MaterialTheme.typography.bodySmall)
                Text(
                    text = "Rol: ${user.role}", 
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            if (canEdit) {
                IconButton(onClick = onAssignTask) {
                    Icon(Icons.Default.Assignment, contentDescription = "Asignar Tarea")
                }
            }
        }
    }
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
        title = { Text("Asignar Tarea a ${targetUser.nombre}") },
        text = {
            Column {
                OutlinedTextField(
                    value = titulo,
                    onValueChange = { titulo = it },
                    label = { Text("Título") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = desc,
                    onValueChange = { desc = it },
                    label = { Text("Descripción") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(Task(titulo = titulo, descripcion = desc))
                },
                enabled = titulo.isNotBlank()
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}
