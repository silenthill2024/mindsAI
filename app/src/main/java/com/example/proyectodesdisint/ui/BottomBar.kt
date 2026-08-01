package com.example.proyectodesdisint.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomBar(navController: NavController) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {

        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = {
                if (currentRoute != "home") {
                    navController.navigate("home") {
                        launchSingleTop = true
                    }
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Inicio"
                )
            },
            label = {
                Text("Inicio")
            },
            colors = bottomItemColors()
        )

        NavigationBarItem(
            selected = currentRoute == "calendar",
            onClick = {
                if (currentRoute != "calendar") {
                    navController.navigate("calendar") {
                        launchSingleTop = true
                    }
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Agenda"
                )
            },
            label = {
                Text("Agenda")
            },
            colors = bottomItemColors()
        )

        NavigationBarItem(
            selected = currentRoute == "ai",
            onClick = {
                if (currentRoute != "ai") {
                    navController.navigate("ai") {
                        launchSingleTop = true
                    }
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Psychology,
                    contentDescription = "MindsAI"
                )
            },
            label = {
                Text("MindsAI")
            },
            colors = bottomItemColors()
        )

        NavigationBarItem(
            selected = currentRoute == "blog",
            onClick = {
                if (currentRoute != "blog") {
                    navController.navigate("blog") {
                        launchSingleTop = true
                    }
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Forum,
                    contentDescription = "Comunidad"
                )
            },
            label = {
                Text("Comunidad")
            },
            colors = bottomItemColors()
        )

        NavigationBarItem(
            selected = currentRoute == "profile",
            onClick = {
                if (currentRoute != "profile") {
                    navController.navigate("profile") {
                        launchSingleTop = true
                    }
                }
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Perfil"
                )
            },
            label = {
                Text("Perfil")
            },
            colors = bottomItemColors()
        )
    }
}

@Composable
private fun bottomItemColors() =
    NavigationBarItemDefaults.colors(
        selectedIconColor = MaterialTheme.colorScheme.onPrimary,
        selectedTextColor = MaterialTheme.colorScheme.primary,
        indicatorColor = MaterialTheme.colorScheme.primary
    )
