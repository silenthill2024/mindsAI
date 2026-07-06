package com.example.mindsai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.mindsai.navigation.AppNavigation
import com.example.mindsai.components.AppBottomNavigation
import com.example.mindsai.ui.theme.MindsAITheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Este es el estado global que controlará todo
            var isDarkMode by remember { mutableStateOf(false) }

            MindsAITheme(darkTheme = isDarkMode) {
                val navController = rememberNavController()
                Scaffold(
                    bottomBar = { AppBottomNavigation(navController) },
                    containerColor = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) { padding ->
                    // Le pasamos el estado y la función para cambiarlo a la navegación
                    AppNavigation(navController, Modifier.padding(padding), isDarkMode) { isDarkMode = it }
                }
            }
        }
    }
}
// Minds AI 1st push
