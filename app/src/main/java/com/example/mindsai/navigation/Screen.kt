package com.example.mindsai.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.ui.graphics.vector.ImageVector

import androidx.compose.material.icons.filled.Psychology

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Splash : Screen("splash", "Splash", Icons.Default.Home)
    object Login : Screen("login", "Login", Icons.Default.Person)
    object Register : Screen("register", "Register", Icons.Default.Person)
    object Home : Screen("home", "Inicio", Icons.Default.Home)
    object Calendar : Screen("calendar", "Calendario", Icons.Default.CalendarMonth)
    object Brain : Screen("brain", "Mente", Icons.Default.Psychology)
    object Forum : Screen("forum", "Foro", Icons.Default.Forum)
    object Profile : Screen("profile", "Perfil", Icons.Default.Person)
    object Tasks : Screen("tasks", "Tareas", Icons.Default.CheckCircle)
}

val bottomNavItems = listOf(
    Screen.Home,
    Screen.Calendar,
    Screen.Brain,
    Screen.Forum,
    Screen.Profile,
)