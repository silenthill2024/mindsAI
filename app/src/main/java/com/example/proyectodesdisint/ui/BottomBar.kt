package com.example.proyectodesdisint.ui

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource

@Composable
fun BottomBar(navController: NavController) {

    NavigationBar {

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("home") },
            icon = {Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Inicio"
            )},
            label = { Text("Home") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("calendar") },
            icon = {Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = "Inicio"
            )},
            label = { Text("Calendar") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate("profile") },
            icon = {Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Inicio"
            )},
            label = { Text("Profile") }
        )
    }
}