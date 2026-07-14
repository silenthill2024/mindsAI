package com.example.mindsai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mindsai.navigation.AppNavigation
import com.example.mindsai.components.AppBottomNavigation
import com.example.mindsai.navigation.Screen
import com.example.mindsai.ui.theme.MindsAITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isDarkMode by remember { mutableStateOf(false) }

            MindsAITheme(darkTheme = isDarkMode) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                Scaffold(
                    bottomBar = {
                        val noBarScreens = listOf(Screen.Login.route, Screen.Register.route, Screen.Splash.route)
                        if (currentRoute !in noBarScreens) {
                            AppBottomNavigation(navController)
                        }
                    },
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) { padding ->
                    AppNavigation(navController, Modifier.padding(padding), isDarkMode) { isDarkMode = it }
                }
            }
        }
    }
}
