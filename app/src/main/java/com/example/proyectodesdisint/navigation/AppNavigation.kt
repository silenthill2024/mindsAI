package com.example.proyectodesdisint.navigation

import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import com.example.proyectodesdisint.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.TopAppBarDefaults

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val user = FirebaseAuth.getInstance().currentUser

    Scaffold(
        topBar = {
            val navBackStackEntry = navController.currentBackStackEntryAsState().value
            val currentRoute = navBackStackEntry?.destination?.route

            if (currentRoute == "home" || currentRoute == "calendar" || currentRoute == "profile" || currentRoute == "ai") {
                CenterAlignedTopAppBar(
                    title = { AppLogo() },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        },
        bottomBar = {
            val navBackStackEntry = navController.currentBackStackEntryAsState().value
            val currentRoute = navBackStackEntry?.destination?.route

            if (currentRoute == "home" || currentRoute == "calendar" || currentRoute == "profile" || currentRoute == "ai") {
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

            composable("ai") {
                AIScreen(navController)
            }

            composable("profile") {
                ProfileScreen(navController)
            }
        }
    }
}
