package com.example.mindsai.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.mindsai.local.data.DatabaseProvider
import com.example.mindsai.repository.ForumRepository
import com.example.mindsai.repository.StudyRepository
import com.example.mindsai.repository.UserRepository
import com.example.mindsai.screens.*
import com.example.mindsai.viewmodel.*

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier,
    isDarkMode: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val database = DatabaseProvider.getDatabase(context)
    
    val userRepository = UserRepository(database.userDao())
    val forumRepository = ForumRepository()
    val studyRepository = StudyRepository(database.studyDao())
    
    val factory = ViewModelFactory(userRepository, forumRepository, studyRepository)

    NavHost(navController = navController, startDestination = Screen.Splash.route, modifier = modifier) {
        
        composable(Screen.Splash.route) {
            SplashScreen(onNavigateToLogin = {
                navController.navigate(Screen.Login.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }

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
        composable(Screen.Home.route) { 
            val homeViewModel: HomeViewModel = viewModel(factory = factory)
            HomeScreen(viewModel = homeViewModel, onNavigateToTasks = { navController.navigate(Screen.Tasks.route) }) 
        }

        composable(Screen.Tasks.route) {
            val homeViewModel: HomeViewModel = viewModel(factory = factory)
            TasksScreen(viewModel = homeViewModel)
        }
        
        composable(Screen.Calendar.route) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Pantalla de Calendario (Próximamente)")
            }
        }

        composable(Screen.IA.route) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Pantalla de IA (Tutor Inteligente)")
            }
        }

        composable(Screen.Forum.route) { 
            val forumViewModel: ForumViewModel = viewModel(factory = factory)
            ForumScreen(viewModel = forumViewModel) 
        }

        composable(Screen.Profile.route) { 
            val profileViewModel: ProfileViewModel = viewModel(factory = factory)
            ProfileScreen(
                viewModel = profileViewModel,
                isDarkMode = isDarkMode, 
                onThemeChange = onThemeChange,
                onLogout = {
                    com.example.mindsai.local.UserSession.currentUser = null
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            ) 
        }
    }
}
