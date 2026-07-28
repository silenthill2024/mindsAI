package com.example.proyectodesdisint.navigation

import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.example.proyectodesdisint.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding

@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    val user = FirebaseAuth.getInstance().currentUser

    Scaffold(
        bottomBar = {
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

            if (currentRoute == "home" || currentRoute == "calendar" || currentRoute == "profile") {
                BottomBar(navController)
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = if (user != null) "home" else "login",
            modifier = Modifier.padding(innerPadding)
        ) {

            composable("login") {
                LoginScreen(navController)
            }

            composable("register") {
                RegisterScreen(navController)
            }

            composable("home") {
                HomeScreen(navController)
            }

            composable("calendar") {
                CalendarScreen(navController)
            }

            composable("profile") {
                ProfileScreen(navController)
            }
        }
    }
}