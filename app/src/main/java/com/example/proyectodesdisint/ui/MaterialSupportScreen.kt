package com.example.proyectodesdisint.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectodesdisint.data.MaterialRepository
import com.example.proyectodesdisint.model.MaterialApoyo
import com.example.proyectodesdisint.model.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MaterialSupportScreen(
    navController: NavController
) {
    val repository = remember {
        MaterialRepository()
    }

    val firebaseUser =
        FirebaseAuth.getInstance().currentUser

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember {
        SnackbarHostState()
    }

    var materials by remember {
        mutableStateOf<List<MaterialApoyo>>(emptyList())
    }

    var currentUser by remember {
        mutableStateOf(
            User(
                uid = firebaseUser?.uid.orEmpty(),
                role = "ALUMNO"
            )
        )
    }

    var loading by remember {
        mutableStateOf(true)
    }

    var showAddDialog by remember {
        mutableStateOf(false)
    }

    DisposableEffect(firebaseUser?.uid) {
        val materialsListener =
            repository.listenMaterials(
                onResult = {
                    materials = it
                    loading = false
                },
                onError = {
                    loading = false
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            "No se pudieron cargar los materiales"
                        )
                    }
                }
            )

        val roleListener =
            firebaseUser?.uid
                ?.takeIf { it.isNotBlank() }
                ?.let { uid ->
                    repository.listenUserRole(
                        uid = uid,
                        onResult = {
                            currentUser = it
                        },
                        onError = {
                            currentUser = User(
                                uid = uid,
                                role = "ALUMNO"
                            )
                        }
                    )
                }

        onDispose {
            materialsListener.remove()
            roleListener?.remove()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Material de apoyo",
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = when (
                                currentUser.role.uppercase()
                            ) {
                                "ADMIN" ->
                                    "Administrador"

                                "PROFE" ->
                                    "Profesor"

                                else ->
                                    "Alumno · Solo lectura"
                            },
                            style =
                                MaterialTheme.typography.labelSmall,
                            color =
                                MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState
            )
        },
        floatingActionButton = {
            if (currentUser.canManageMaterials) {
                FloatingActionButton(
                    onClick = {
                        showAddDialog = true
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription =
                            "Agregar material"
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(
                            Alignment.Center
                        )
                    )
                }

                materials.isEmpty() -> {
                    EmptyMaterialsMessage(
                        canManage =
                            currentUser.canManageMaterials,
                        modifier = Modifier.align(
                            Alignment.Center
                        )
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding =
                            androidx.compose.foundation.layout
                                .PaddingValues(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = 12.dp,
                                    bottom = 100.dp
                                ),
                        verticalArrangement =
                            Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = materials,
                            key = { material ->
                                material.id
                            }
                        ) { material ->
                            MaterialCard(
                                material = material,
                                canManage =
                                    currentUser
                                        .canManageMaterials,
                                onOpen = {
                                    val link =
                                        normalizeUrl(
                                            material.enlace
                                        )

                                    try {
                                        context.startActivity(
                                            Intent(
                                                Intent.ACTION_VIEW,
                                                Uri.parse(link)
                                            )
                                        )
                                    } catch (
                                        exception: Exception
                                    ) {
                                        scope.launch {
                                            snackbarHostState
                                                .showSnackbar(
                                                    "No se pudo abrir el enlace"
                                                )
                                        }
                                    }
                                },
                                onDelete = {
                                    scope.launch {
                                        val result =
                                            repository
                                                .deleteMaterial(
                                                    material.id
                                                )

                                        snackbarHostState
                                            .showSnackbar(
                                                if (
                                                    result.isSuccess
                                                ) {
                                                    "Material eliminado"
                                                } else {
                                                    "No se pudo eliminar"
                                                }
                                            )
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddMaterialDialog(
            onDismiss = {
                showAddDialog = false
            },
            onSave = { title, description, link ->
                val material = MaterialApoyo(
                    titulo = title.trim(),
                    descripcion =
                        description.trim(),
                    enlace = normalizeUrl(
                        link.trim()
                    ),
                    tipo = "LINK",
                    autorId =
                        firebaseUser?.uid.orEmpty(),
                    autorNombre =
                        currentUser.nombre
                            .ifBlank {
                                currentUser.role
                            }
                )

                scope.launch {
                    val result =
                        repository.addLinkMaterial(
                            material
                        )

                    if (result.isSuccess) {
                        showAddDialog = false

                        snackbarHostState.showSnackbar(
                            "Material publicado"
                        )
                    } else {
                        snackbarHostState.showSnackbar(
                            "No se pudo publicar"
                        )
                    }
                }
            }
        )
    }
}

@Composable
private fun MaterialCard(
    material: MaterialApoyo,
    canManage: Boolean,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Link,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(
                modifier = Modifier.padding(
                    horizontal = 6.dp
                )
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = material.titulo,
                    style =
                        MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                if (material.descripcion.isNotBlank()) {
                    Text(
                        text = material.descripcion,
                        style =
                            MaterialTheme.typography.bodySmall,
                        color =
                            MaterialTheme.colorScheme
                                .onSurfaceVariant
                    )
                }

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "Abrir recurso",
                    style =
                        MaterialTheme.typography.labelMedium,
                    color =
                        MaterialTheme.colorScheme.primary
                )
            }

            if (canManage) {
                IconButton(
                    onClick = onDelete
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription =
                            "Eliminar material",
                        tint =
                            MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyMaterialsMessage(
    canManage: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {
        Text(
            text = "No hay materiales disponibles",
            style =
                MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = if (canManage) {
                "Presiona + para publicar el primer enlace."
            } else {
                "El profesor todavía no ha publicado recursos."
            },
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun AddMaterialDialog(
    onDismiss: () -> Unit,
    onSave: (
        title: String,
        description: String,
        link: String
    ) -> Unit
) {
    var title by remember {
        mutableStateOf("")
    }

    var description by remember {
        mutableStateOf("")
    }

    var link by remember {
        mutableStateOf("")
    }

    val valid =
        title.isNotBlank() &&
        link.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Publicar material")
        },
        text = {
            Column(
                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                    },
                    label = {
                        Text("Título")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = {
                        description = it
                    },
                    label = {
                        Text("Descripción")
                    },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = link,
                    onValueChange = {
                        link = it
                    },
                    label = {
                        Text("Enlace")
                    },
                    placeholder = {
                        Text("https://...")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                enabled = valid,
                onClick = {
                    onSave(
                        title,
                        description,
                        link
                    )
                }
            ) {
                Text("Publicar")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("Cancelar")
            }
        }
    )
}

private fun normalizeUrl(
    value: String
): String {
    val trimmed = value.trim()

    return when {
        trimmed.startsWith(
            "https://",
            ignoreCase = true
        ) -> trimmed

        trimmed.startsWith(
            "http://",
            ignoreCase = true
        ) -> trimmed

        else -> "https://$trimmed"
    }
}
