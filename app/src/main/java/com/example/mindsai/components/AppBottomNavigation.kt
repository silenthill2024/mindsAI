package com.example.mindsai.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun AppBottomNavigation(navController: NavHostController) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, "Inicio") },
            label = { Text("Inicio") },
            selected = false,
            onClick = { navController.navigate("home") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Forum, "Foro") },
            label = { Text("Foro") },
            selected = false,
            onClick = { navController.navigate("forum") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, "Perfil") },
            label = { Text("Perfil") },
            selected = false,
            onClick = { navController.navigate("profile") }
        )
    }
}