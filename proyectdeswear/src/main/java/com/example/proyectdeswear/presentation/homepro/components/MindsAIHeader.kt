package com.example.proyectdeswear.presentation.homepro.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import com.example.proyectdeswear.data.WearSessionStore
import com.example.proyectdeswear.data.WearApiClient
import com.example.proyectdeswear.data.WearUserSession
import com.example.proyectdeswear.presentation.components.WearProfilePhoto
import com.example.proyectdeswear.presentation.homepro.MindsAIColors
import kotlinx.coroutines.delay

@Composable
fun MindsAIHeader(
    notificationCount: Int,
    onNotificationsClick: () -> Unit
) {

    val context =
        LocalContext.current

    val sessionStore =
        remember {
            WearSessionStore(
                context.applicationContext
            )
        }

    LaunchedEffect(Unit) {

        WearApiClient.syncProfile(
            context.applicationContext
        )
    }

    var session by remember {
        mutableStateOf(
            sessionStore.getSession()
        )
    }

    /*
     * El servicio Wear recibe PROFILE_SYNC
     * en segundo plano.
     *
     * Refrescamos el header para que la foto
     * cambie sin tener que cerrar la app.
     */
    LaunchedEffect(Unit) {

        while (true) {

            val latest =
                sessionStore.getSession()

            if (latest != session) {
                session = latest
            }

            delay(1000)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 24.dp,
                vertical = 4.dp
            ),
        horizontalArrangement =
            Arrangement.SpaceBetween,
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        UserIdentity(
            session = session
        )

        NotificationBell(
            count = notificationCount,
            onClick = onNotificationsClick
        )
    }
}

@Composable
private fun UserIdentity(
    session: WearUserSession
) {

    val displayName =
        session.nombre
            .trim()
            .ifBlank {
                "MindsAI"
            }

    val firstName =
        displayName
            .substringBefore(" ")

    Row(
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        WearProfilePhoto(
            photoUrl = session.photoUrl,
            userName = displayName,
            size = 34.dp
        )

        Spacer(
            modifier =
                Modifier.width(7.dp)
        )

        Column {

            Text(
                text = firstName,
                color =
                    MindsAIColors.TextPrimary,
                fontSize = 13.sp,
                fontWeight =
                    FontWeight.Bold
            )

            if (
                session.uid.isNotBlank()
            ) {

                Text(
                    text =
                        session.role
                            .ifBlank {
                                "ALUMNO"
                            },
                    color =
                        MindsAIColors.Purple,
                    fontSize = 8.sp
                )
            }
        }
    }
}