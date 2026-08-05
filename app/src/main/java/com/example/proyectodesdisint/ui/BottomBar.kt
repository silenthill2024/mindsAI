package com.example.proyectodesdisint.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

private data class BottomNavigationItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun BottomBar(
    navController: NavController
) {
    val items = listOf(
        BottomNavigationItem(
            route = "home",
            label = "Inicio",
            icon = Icons.Default.Home
        ),
        BottomNavigationItem(
            route = "calendar",
            label = "Agenda",
            icon = Icons.Default.CalendarMonth
        ),
        BottomNavigationItem(
            route = "ai",
            label = "IA",
            icon = Icons.Default.SmartToy
        ),
        BottomNavigationItem(
            route = "materials",
            label = "Apoyo",
            icon = Icons.Default.MenuBook
        ),
        BottomNavigationItem(
            route = "blog",
            label = "Blog",
            icon = Icons.Default.Forum
        ),
        BottomNavigationItem(
            route = "profile",
            label = "Perfil",
            icon = Icons.Default.Person
        )
    )

    val navBackStackEntry by
        navController.currentBackStackEntryAsState()

    val currentRoute =
        navBackStackEntry?.destination?.route

    NavigationBar(
        containerColor =
            MaterialTheme.colorScheme.surface
    ) {
        items.forEach { item ->
            val selected =
                currentRoute == item.route

            NavigationBarItem(
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(
                            item.route
                        ) {
                            popUpTo("home") {
                                saveState = true
                            }

                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        softWrap = false
                    )
                },
                colors =
                    NavigationBarItemDefaults.colors(
                        selectedIconColor =
                            MaterialTheme.colorScheme.primary,
                        selectedTextColor =
                            MaterialTheme.colorScheme.primary,
                        indicatorColor =
                            MaterialTheme.colorScheme.primaryContainer,
                        unselectedIconColor =
                            MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor =
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
            )
        }
    }
}
