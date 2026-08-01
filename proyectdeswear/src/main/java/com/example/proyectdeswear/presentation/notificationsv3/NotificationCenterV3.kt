package com.example.proyectdeswear.presentation.notificationsv3

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import com.example.proyectdeswear.presentation.Task
import com.example.proyectdeswear.presentation.notificationsv3.components.FuturisticNotificationCard

@Composable
fun NotificationCenterV3(
    tasks: List<Task>,
    onBack: () -> Unit,
    onComplete: (Task) -> Unit,
    onDelete: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    val notifications = remember(tasks) {
        NotificationEngine.createNotifications(tasks)
    }

    var expandedId by remember {
        mutableStateOf<String?>(null)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NotificationV3Colors.Background)
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            drawCircle(
                color = NotificationV3Colors.Purple.copy(alpha = 0.08f),
                radius = size.minDimension * 0.43f,
                center = Offset(
                    size.width * 0.15f,
                    size.height * 0.18f
                )
            )

            drawCircle(
                color = NotificationV3Colors.Cyan.copy(alpha = 0.05f),
                radius = size.minDimension * 0.40f,
                center = Offset(
                    size.width * 0.90f,
                    size.height * 0.82f
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    top = 18.dp,
                    bottom = 30.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NotificationHeader(
                count = notifications.size,
                onBack = onBack
            )

            Spacer(modifier = Modifier.height(12.dp))

            IntelligentSummary(
                notifications = notifications
            )

            Spacer(modifier = Modifier.height(12.dp))

            notifications.forEach { notification ->
                FuturisticNotificationCard(
                    notification = notification,
                    expanded = expandedId == notification.id,
                    onClick = {
                        expandedId =
                            if (expandedId == notification.id) {
                                null
                            } else {
                                notification.id
                            }
                    },
                    onComplete = {
                        notification.task?.let { task ->
                            onComplete(task)
                            expandedId = null
                        }
                    },
                    onDelete = {
                        notification.task?.let { task ->
                            onDelete(task)
                            expandedId = null
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun NotificationHeader(
    count: Int,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    NotificationV3Colors.SurfaceLight,
                    CircleShape
                )
                .clickable(
                    interactionSource = remember {
                        MutableInteractionSource()
                    },
                    indication = null,
                    onClick = onBack
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "<",
                color = NotificationV3Colors.TextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Notificaciones",
                color = NotificationV3Colors.TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "$count activas",
                color = NotificationV3Colors.Purple,
                fontSize = 8.sp
            )
        }

        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    NotificationV3Colors.Purple.copy(alpha = 0.16f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = count.coerceAtMost(9).toString(),
                color = NotificationV3Colors.Lavender,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun IntelligentSummary(
    notifications: List<SmartNotification>
) {
    val important = notifications.count {
        it.priority >= 70
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp)
            .background(
                NotificationV3Colors.Surface.copy(alpha = 0.96f),
                RoundedCornerShape(24.dp)
            )
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "RESUMEN INTELIGENTE",
            color = NotificationV3Colors.Cyan,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(7.dp))

        Text(
            text = notifications.size.toString(),
            color = NotificationV3Colors.TextPrimary,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "eventos activos",
            color = NotificationV3Colors.TextSecondary,
            fontSize = 9.sp
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = "$important requieren atencion",
            color = if (important > 0) {
                NotificationV3Colors.Orange
            } else {
                NotificationV3Colors.Green
            },
            fontSize = 8.sp,
            textAlign = TextAlign.Center
        )
    }
}
