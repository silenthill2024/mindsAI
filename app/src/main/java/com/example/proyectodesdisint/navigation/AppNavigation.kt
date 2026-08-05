package com.example.proyectodesdisint.navigation

import android.app.Application
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.proyectodesdisint.ui.AIScreen
import com.example.proyectodesdisint.ui.AdminPanelScreen
import com.example.proyectodesdisint.ui.AppLogo
import com.example.proyectodesdisint.ui.BlogFirebaseScreen
import com.example.proyectodesdisint.ui.BottomBar
import com.example.proyectodesdisint.ui.CalendarScreen
import com.example.proyectodesdisint.ui.homev3.HomeV3Screen
import com.example.proyectodesdisint.ui.LoginScreen
import com.example.proyectodesdisint.ui.ProfileScreen
import com.example.proyectodesdisint.ui.RegisterScreen
import com.example.proyectodesdisint.ui.TasksScreen
import com.example.proyectodesdisint.ui.components.ProfileImageDisplay
import com.example.proyectodesdisint.viewmodel.ProfileViewModel
import com.google.firebase.auth.FirebaseAuth
import com.example.proyectodesdisint.ui.MaterialSupportScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {

    val navController = rememberNavController()
    val user = FirebaseAuth.getInstance().currentUser
    val context = androidx.compose.ui.platform.LocalContext.current
    val profileViewModel: ProfileViewModel = viewModel(
        factory = com.example.proyectodesdisint.viewmodel.ProfileViewModelFactory(context.applicationContext as android.app.Application)
    )
    val profile by profileViewModel.profile.collectAsState()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Sincronizar el perfil cuando cambie el usuario o la pantalla
    androidx.compose.runtime.LaunchedEffect(user, currentRoute) {
        profileViewModel.loadProfile()
    }

    val mainRoutes = setOf(
        "home",
        "calendar",
        "ai",
        "materials",
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
                    navigationIcon = {
                        if (currentRoute != "profile") {
                            ProfileImageDisplay(
                                photoUrl = profile.photoUrl,
                                userName = profile.nombre.ifBlank { "U" },
                                size = 36.dp,
                                modifier = Modifier
                                    .padding(start = 12.dp)
                                    .clickable { navController.navigate("profile") }
                            )
                        }
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
                HomeV3Screen(navController)
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
            composable("materials") {
                MaterialSupportScreen(navController)
            }


            composable("profile") {
                ProfileScreen(navController)
            }

            composable("tasks") {
                TasksScreen(navController)
            }

            composable("admin_panel") {
                AdminPanelScreen(navController)
            }
        }
    }
}
