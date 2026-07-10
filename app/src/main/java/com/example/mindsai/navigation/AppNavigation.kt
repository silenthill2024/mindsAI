package com.example.mindsai.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mindsai.local.data.DatabaseProvider
import com.example.mindsai.repository.UserRepository
import com.example.mindsai.screens.*
import com.example.mindsai.viewmodel.LoginViewModel
import com.example.mindsai.viewmodel.RegisterViewModel
import com.example.mindsai.viewmodel.ViewModelFactory

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier,
    isDarkMode: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val database = DatabaseProvider.getDatabase(context)
    val repository = UserRepository(database.userDao())
    val factory = ViewModelFactory(repository)

    NavHost(navController = navController, startDestination = Screen.Login.route, modifier = modifier) {
        composable(Screen.Login.route) {
            val viewModel: LoginViewModel = viewModel(factory = factory)
            LoginScreen(
                viewModel = viewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }
        composable(Screen.Register.route) {
            val viewModel: RegisterViewModel = viewModel(factory = factory)
            RegisterScreen(
                viewModel = viewModel,
                onRegisterSuccess = {
                    navController.popBackStack()
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.Home.route) { HomeScreen() }
        composable(Screen.Forum.route) { ForumScreen() }
        // Pasamos las variables a ProfileScreen
        composable(Screen.Profile.route) { ProfileScreen(isDarkMode, onThemeChange) }
    }
}
