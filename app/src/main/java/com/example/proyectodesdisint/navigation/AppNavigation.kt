package com.example.proyectodesdisint.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.proyectodesdisint.ui.AIScreen
import com.example.proyectodesdisint.ui.AppLogo
import com.example.proyectodesdisint.ui.BlogFirebaseScreen
import com.example.proyectodesdisint.ui.BottomBar
import com.example.proyectodesdisint.ui.CalendarScreen
import com.example.proyectodesdisint.ui.HomeWithVideosScreen
import com.example.proyectodesdisint.ui.LoginScreen
import com.example.proyectodesdisint.ui.ProfileScreen
import com.example.proyectodesdisint.ui.RegisterScreen
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val user = FirebaseAuth.getInstance().currentUser

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val mainRoutes = setOf(
        "home",
        "calendar",
        "ai",
        "blog",
        "profile"
    )

    Scaffold(
        topBar = {
            if (currentRoute in mainRoutes) {
                CenterAlignedTopAppBar(
                    title = {
                        AppLogo()
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        titleContentColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        },
        bottomBar = {
            if (currentRoute in mainRoutes) {
                BottomBar(navController)
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = if (user != null) {
                "home"
            } else {
                "login"
            },
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") {
                LoginScreen(navController)
            }

            composable("register") {
                RegisterScreen(navController)
            }

            composable("home") {
                HomeWithVideosScreen(navController)
            }

            composable("calendar") {
                CalendarScreen(navController)
            }

            composable("ai") {
                AIScreen(navController)
            }

            composable("blog") {
                BlogFirebaseScreen(navController)
            }

            composable("profile") {
                ProfileScreen(navController)
            }
        }
    }
}
