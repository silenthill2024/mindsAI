package com.example.mindsai.navigation

import androidx.compose.ui.res.painterResource
import com.example.mindsai.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            CalendarScreen()
        }

        composable(Screen.Brain.route) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color(0xFFF8F9FF)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_logo),
                        contentDescription = null,
                        modifier = Modifier.size(120.dp),
                        tint = Color.Unspecified
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "Módulo Cerebral IA", 
                        fontSize = 24.sp, 
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF673AB7)
                    )
                    Text(
                        "Próximamente", 
                        fontSize = 16.sp, 
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Text(
                        "Estamos entrenando tu nuevo tutor...",
                        fontSize = 12.sp,
                        color = Color.LightGray
                    )
                }
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
