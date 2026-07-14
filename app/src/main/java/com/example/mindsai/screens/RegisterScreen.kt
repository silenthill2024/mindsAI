package com.example.mindsai.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mindsai.viewmodel.RegisterViewModel

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    
    // Estados de error
    var nombreError by remember { mutableStateOf<String?>(null) }
    var correoError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    val gradient = Brush.verticalGradient(
        colors = listOf(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.primary)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                color = Color.White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Storage, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Insert: Room (SQL Relacional)", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Text(text = "Nueva Cuenta", fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
            Text(text = "Únete a la red académica más grande", fontSize = 14.sp, color = Color.White.copy(alpha = 0.8f), modifier = Modifier.padding(bottom = 32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Campo Nombre
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { 
                            nombre = it
                            if (it.isNotEmpty()) nombreError = null 
                        },
                        label = { Text("Nombre Completo") },
                        isError = nombreError != null,
                        supportingText = { if (nombreError != null) Text(nombreError!!) },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Campo Correo
                    OutlinedTextField(
                        value = correo,
                        onValueChange = { 
                            correo = it
                            if (it.contains("@")) correoError = null
                        },
                        label = { Text("Correo Electrónico") },
                        isError = correoError != null,
                        supportingText = { if (correoError != null) Text(correoError!!) },
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Campo Contraseña
                    OutlinedTextField(
                        value = password,
                        onValueChange = { 
                            password = it
                            if (it.length >= 6) passwordError = null
                        },
                        label = { Text("Contraseña (min. 6 caracteres)") },
                        visualTransformation = PasswordVisualTransformation(),
                        isError = passwordError != null,
                        supportingText = { if (passwordError != null) Text(passwordError!!) },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            // Validación lógica
                            var isValid = true
                            if (nombre.isEmpty()) { nombreError = "El nombre es obligatorio"; isValid = false }
                            if (!correo.contains("@") || !correo.contains(".")) { correoError = "Ingresa un correo válido"; isValid = false }
                            if (password.length < 6) { passwordError = "La contraseña debe tener al menos 6 caracteres"; isValid = false }

                            if (isValid) {
                                viewModel.register(nombre, correo, password, onSuccess = onRegisterSuccess)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("Crear Cuenta", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }

                    TextButton(onClick = onNavigateToLogin, modifier = Modifier.padding(top = 8.dp)) {
                        Text("¿Ya tienes cuenta? Inicia sesión aquí")
                    }
                }
            }
        }
    }
}
