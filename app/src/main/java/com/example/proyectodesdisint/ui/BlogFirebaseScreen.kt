package com.example.proyectodesdisint.ui

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.proyectodesdisint.data.FirebaseService
import com.example.proyectodesdisint.model.BlogPost
import com.example.proyectodesdisint.model.BlogReply
import com.example.proyectodesdisint.model.UserProfile
import com.example.proyectodesdisint.streaming.WearSyncManager
import com.example.proyectodesdisint.ui.components.LetterAvatar
import com.example.proyectodesdisint.ui.components.ProfileImageDisplay
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BlogFirebaseScreen(navController: NavController) {
    val service = remember { FirebaseService() }
    val context = LocalContext.current
    val wearSyncManager = remember { WearSyncManager(context) }
    var blogs by remember { mutableStateOf<List<BlogPost>?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("Explorador") }
    var userPhotoUrl by remember { mutableStateOf("") }
    var currentUserProfile by remember { mutableStateOf<UserProfile?>(null) }
    val user = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(Unit) {
        service.listenBlogs { blogs = it }
        user?.let {
            val profile = service.getUserProfile(it.uid)
            if (profile != null) {
                // Mapping User to UserProfile for ease of use in role checking
                currentUserProfile = UserProfile(uid = profile.uid, nombre = profile.nombre, role = profile.role, photoUrl = profile.photoUrl)
                userName = profile.nombre
                userPhotoUrl = profile.photoUrl
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Comunidad MindsAI",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                "Comparte tus dudas y pensamientos con el mundo.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            when {
                blogs == null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                blogs!!.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay publicaciones aún. ¡Sé el primero!", color = Color.Gray)
                    }
                }
                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(blogs!!) { blog ->
                            BlogFirebaseItemView(
                                blog = blog, 
                                service = service, 
                                currentUserName = userName, 
                                currentUserPhotoUrl = userPhotoUrl,
                                currentUserRole = if (currentUserProfile?.isAdmin == true) "ADMIN" else (currentUserProfile?.role ?: "ALUMNO")
                            )
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Nueva Publicación") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("¿Qué quieres compartir?") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        service.addBlogPost(BlogPost(
                            titulo = title,
                            contenido = content,
                            autor = userName,
                            autorID = user?.uid ?: "",
                            autorPhotoUrl = userPhotoUrl
                        ))
                        title = ""; content = ""; showDialog = false
                    }
                }) { Text("Publicar") }
            },
            dismissButton = { TextButton(onClick = { showDialog = false }) { Text("Cancelar") } }
        )
    }
}

@Composable
fun BlogFirebaseItemView(
    blog: BlogPost, 
    service: FirebaseService, 
    currentUserName: String, 
    currentUserPhotoUrl: String,
    currentUserRole: String = "ALUMNO"
) {
    var expanded by remember { mutableStateOf(false) }
    var replies by remember { mutableStateOf(listOf<BlogReply>()) }
    var replyText by remember { mutableStateOf("") }
    
    // Edición de Post
    var showEditPostDialog by remember { mutableStateOf(false) }
    var editTitle by remember { mutableStateOf(blog.titulo) }
    var editContent by remember { mutableStateOf(blog.contenido) }
    
    // Edición de Comentario
    var showEditReplyDialog by remember { mutableStateOf(false) }
    var selectedReplyId by remember { mutableStateOf("") }
    var editReplyText by remember { mutableStateOf("") }

    val user = FirebaseAuth.getInstance().currentUser
    val isAuthor = user?.uid == blog.autorID
    val isAdmin = currentUserRole == "ADMIN"

    // Load replies immediately to show count
    LaunchedEffect(blog.id) {
        service.listenBlogReplies(blog.id) { replies = it }
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                ProfileImageDisplay(
                    photoUrl = blog.autorPhotoUrl,
                    userName = blog.autor,
                    size = 40.dp
                )
                
                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(blog.titulo, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            blog.autor,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            " • " + SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(Date(blog.fecha)),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                }

                if (isAuthor || isAdmin) {
                    Row {
                        IconButton(onClick = { showEditPostDialog = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar post", modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = { service.deleteBlogPost(blog.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar post", modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Text(blog.contenido, style = MaterialTheme.typography.bodyMedium)
            
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.ChatBubbleOutline, null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                Spacer(Modifier.width(4.dp))
                Text("${replies.size} Comentarios", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }

            if (expanded) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)
                
                replies.forEach { reply ->
                    val isReplyAuthor = user?.uid == reply.autorID
                    
                    Row(
                        modifier = Modifier.padding(bottom = 12.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        ProfileImageDisplay(
                            photoUrl = reply.autorPhotoUrl,
                            userName = reply.autor,
                            size = 32.dp
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(reply.autor, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            Text(reply.texto, style = MaterialTheme.typography.bodySmall)
                        }

                        if (isReplyAuthor || isAdmin) {
                            Row {
                                IconButton(onClick = { 
                                    selectedReplyId = reply.id
                                    editReplyText = reply.texto
                                    showEditReplyDialog = true 
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar respuesta", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
                                }
                                IconButton(onClick = { service.deleteBlogReply(blog.id, reply.id) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar respuesta", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = replyText,
                        onValueChange = { replyText = it },
                        placeholder = { Text("Escribe un comentario...") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 2
                    )
                    IconButton(onClick = {
                        if (replyText.isNotBlank()) {
                            service.addBlogReply(blog.id, BlogReply(
                                texto = replyText,
                                autor = currentUserName,
                                autorID = user?.uid ?: "",
                                autorPhotoUrl = currentUserPhotoUrl
                            ))
                            replyText = ""
                        }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.Send, null, tint = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }

    // Diálogos de Edición
    if (showEditPostDialog) {
        AlertDialog(
            onDismissRequest = { showEditPostDialog = false },
            title = { Text("Editar Publicación") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = editTitle, onValueChange = { editTitle = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = editContent, onValueChange = { editContent = it }, label = { Text("Contenido") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                }
            },
            confirmButton = {
                Button(onClick = {
                    service.updateBlogPost(blog.id, editTitle, editContent)
                    showEditPostDialog = false
                }) { Text("Guardar") }
            },
            dismissButton = { TextButton(onClick = { showEditPostDialog = false }) { Text("Cancelar") } }
        )
    }

    if (showEditReplyDialog) {
        AlertDialog(
            onDismissRequest = { showEditReplyDialog = false },
            title = { Text("Editar Comentario") },
            text = {
                OutlinedTextField(value = editReplyText, onValueChange = { editReplyText = it }, label = { Text("Comentario") }, modifier = Modifier.fillMaxWidth())
            },
            confirmButton = {
                Button(onClick = {
                    service.updateBlogReply(blog.id, selectedReplyId, editReplyText)
                    showEditReplyDialog = false
                }) { Text("Guardar") }
            },
            dismissButton = { TextButton(onClick = { showEditReplyDialog = false }) { Text("Cancelar") } }
        )
    }
}
