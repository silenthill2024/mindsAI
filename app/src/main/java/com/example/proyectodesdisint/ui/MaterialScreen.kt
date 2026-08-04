package com.example.proyectodesdisint.ui

import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import android.widget.Toast
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.proyectodesdisint.data.FirebaseService
import com.example.proyectodesdisint.model.MaterialApoyo
import com.example.proyectodesdisint.viewmodel.MaterialViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun MaterialScreen(viewModel: MaterialViewModel = viewModel()) {
    val materials by viewModel.materials.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    val userRole = userProfile?.role ?: "ALUMNO"
    val userName = userProfile?.nombre ?: ""
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Material de Apoyo",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "Recursos compartidos por tus profesores.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(materials) { item ->
                    MaterialItemView(
                        item = item,
                        canDelete = userRole == "ADMIN" || userRole == "PROFE",
                        onDelete = { viewModel.deleteMaterial(item.id) }
                    )
                }
            }
        }

        // Solo visible para Profesores o Admins
        if (userRole == "PROFE" || userRole == "ADMIN") {
            FloatingActionButton(
                onClick = { showDialog = true },
                modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
            }
        }
    }

    if (showDialog) {
        AddMaterialDialog(
            service = FirebaseService(),
            userName = userName,
            userId = userId,
            onDismiss = { showDialog = false },
            onSuccess = { 
                showDialog = false 
            }
        )
    }
}

@Composable
fun MaterialItemView(item: MaterialApoyo, canDelete: Boolean, onDelete: () -> Unit) {
    val context = LocalContext.current
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
            Surface(
                color = if (item.tipo == "ARCHIVO") MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.size(56.dp)
            ) {
                var isError by remember { mutableStateOf(false) }
                var isLoading by remember { mutableStateOf(true) }

                Box(contentAlignment = Alignment.Center) {
                    AsyncImage(
                        model = item.urlMaterial,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        onSuccess = { 
                            isLoading = false
                            isError = false 
                        },
                        onError = { 
                            isLoading = false
                            isError = true 
                        },
                        onLoading = {
                            isLoading = true
                        }
                    )
                    
                    if (isError || (isLoading && item.urlMaterial.isBlank())) {
                        Icon(
                            imageVector = if (item.tipo == "ARCHIVO") Icons.Default.AttachFile else Icons.Default.Link,
                            contentDescription = null,
                            modifier = Modifier.padding(12.dp),
                            tint = if (item.tipo == "ARCHIVO") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(item.titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                if (item.tipo == "ARCHIVO") {
                    Text(
                        text = "Archivo: ${item.nombreArchivo ?: "Documento"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(item.descripcion, style = MaterialTheme.typography.bodySmall, maxLines = 2, color = Color.Gray)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.autor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        " • ${SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(item.fecha))}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }
            
            Row {
                IconButton(onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.urlMaterial))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "No se pudo abrir el recurso", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Icon(Icons.Default.OpenInNew, null, tint = MaterialTheme.colorScheme.primary)
                }
                if (canDelete) {
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
fun AddMaterialDialog(
    service: FirebaseService,
    userName: String,
    userId: String,
    onDismiss: () -> Unit, 
    onSuccess: () -> Unit
) {
    var titulo by remember { mutableStateOf("") }
    var desc by remember { mutableStateOf("") }
    var url by remember { mutableStateOf("") }
    var tipo by remember { mutableStateOf("LINK") } // "LINK" o "ARCHIVO"
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var selectedFileName by remember { mutableStateOf("") }
    var isUploading by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            selectedFileUri = it
            // Obtener nombre del archivo
            context.contentResolver.query(it, null, null, null, null)?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (cursor.moveToFirst()) {
                    selectedFileName = cursor.getString(nameIndex)
                }
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Subir Material") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = titulo, onValueChange = { titulo = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = desc, onValueChange = { desc = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
                
                Text("Tipo de Recurso:", style = MaterialTheme.typography.labelMedium)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(selected = tipo == "LINK", onClick = { tipo = "LINK" })
                    Text("Enlace Web", modifier = Modifier.clickable { tipo = "LINK" })
                    Spacer(Modifier.width(16.dp))
                    RadioButton(selected = tipo == "ARCHIVO", onClick = { tipo = "ARCHIVO" })
                    Text("Archivo", modifier = Modifier.clickable { tipo = "ARCHIVO" })
                }

                if (tipo == "LINK") {
                    OutlinedTextField(
                        value = url, 
                        onValueChange = { url = it }, 
                        label = { Text("URL (Drive, YouTube, Web)") },
                        modifier = Modifier.fillMaxWidth(),
                        leadingIcon = { Icon(Icons.Default.Link, null) }
                    )
                } else {
                    Button(
                        onClick = { filePicker.launch("*/*") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                    ) {
                        Icon(Icons.Default.AttachFile, null)
                        Spacer(Modifier.width(8.dp))
                        Text(if (selectedFileName.isEmpty()) "Seleccionar Archivo" else "Cambiar Archivo")
                    }
                    if (selectedFileName.isNotEmpty()) {
                        Text("Archivo seleccionado: $selectedFileName", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                }

                if (isUploading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
                    Text("Procesando...", style = MaterialTheme.typography.labelSmall)
                }
            }
        },
        confirmButton = {
            Button(
                enabled = !isUploading && titulo.isNotBlank() && (url.isNotBlank() || selectedFileUri != null),
                onClick = {
                    isUploading = true
                    scope.launch {
                        var finalUrl = url
                        var fileName = ""
                        
                        // 1. Si es archivo, primero subirlo
                        if (tipo == "ARCHIVO" && selectedFileUri != null) {
                            val downloadUrl = service.uploadFile(selectedFileUri!!, selectedFileName)
                            if (downloadUrl != null) {
                                finalUrl = downloadUrl
                                fileName = selectedFileName
                            } else {
                                Toast.makeText(context, "Error al subir archivo a Storage", Toast.LENGTH_SHORT).show()
                                isUploading = false
                                return@launch
                            }
                        }

                        // 2. Guardar metadatos en Firestore
                        val success = service.addMaterial(MaterialApoyo(
                            titulo = titulo,
                            descripcion = desc,
                            urlMaterial = finalUrl,
                            tipo = tipo,
                            nombreArchivo = if (tipo == "ARCHIVO") fileName else null,
                            autor = userName,
                            autorID = userId
                        ))

                        if (success) {
                            Toast.makeText(context, "Material publicado con éxito", Toast.LENGTH_SHORT).show()
                            onSuccess()
                        } else {
                            Toast.makeText(context, "Error al guardar en la base de datos", Toast.LENGTH_SHORT).show()
                        }
                        isUploading = false
                    }
                }
            ) {
                Text("Publicar")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss, enabled = !isUploading) { Text("Cancelar") } }
    )
}
