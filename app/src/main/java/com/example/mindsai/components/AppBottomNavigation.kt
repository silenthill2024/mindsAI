package com.example.mindsai.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController

@Composable
fun AppBottomNavigation(navController: NavHostController) {
    NavigationBar(containerColor = androidx.compose.ui.graphics.Color.White) {
        NavigationBarItem(icon = { Icon(Icons.Default.Home, "Inicio") }, selected = false, onClick = { navController.navigate("home") }, label = { Text("Inicio") })
        NavigationBarItem(icon = { Icon(Icons.Default.Forum, "Foro") }, selected = false, onClick = { navController.navigate("forum") }, label = { Text("Foro") })
        NavigationBarItem(icon = { Icon(Icons.Default.Person, "Perfil") }, selected = false, onClick = { navController.navigate("profile") }, label = { Text("Perfil") })
    }
}