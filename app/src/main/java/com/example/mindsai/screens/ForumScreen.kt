package com.example.mindsai.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mindsai.local.UserSession
import com.example.mindsai.model.ForumPost
import com.example.mindsai.viewmodel.ForumViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForumScreen(viewModel: ForumViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("Todas") }
    
    val categories = listOf(
        "Todas" to Icons.Default.AllInclusive,
        "Matemáticas" to Icons.Default.Functions,
        "Programación" to Icons.Default.Code,
        "Física" to Icons.Default.Science,
        "Bases de Datos" to Icons.Default.Storage
    )
    
    val userName = UserSession.currentUser?.nombre ?: "Invitado"
    val posts by viewModel.posts.collectAsState()

    Scaffold(
        containerColor = Color(0xFFF8F9FF),
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFF5D5FEF),
                contentColor = Color.White,
                shape = RoundedCornerShape(20.dp),
                icon = { Icon(Icons.Default.Edit, contentDescription = null) },
                text = { Text("Nueva pregunta") }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // 1. Header estilo "Maria González"
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Foro", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, tint = Color(0xFF5D5FEF))
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        BadgedBox(badge = { Badge { Text("3") } }) {
                            Icon(Icons.Default.NotificationsNone, contentDescription = null)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Surface(modifier = Modifier.size(35.dp), shape = CircleShape, color = Color.LightGray) {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(4.dp))
                        }
                    }
                }
                Text("Pregunta, aprende y comparte conocimientos", fontSize = 14.sp, color = Color.Gray)
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Buscar preguntas o temas...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        modifier = Modifier.size(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
                    ) {
                        IconButton(onClick = {}) {
                            Icon(Icons.Default.Tune, contentDescription = "Filtro")
                        }
                    }
                }
            }

            // 2. Categorías con Iconos
            LazyRow(
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(categories) { (name, icon) ->
                    val isSelected = selectedCategory == name
                    Card(
                        onClick = { selectedCategory = name },
                        modifier = Modifier.width(100.dp).height(110.dp),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFF5D5FEF) else Color.White
                        ),
                        elevation = CardDefaults.cardElevation(if (isSelected) 4.dp else 1.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(icon, contentDescription = null, tint = if (isSelected) Color.White else Color(0xFF5D5FEF))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(name, fontSize = 11.sp, color = if (isSelected) Color.White else Color.Black, fontWeight = FontWeight.Bold)
                            Text("28", fontSize = 10.sp, color = if (isSelected) Color.White.copy(alpha = 0.7f) else Color.Gray)
                        }
                    }
                }
            }

            // 3. Preguntas Recientes
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Preguntas recientes", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Más recientes", color = Color(0xFF5D5FEF), fontSize = 12.sp)
            }

            LazyColumn(
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 100.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                val filteredPosts = posts.filter { 
                    (selectedCategory == "Todas" || it.category == selectedCategory) &&
                    (it.title.contains(searchQuery, true) || it.body.contains(searchQuery, true))
                }
                
                items(filteredPosts) { post ->
                    ForoPostItemPremium(post = post, onVote = { delta -> viewModel.vote(post.id, delta) })
                }
                
                // Sugerencia IA
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0FF))
                    ) {
                        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(modifier = Modifier.size(45.dp), shape = CircleShape, color = Color.White) {
                                Icon(Icons.Default.SmartToy, contentDescription = null, tint = Color(0xFF5D5FEF), modifier = Modifier.padding(10.dp))
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("¿No encuentras respuesta?", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("Pregunta a StudyMind AI y obtén ayuda instantánea", fontSize = 12.sp, color = Color.Gray)
                            }
                            Button(
                                onClick = { },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color(0xFF5D5FEF)),
                                elevation = ButtonDefaults.buttonElevation(2.dp)
                            ) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("IA", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        CreatePostDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { t, b ->
                viewModel.createPost(t, b, "General", userName)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun ForoPostItemPremium(post: ForumPost, onVote: (Int) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = Color.LightGray) {
                    Icon(Icons.Default.Person, contentDescription = null)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(post.author, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Estudiante • Hace 1 hora", fontSize = 11.sp, color = Color.Gray)
                }
                IconButton(onClick = { }) {
                    Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.Gray)
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Surface(
                color = Color(0xFFF0F0FF),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    post.category,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5D5FEF)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Text(post.title, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(4.dp))
            Text(post.body, fontSize = 14.sp, color = Color.Gray, maxLines = 3)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("5 respuestas", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("23 vistas", fontSize = 12.sp, color = Color.Gray)
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { onVote(1) }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.ThumbUp, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Gray)
                    }
                    Text("${post.votes}", modifier = Modifier.padding(horizontal = 4.dp), fontWeight = FontWeight.Bold)
                    IconButton(onClick = { onVote(-1) }, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.ThumbDown, contentDescription = null, modifier = Modifier.size(18.dp), tint = Color.Gray)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.BookmarkBorder, contentDescription = null, tint = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun CreatePostDialog(onDismiss: () -> Unit, onConfirm: (String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nueva Publicación") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Título") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = body, onValueChange = { body = it }, label = { Text("Mensaje") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
            }
        },
        confirmButton = {
            Button(onClick = { if(title.isNotBlank()) onConfirm(title, body) }) {
                Text("Publicar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
