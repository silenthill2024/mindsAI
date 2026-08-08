package com.example.proyectdeswear.presentation.notificationsv3.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import com.example.proyectdeswear.presentation.notificationsv3.NotificationV3Colors
import com.example.proyectdeswear.presentation.notificationsv3.SmartNotification
import com.example.proyectdeswear.presentation.notificationsv3.SmartNotificationType

@Composable
fun FuturisticNotificationCard(
    notification: SmartNotification,
    expanded: Boolean,
    onClick: () -> Unit,
    onComplete: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var confirmDelete by remember(notification.id) {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .background(
                NotificationV3Colors.Surface,
                RoundedCornerShape(22.dp)
            )
            .padding(
                horizontal = 12.dp,
                vertical = 10.dp
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember {
                        MutableInteractionSource()
                    },
                    indication = null,
                    onClick = onClick
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NotificationTypeIcon(
                type = notification.type,
                accent = notification.accent
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        color = NotificationV3Colors.TextPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )

                    Text(
                        text = notification.timeLabel,
                        color = notification.accent,
                        fontSize = 7.sp,
                        maxLines = 1
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = notification.message,
                    color = NotificationV3Colors.TextSecondary,
                    fontSize = 8.sp,
                    textAlign = TextAlign.Start,
                    maxLines = if (expanded) 4 else 2
                )
            }

            Box(
                modifier = Modifier
                    .padding(start = 6.dp)
                    .size(6.dp)
                    .background(
                        notification.accent,
                        CircleShape
                    )
            )
        }

        if (notification.task != null) {
            Spacer(modifier = Modifier.height(9.dp))

            if (!confirmDelete) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    NotificationActionButton(
                        text = "Completar",
                        background = NotificationV3Colors.Purple,
                        textColor = Color.White,
                        modifier = Modifier.weight(1f),
                        onClick = onComplete
                    )

                    NotificationActionButton(
                        text = "Eliminar",
                        background = NotificationV3Colors.Red.copy(
                            alpha = 0.18f
                        ),
                        textColor = NotificationV3Colors.Red,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            confirmDelete = true
                        }
                    )
                }
            } else {
                Text(
                    text = "¿Eliminar esta tarea?",
                    color = NotificationV3Colors.TextPrimary,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    NotificationActionButton(
                        text = "Cancelar",
                        background = NotificationV3Colors.SurfaceLight,
                        textColor = NotificationV3Colors.TextSecondary,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            confirmDelete = false
                        }
                    )

                    NotificationActionButton(
                        text = "Sí, eliminar",
                        background = NotificationV3Colors.Red,
                        textColor = Color.White,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            confirmDelete = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationActionButton(
    text: String,
    background: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .background(
                background,
                RoundedCornerShape(14.dp)
            )
            .clickable(
                interactionSource = remember {
                    MutableInteractionSource()
                },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun NotificationTypeIcon(
    type: SmartNotificationType,
    accent: Color
) {
    Box(
        modifier = Modifier
            .size(38.dp)
            .background(
                accent.copy(alpha = 0.14f),
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.size(20.dp)
        ) {
            when (type) {
                SmartNotificationType.AI -> {
                    val points = listOf(
                        Offset(
                            size.width * 0.20f,
                            size.height * 0.25f
                        ),
                        Offset(
                            size.width * 0.50f,
                            size.height * 0.15f
                        ),
                        Offset(
                            size.width * 0.80f,
                            size.height * 0.30f
                        ),
                        Offset(
                            size.width * 0.28f,
                            size.height * 0.70f
                        ),
                        Offset(
                            size.width * 0.67f,
                            size.height * 0.78f
                        )
                    )

                    val links = listOf(
                        0 to 1,
                        1 to 2,
                        0 to 3,
                        1 to 3,
                        1 to 4,
                        2 to 4,
                        3 to 4
                    )

                    links.forEach {
                        drawLine(
                            color = accent,
                            start = points[it.first],
                            end = points[it.second],
                            strokeWidth = 1.5f
                        )
                    }

                    points.forEach {
                        drawCircle(
                            color = accent,
                            radius = 2.5f,
                            center = it
                        )
                    }
                }

                SmartNotificationType.OVERDUE -> {
                    drawLine(
                        color = accent,
                        start = Offset(
                            size.width * 0.50f,
                            size.height * 0.20f
                        ),
                        end = Offset(
                            size.width * 0.50f,
                            size.height * 0.62f
                        ),
                        strokeWidth = 3.dp.toPx()
                    )

                    drawCircle(
                        color = accent,
                        radius = 2.5.dp.toPx(),
                        center = Offset(
                            size.width * 0.50f,
                            size.height * 0.80f
                        )
                    )
                }

                else -> {
                    drawCircle(
                        color = accent,
                        radius = size.minDimension * 0.30f,
                        center = center,
                        style =
                            androidx.compose.ui.graphics.drawscope.Stroke(
                                width = 2.dp.toPx()
                            )
                    )

                    drawCircle(
                        color = accent,
                        radius = size.minDimension * 0.09f,
                        center = center
                    )
                }
            }
        }
    }
}