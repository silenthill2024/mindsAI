package com.example.proyectdeswear.presentation.notificationsv3

import androidx.compose.ui.graphics.Color
import com.example.proyectdeswear.presentation.Task

enum class SmartNotificationType {
    AI,
    OVERDUE,
    TODAY,
    UPCOMING,
    INFORMATION
}

data class SmartNotification(
    val id: String,
    val title: String,
    val message: String,
    val timeLabel: String,
    val type: SmartNotificationType,
    val accent: Color,
    val priority: Int,
    val task: Task? = null
)
