package com.example.proyectodesdisint.ui

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.ChatBubbleOutline

import androidx.compose.material.icons.automirrored.filled.MenuBook

@Composable
fun BottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary
    ) {

        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = { 
                if (currentRoute != "home") {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Inicio"
                )
            },
            label = { Text("Home", style = MaterialTheme.typography.labelSmall, maxLines = 1) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primary
            )
        )

        NavigationBarItem(
            selected = currentRoute == "material",
            onClick = { 
                if (currentRoute != "material") {
                    navController.navigate("material") 
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = "Material"
                )
            },
            label = { Text("Material", style = MaterialTheme.typography.labelSmall, maxLines = 1) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primary
            )
        )

        NavigationBarItem(
            selected = currentRoute == "calendar",
            onClick = { 
                if (currentRoute != "calendar") {
                    navController.navigate("calendar") 
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Calendario"
                )
            },
            label = { Text("Calen.", style = MaterialTheme.typography.labelSmall, maxLines = 1) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primary
            )
        )

        NavigationBarItem(
            selected = currentRoute == "ai",
            onClick = { 
                if (currentRoute != "ai") {
                    navController.navigate("ai") 
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = "IA"
                )
            },
            label = { Text("MindsAI", style = MaterialTheme.typography.labelSmall, maxLines = 1) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primary
            )
        )

        NavigationBarItem(
            selected = currentRoute == "blog",
            onClick = { 
                if (currentRoute != "blog") {
                    navController.navigate("blog") 
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.ChatBubbleOutline,
                    contentDescription = "Blog"
                )
            },
            label = { Text("Community", style = MaterialTheme.typography.labelSmall, maxLines = 1) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primary
            )
        )

        NavigationBarItem(
            selected = currentRoute == "profile",
            onClick = { 
                if (currentRoute != "profile") {
                    navController.navigate("profile") 
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Perfil"
                )
            },
            label = { Text("Profile", style = MaterialTheme.typography.labelSmall, maxLines = 1) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.onPrimary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}
