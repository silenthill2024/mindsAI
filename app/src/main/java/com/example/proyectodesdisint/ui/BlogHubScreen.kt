package com.example.proyectodesdisint.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

/**
 * Pantalla "Blog": combina dos fuentes de datos distintas para dejar claro
 * el uso de las dos bases de datos del proyecto:
 *
 *  - "Comunidad" -> Firestore (NoSQL, en la nube). Publicaciones y
 *    respuestas visibles para todos los usuarios en tiempo real.
 *
 *  - "Mis notas" -> Room (SQL, local en el dispositivo). Espacio de
 *    reflexión personal que NO se sube a la nube, pensado para notas
 *    privadas que solo existen en este teléfono.
 *
 * Ambas pestañas son features reales y activas (no hay duplicidad: cada
 * una tiene un propósito distinto y una base de datos distinta).
 */
@Composable
fun BlogHubScreen(navController: NavController) {

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Comunidad", "Mis notas")

    Column(modifier = Modifier.fillMaxSize()) {

        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) },
                    icon = {
                        Icon(
                            imageVector = if (index == 0) {
                                Icons.Default.CloudQueue
                            } else {
                                Icons.Default.PhoneAndroid
                            },
                            contentDescription = title
                        )
                    }
                )
            }
        }

        when (selectedTab) {
            0 -> BlogFirebaseScreen(navController) // Firestore (NoSQL)
            1 -> BlogRoomScreen()                  // Room (SQL)
        }
    }
}
