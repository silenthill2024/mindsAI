package com.example.mindsai.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mindsai.screens.*

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier,
    isDarkMode: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    NavHost(navController = navController, startDestination = "home", modifier = modifier) {
        composable("home") { HomeScreen() }
        composable("forum") { ForumScreen() }
        // Pasamos las variables a ProfileScreen
        composable("profile") { ProfileScreen(isDarkMode, onThemeChange) }
    }
}