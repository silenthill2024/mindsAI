package com.example.mindsai.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Login : Screen("login", "Login", Icons.Default.Person)
    object Register : Screen("register", "Register", Icons.Default.Person)
    object Home : Screen("home", "Inicio", Icons.Default.Home)
    object Forum : Screen("forum", "Foro", Icons.Default.Forum)
    object Profile : Screen("profile", "Perfil", Icons.Default.Person)
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Forum,
    Screen.Profile,
)