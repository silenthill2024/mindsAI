package com.example.proyectdeswear.presentation.notificationsv3

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import com.example.proyectdeswear.presentation.Task
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val Background =
    Color(0xFF080A12)

private val Surface =
    Color(0xFF141722)

private val TextPrimary =
    Color.White

private val TextSecondary =
    Color(0xFF969BAB)

private val Purple =
    Color(0xFF8B7CFF)

private val Warning =
    Color(0xFFFFB74D)

private val MaterialBlue =
    Color(0xFF67B7FF)

private val CommentPurple =
    Color(0xFFB69CFF)


enum class RelevantNotificationType {
    COMMENT,
    TASK_DUE,
    MATERIAL
}


data class RelevantNotification(
    val id: String,
    val type: RelevantNotificationType,
    val title: String,
    val message: String,
    val timeLabel: String = "",
    val task: Task? = null
)


@Composable
fun NotificationCenterV3(
    tasks: List<Task>,
    onBack: () -> Unit,
    onComplete: (Task) -> Unit,
    onDelete: (Task) -> Unit,
    modifier: Modifier = Modifier
) {

    /*
     * Actualmente las notificaciones reales
     * se generan con las tareas.
     *
     * COMMENT y MATERIAL quedan preparados
     * para conectarlos despuÃ©s con Firebase/API.
     */

    val notifications =
        buildTaskNotifications(tasks)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 16.dp
                )
        ) {

            Spacer(
                modifier =
                    Modifier.height(9.dp)
            )

            NotificationsHeader(
                count =
                    notifications.size,
                onBack =
                    onBack
            )

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            if (notifications.isEmpty()) {

                EmptyNotifications()

            } else {

                LazyColumn(
                    modifier =
                        Modifier.fillMaxSize(),
                    verticalArrangement =
                        Arrangement.spacedBy(
                            7.dp
                        )
                ) {

                    items(
                        items = notifications,
                        key = {
                            it.id
                        }
                    ) { notification ->

                        RelevantNotificationCard(
                            notification =
                                notification
                        )
                    }

                    item {

                        Spacer(
                            modifier =
                                Modifier.height(20.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun NotificationsHeader(
    count: Int,
    onBack: () -> Unit
) {

    Row(
        modifier =
            Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.SpaceBetween,
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(34.dp)
                .background(
                    Surface,
                    CircleShape
                )
                .clickable {
                    onBack()
                },
            contentAlignment =
                Alignment.Center
        ) {

            Text(
                text = "â€¹",
                color =
                    TextPrimary,
                fontSize = 25.sp,
                fontWeight =
                    FontWeight.Bold
            )
        }

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Text(
                text =
                    "Notificaciones",
                color =
                    TextPrimary,
                fontSize = 14.sp,
                fontWeight =
                    FontWeight.Bold
            )

            Text(
                text =
                    when (count) {
                        0 -> "Todo al dÃ­a"
                        1 -> "1 importante"
                        else -> "$count importantes"
                    },
                color =
                    TextSecondary,
                fontSize = 8.sp
            )
        }

        Box(
            modifier = Modifier
                .size(34.dp)
                .background(
                    Purple.copy(
                        alpha = 0.15f
                    ),
                    CircleShape
                ),
            contentAlignment =
                Alignment.Center
        ) {

            Text(
                text =
                    count
                        .coerceAtMost(9)
                        .toString(),
                color =
                    Purple,
                fontSize = 11.sp,
                fontWeight =
                    FontWeight.Bold
            )
        }
    }
}


@Composable
private fun RelevantNotificationCard(
    notification:
        RelevantNotification
) {

    val accent =
        when (notification.type) {

            RelevantNotificationType.COMMENT ->
                CommentPurple

            RelevantNotificationType.TASK_DUE ->
                Warning

            RelevantNotificationType.MATERIAL ->
                MaterialBlue
        }

    val icon =
        when (notification.type) {

            RelevantNotificationType.COMMENT ->
                "C"

            RelevantNotificationType.TASK_DUE ->
                "!"

            RelevantNotificationType.MATERIAL ->
                "M"
        }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Surface,
                RoundedCornerShape(
                    16.dp
                )
            )
            .padding(
                horizontal = 11.dp,
                vertical = 10.dp
            ),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Box(
            modifier = Modifier
                .size(32.dp)
                .background(
                    accent.copy(
                        alpha = 0.18f
                    ),
                    CircleShape
                ),
            contentAlignment =
                Alignment.Center
        ) {

            Text(
                text = icon,
                color = accent,
                fontSize = 12.sp,
                fontWeight =
                    FontWeight.ExtraBold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 9.dp
                )
        ) {

            Text(
                text =
                    notification.title,
                color =
                    TextPrimary,
                fontSize = 11.sp,
                fontWeight =
                    FontWeight.Bold,
                maxLines = 1
            )

            Spacer(
                modifier =
                    Modifier.height(2.dp)
            )

            Text(
                text =
                    notification.message,
                color =
                    TextSecondary,
                fontSize = 8.sp,
                maxLines = 2
            )

            if (
                notification
                    .timeLabel
                    .isNotBlank()
            ) {

                Spacer(
                    modifier =
                        Modifier.height(3.dp)
                )

                Text(
                    text =
                        notification.timeLabel,
                    color =
                        accent,
                    fontSize = 8.sp,
                    fontWeight =
                        FontWeight.Bold
                )
            }
        }
    }
}


@Composable
private fun EmptyNotifications() {

    Column(
        modifier =
            Modifier.fillMaxSize(),
        horizontalAlignment =
            Alignment.CenterHorizontally,
        verticalArrangement =
            Arrangement.Center
    ) {

        Box(
            modifier = Modifier
                .size(48.dp)
                .background(
                    Purple.copy(
                        alpha = 0.12f
                    ),
                    CircleShape
                ),
            contentAlignment =
                Alignment.Center
        ) {

            Text(
                text = "âœ“",
                color =
                    Purple,
                fontSize = 21.sp,
                fontWeight =
                    FontWeight.Bold
            )
        }

        Spacer(
            modifier =
                Modifier.height(9.dp)
        )

        Text(
            text =
                "Todo tranquilo",
            color =
                TextPrimary,
            fontSize = 13.sp,
            fontWeight =
                FontWeight.Bold
        )

        Text(
            text =
                "Nada importante por ahora",
            color =
                TextSecondary,
            fontSize = 9.sp,
            textAlign =
                TextAlign.Center
        )
    }
}


private fun buildTaskNotifications(
    tasks: List<Task>
): List<RelevantNotification> {

    val today =
        Calendar.getInstance()

    val tomorrow =
        Calendar.getInstance().apply {
            add(
                Calendar.DAY_OF_YEAR,
                1
            )
        }

    return tasks
        .filter {
            !it.completado
        }
        .mapNotNull { task ->

            val taskDate =
                parseTaskDate(
                    task.fecha
                )
                    ?: return@mapNotNull null

            val taskCalendar =
                Calendar.getInstance().apply {
                    time = taskDate
                }

            val isToday =
                sameDay(
                    taskCalendar,
                    today
                )

            val isTomorrow =
                sameDay(
                    taskCalendar,
                    tomorrow
                )

            val isOverdue =
                taskCalendar.before(today) &&
                    !isToday

            when {

                isOverdue ->

                    RelevantNotification(
                        id =
                            "overdue_${task.documentId}",
                        type =
                            RelevantNotificationType.TASK_DUE,
                        title =
                            "Tarea vencida",
                        message =
                            task.titulo,
                        timeLabel =
                            "Requiere atenciÃ³n",
                        task =
                            task
                    )

                isToday ->

                    RelevantNotification(
                        id =
                            "today_${task.documentId}",
                        type =
                            RelevantNotificationType.TASK_DUE,
                        title =
                            "Vence hoy",
                        message =
                            task.titulo,
                        timeLabel =
                            task.hora.ifBlank {
                                "Hoy"
                            },
                        task =
                            task
                    )

                isTomorrow ->

                    RelevantNotification(
                        id =
                            "tomorrow_${task.documentId}",
                        type =
                            RelevantNotificationType.TASK_DUE,
                        title =
                            "Vence maÃ±ana",
                        message =
                            task.titulo,
                        timeLabel =
                            task.hora.ifBlank {
                                "MaÃ±ana"
                            },
                        task =
                            task
                    )

                else ->
                    null
            }
        }
}


private fun parseTaskDate(
    value: String
): Date? {

    if (value.isBlank()) {
        return null
    }

    val formats =
        listOf(
            "yyyy-MM-dd",
            "dd/MM/yyyy",
            "dd-MM-yyyy"
        )

    for (format in formats) {

        try {

            val parser =
                SimpleDateFormat(
                    format,
                    Locale.getDefault()
                ).apply {
                    isLenient = false
                }

            val parsed =
                parser.parse(value)

            if (parsed != null) {
                return parsed
            }

        } catch (_: Exception) {
            // Intentar siguiente formato
        }
    }

    return null
}


private fun sameDay(
    first: Calendar,
    second: Calendar
): Boolean {

    return first.get(
        Calendar.YEAR
    ) ==
        second.get(
            Calendar.YEAR
        ) &&
        first.get(
            Calendar.DAY_OF_YEAR
        ) ==
        second.get(
            Calendar.DAY_OF_YEAR
        )
}