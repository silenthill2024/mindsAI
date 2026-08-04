package com.example.proyectodesdisint.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectodesdisint.data.FirebaseService
import com.example.proyectodesdisint.model.BlogPost
import com.example.proyectodesdisint.model.BlogReply
import com.example.proyectodesdisint.streaming.WearSyncManager
import org.json.JSONObject
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
    val user = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(Unit) {
        service.listenBlogs { blogs = it }
        user?.let {
            val profile = service.getUserProfile(it.uid)
            profile?.let { p -> userName = p.nombre }
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
                        Text("No hay publicaciones aÃƒÂºn. Ã‚Â¡SÃƒÂ© el primero!", color = Color.Gray)
                    }
                }
                else -> {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(blogs!!) { blog ->
                            BlogFirebaseItemView(blog, service, userName)
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
            title = { Text("Nueva PublicaciÃƒÂ³n") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("TÃƒÂ­tulo") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Ã‚Â¿QuÃƒÂ© quieres compartir?") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        service.addBlogPost(BlogPost(
                            titulo = title,
                            contenido = content,
                            autor = userName,
                            autorID = user?.uid ?: ""
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
fun BlogFirebaseItemView(blog: BlogPost, service: FirebaseService, currentUserName: String) {
    var expanded by remember { mutableStateOf(false) }
    var replies by remember { mutableStateOf(listOf<BlogReply>()) }
    var replyText by remember { mutableStateOf("") }
    val user = FirebaseAuth.getInstance().currentUser

    LaunchedEffect(expanded) {
        if (expanded) {
            service.listenBlogReplies(blog.id) { replies = it }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { expanded = !expanded },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(blog.titulo, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    blog.autor,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    " Ã¢â‚¬Â¢ " + SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(Date(blog.fecha)),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
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
                    Column(modifier = Modifier.padding(bottom = 8.dp)) {
                        Text(reply.autor, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        Text(reply.texto, style = MaterialTheme.typography.bodySmall)
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
                                autorID = user?.uid ?: ""
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
}
