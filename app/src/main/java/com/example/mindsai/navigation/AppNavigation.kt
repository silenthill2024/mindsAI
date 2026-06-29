package com.example.mindsai.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mindsai.screens.ForumScreen
import com.example.mindsai.screens.HomeScreen
import com.example.mindsai.screens.ProfileScreen

@Composable
fun AppNavigation(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable("home") { HomeScreen() }
        composable("forum") { ForumScreen() }
        composable("profile") { ProfileScreen() }
    }
}