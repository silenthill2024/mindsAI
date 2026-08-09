package com.example.proyectdeswear.presentation


import androidx.compose.ui.platform.LocalContext
import com.example.proyectdeswear.presentation.notificationsv3.NotificationCenterV3

import com.example.proyectdeswear.presentation.homepro.MindsAIProfessionalHome

import com.example.proyectdeswear.presentation.homev2.MindsAIHomeV2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import com.example.proyectdeswear.presentation.theme.ProyectoDesDisIntTheme
import kotlinx.coroutines.delay
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private val Background = Color(0xFF08080D)
private val Surface = Color(0xFF191820)
private val SurfaceLight = Color(0xFF25232D)

private val Purple = Color(0xFF9C5CFF)
private val Blue = Color(0xFF45B8FF)
private val Turquoise = Color(0xFF2ED9C3)

private val Danger = Color(0xFFFF526D)
private val Warning = Color(0xFFFFA940)
private val Success = Color(0xFF36D399)
private val SecondaryText = Color(0xFFB9B5C4)

private enum class WatchScreen {
    DASHBOARD,
    NOTIFICATIONS
}

private enum class TaskStatus {
    OVERDUE,
    TODAY,
    UPCOMING,
    NO_DATE
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ProyectoDesDisIntTheme {
                MindsAIWearApp()
            }
        }
    }
}

@Composable
private fun MindsAIWearApp() {

    var screen by remember {
        mutableStateOf(WatchScreen.DASHBOARD)
    }

    var tasks by remember {
        mutableStateOf(emptyList<Task>())
    }

    var expandedTaskId by remember {
        mutableStateOf<String?>(null)
    }

    var feedbackMessage by remember {
        mutableStateOf<String?>(null)
    }

    val context = LocalContext.current

    val service = remember(context) {
        FirebaseServiceWear(
            context.applicationContext
        )
    }

    val currentUser = remember { FirebaseAuth.getInstance().currentUser }

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            service.listenTasks { updatedTasks ->
                tasks = updatedTasks

                if (
                    expandedTaskId != null &&
                    updatedTasks.none {
                        it.documentId == expandedTaskId
                    }
                ) {
                    expandedTaskId = null
                }
            }
        }
    }

    LaunchedEffect(feedbackMessage) {
        if (feedbackMessage != null) {
            delay(1800)
            feedbackMessage = null
        }
    }

    if (currentUser == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                "Por favor, inicia sesión en tu teléfono para sincronizar.",
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(20.dp)
            )
        }
        return
    }

    when (screen) {
        WatchScreen.DASHBOARD -> {
            DashboardScreen(
                tasks = tasks,
                expandedTaskId = expandedTaskId,
                feedbackMessage = feedbackMessage,
                onNotificationsClick = {
                    screen = WatchScreen.NOTIFICATIONS
                },
                onTaskClick = { task ->
                    expandedTaskId =
                        if (expandedTaskId == task.documentId) {
                            null
                        } else {
                            task.documentId
                        }
                },
                onComplete = { task ->
                    service.markTaskAsCompleted(
                        task = task,
                        onSuccess = {
                            expandedTaskId = null
                            feedbackMessage = "Tarea completada"
                        },
                        onFailure = {
                            feedbackMessage = "No se pudo completar"
                        }
                    )
                },
                onDelete = { task ->
                    service.deleteTask(
                        task = task,
                        onSuccess = {
                            expandedTaskId = null
                            feedbackMessage = "Tarea eliminada"
                        },
                        onFailure = {
                            feedbackMessage = "No se pudo eliminar"
                        }
                    )
                },
                onVoiceClick = {
                    feedbackMessage =
                        "Di: mostrar pendientes"
                }
            )
        }

        WatchScreen.NOTIFICATIONS -> {
            NotificationsScreen(
                tasks = tasks,
                expandedTaskId = expandedTaskId,
                feedbackMessage = feedbackMessage,
                onBack = {
                    expandedTaskId = null
                    screen = WatchScreen.DASHBOARD
                },
                onTaskClick = { task ->
                    expandedTaskId =
                        if (expandedTaskId == task.documentId) {
                            null
                        } else {
                            task.documentId
                        }
                },
                onComplete = { task ->
                    service.markTaskAsCompleted(
                        task = task,
                        onSuccess = {
                            expandedTaskId = null
                            feedbackMessage = "Tarea completada"
                        },
                        onFailure = {
                            feedbackMessage = "No se pudo completar"
                        }
                    )
                },
                onDelete = { task ->
                    service.deleteTask(
                        task = task,
                        onSuccess = {
                            expandedTaskId = null
                            feedbackMessage = "Tarea eliminada"
                        },
                        onFailure = {
                            feedbackMessage = "No se pudo eliminar"
                        }
                    )
                }
            )
        }
    }
}

@Composable
private fun DashboardScreen(
    tasks: List<Task>,
    expandedTaskId: String?,
    feedbackMessage: String?,
    onNotificationsClick: () -> Unit,
    onTaskClick: (Task) -> Unit,
    onComplete: (Task) -> Unit,
    onDelete: (Task) -> Unit,
    onVoiceClick: () -> Unit
) {
    val notificationCount = tasks.count { !it.completado }

    MindsAIProfessionalHome(
        tasks = tasks,
        notificationCount = notificationCount,
        onNotificationsClick = onNotificationsClick,
        onDelete = onDelete,
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun NotificationsScreen(
    tasks: List<Task>,
    expandedTaskId: String?,
    feedbackMessage: String?,
    onBack: () -> Unit,
    onTaskClick: (Task) -> Unit,
    onComplete: (Task) -> Unit,
    onDelete: (Task) -> Unit
) {
    NotificationCenterV3(
        tasks = tasks,
        onBack = onBack,
        onComplete = onComplete,
        onDelete = onDelete,
        modifier = Modifier.fillMaxSize()
    )
}
@Composable
private fun PremiumHeader(
    notificationCount: Int,
    onNotificationsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 18.dp,
                vertical = 7.dp
            ),
        horizontalArrangement =
            Arrangement.SpaceBetween,
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "MindsAI",
                color = Purple,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .background(
                            Success,
                            CircleShape
                        )
                )

                Spacer(
                    modifier = Modifier.size(4.dp)
                )

                Text(
                    text = "Sincronizado",
                    color = SecondaryText,
                    fontSize = 9.sp
                )
            }
        }

        Box(
            modifier = Modifier
                .size(38.dp)
                .background(
                    Surface,
                    CircleShape
                )
                .clickable(
                    onClick = onNotificationsClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Aviso",
                fontSize = 20.sp
            )

            if (notificationCount > 0) {
                Box(
                    modifier = Modifier
                        .size(17.dp)
                        .align(Alignment.TopEnd)
                        .background(
                            Danger,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = notificationCount
                            .coerceAtMost(9)
                            .toString(),
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun DailyProgressRing(
    progress: Float,
    totalPending: Int,
    todayPending: Int
) {
    Box(
        modifier = Modifier.size(155.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val center = Offset(
                size.width / 2f,
                size.height / 2f
            )

            drawProgressRing(
                progress = progress,
                color = Purple,
                radius = size.minDimension * 0.43f,
                strokeWidth = 13f,
                center = center
            )

            drawProgressRing(
                progress = (
                    1f - totalPending.coerceAtMost(10) / 10f
                ).coerceIn(0.08f, 1f),
                color = Blue,
                radius = size.minDimension * 0.33f,
                strokeWidth = 9f,
                center = center
            )

            drawProgressRing(
                progress = (
                    1f - todayPending.coerceAtMost(8) / 8f
                ).coerceIn(0.08f, 1f),
                color = Turquoise,
                radius = size.minDimension * 0.24f,
                strokeWidth = 8f,
                center = center
            )
        }

        Column(
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {
            Text(
                text = "${(progress * 100).toInt()}%",
                color = Color.White,
                fontSize = 27.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                text = "Progreso diario",
                color = SecondaryText,
                fontSize = 9.sp
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope
    .drawProgressRing(
        progress: Float,
        color: Color,
        radius: Float,
        strokeWidth: Float,
        center: Offset
    ) {

    val topLeft = Offset(
        center.x - radius,
        center.y - radius
    )

    val arcSize = Size(
        radius * 2f,
        radius * 2f
    )

    drawArc(
        color = SurfaceLight,
        startAngle = -90f,
        sweepAngle = 360f,
        useCenter = false,
        topLeft = topLeft,
        size = arcSize,
        style = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round
        )
    )

    drawArc(
        color = color,
        startAngle = -90f,
        sweepAngle =
            360f * progress.coerceIn(0f, 1f),
        useCenter = false,
        topLeft = topLeft,
        size = arcSize,
        style = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round
        )
    )
}

@Composable
private fun DailySummary(
    overdue: Int,
    today: Int,
    upcoming: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement =
            Arrangement.SpaceEvenly
    ) {
        SummaryItem(
            value = overdue,
            label = "Vencidas",
            color = Danger
        )

        SummaryItem(
            value = today,
            label = "Hoy",
            color = Warning
        )

        SummaryItem(
            value = upcoming,
            label = "Proximas",
            color = Turquoise
        )
    }
}

@Composable
private fun SummaryItem(
    value: Int,
    label: String,
    color: Color
) {
    Column(
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {
        Text(
            text = value.toString(),
            color = color,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = label,
            color = SecondaryText,
            fontSize = 8.sp
        )
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        modifier = Modifier.padding(
            top = 9.dp,
            bottom = 4.dp
        ),
        color = Color.White,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun NextTaskCard(
    task: Task,
    onClick: () -> Unit
) {
    val status = taskStatus(task)
    val statusColor = taskStatusColor(status)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
            .background(
                Surface,
                RoundedCornerShape(22.dp)
            )
            .clickable(onClick = onClick)
            .padding(13.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceBetween,
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = taskStatusLabel(status),
                    color = statusColor,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = task.titulo.ifBlank {
                        "Tarea sin titulo"
                    },
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2
                )
            }

            Box(
                modifier = Modifier
                    .size(11.dp)
                    .background(
                        statusColor,
                        CircleShape
                    )
            )
        }

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = formatTaskSchedule(task),
            color = SecondaryText,
            fontSize = 10.sp
        )
    }
}

@Composable
private fun CompactTaskChip(
    task: Task,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    val status = taskStatus(task)
    val statusColor = taskStatusColor(status)

    Chip(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 9.dp,
                vertical = 2.dp
            ),
        label = {
            Text(
                text = task.titulo.ifBlank {
                    "Tarea sin titulo"
                },
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        },
        secondaryLabel = {
            Text(
                text = if (isExpanded) {
                    "Ocultar acciones"
                } else {
                    formatTaskSchedule(task)
                },
                color = statusColor,
                fontSize = 9.sp,
                maxLines = 1
            )
        },
        colors = ChipDefaults.chipColors(
            backgroundColor = Surface,
            contentColor = Color.White
        ),
        onClick = onClick
    )
}

@Composable
private fun NotificationTaskCard(
    task: Task,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    val status = taskStatus(task)
    val statusColor = taskStatusColor(status)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 12.dp,
                vertical = 3.dp
            )
            .background(
                Surface,
                RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    statusColor.copy(alpha = 0.18f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = when (status) {
                    TaskStatus.OVERDUE -> "VENCIDA"
                    TaskStatus.TODAY -> "HOY"
                    TaskStatus.UPCOMING -> "PROXIMA"
                    TaskStatus.NO_DATE -> "SIN FECHA"
                },
                color = statusColor,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.size(9.dp)
        )

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = task.titulo.ifBlank {
                    "Tarea sin titulo"
                },
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )

            Text(
                text = if (isExpanded) {
                    "Toca nuevamente para cerrar"
                } else {
                    "Texto"
                },
                color = statusColor,
                fontSize = 8.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun TaskActionsCard(
    task: Task,
    onComplete: () -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 3.dp
            )
            .background(
                SurfaceLight,
                RoundedCornerShape(20.dp)
            )
            .padding(11.dp)
    ) {
        Text(
            text = task.descripcion.ifBlank {
                "Texto"
            },
            color = SecondaryText,
            fontSize = 9.sp,
            textAlign = TextAlign.Start
        )

        ActionButton(
            text = "Completar",
            backgroundColor = Purple,
            textColor = Color.White,
            onClick = onComplete
        )

        ActionButton(
            text = "Eliminar",
            backgroundColor =
                Danger.copy(alpha = 0.17f),
            textColor = Danger,
            onClick = onDelete
        )
    }
}

@Composable
private fun ActionButton(
    text: String,
    backgroundColor: Color,
    textColor: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp)
            .background(
                backgroundColor,
                RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 7.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun VoiceButton(
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(vertical = 12.dp)
            .size(72.dp)
            .background(
                Purple,
                CircleShape
            )
            .clickable(onClick = onClick),
        horizontalAlignment =
            Alignment.CenterHorizontally,
        verticalArrangement =
            Arrangement.Center
    ) {
        Text(
            text = "Aviso",
            fontSize = 23.sp
        )

        Text(
            text = "Hablar",
            color = Color.White,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun EmptyCard(
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp)
            .background(
                Surface,
                RoundedCornerShape(22.dp)
            )
            .padding(14.dp),
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            color = Purple,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = subtitle,
            color = SecondaryText,
            fontSize = 9.sp
        )
    }
}

@Composable
private fun FeedbackCard(message: String) {
    Box(
        modifier = Modifier
            .padding(
                horizontal = 15.dp,
                vertical = 3.dp
            )
            .background(
                Color(0xFF33264A),
                RoundedCornerShape(14.dp)
            )
            .padding(
                horizontal = 12.dp,
                vertical = 6.dp
            )
    ) {
        Text(
            text = message,
            color = Color.White,
            fontSize = 9.sp,
            textAlign = TextAlign.Center
        )
    }
}

private fun taskStatus(task: Task): TaskStatus {
    val date = parseTaskDate(task.fecha)
        ?: return TaskStatus.NO_DATE

    val today = startOfDay(Date())

    return when {
        date.before(today) -> TaskStatus.OVERDUE
        isSameDay(date, today) -> TaskStatus.TODAY
        else -> TaskStatus.UPCOMING
    }
}

private fun parseTaskDate(value: String): Date? {
    val normalized = value.trim().take(10)

    if (normalized.isBlank()) {
        return null
    }

    val formats = listOf(
        "yyyy-MM-dd",
        "dd/MM/yyyy",
        "dd-MM-yyyy"
    )

    formats.forEach { pattern ->
        try {
            val formatter = SimpleDateFormat(
                pattern,
                Locale.getDefault()
            )

            formatter.isLenient = false

            val parsed = formatter.parse(normalized)

            if (parsed != null) {
                return startOfDay(parsed)
            }
        } catch (_: Exception) {
            // Se prueba el siguiente formato.
        }
    }

    return null
}

private fun startOfDay(date: Date): Date {
    return Calendar.getInstance().apply {
        time = date
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time
}

private fun isSameDay(
    first: Date,
    second: Date
): Boolean {
    val firstCalendar =
        Calendar.getInstance().apply {
            time = first
        }

    val secondCalendar =
        Calendar.getInstance().apply {
            time = second
        }

    return firstCalendar.get(Calendar.YEAR) ==
        secondCalendar.get(Calendar.YEAR) &&
        firstCalendar.get(Calendar.DAY_OF_YEAR) ==
        secondCalendar.get(Calendar.DAY_OF_YEAR)
}

private fun taskStatusColor(
    status: TaskStatus
): Color {
    return when (status) {
        TaskStatus.OVERDUE -> Danger
        TaskStatus.TODAY -> Warning
        TaskStatus.UPCOMING -> Turquoise
        TaskStatus.NO_DATE -> SecondaryText
    }
}

private fun taskStatusLabel(
    status: TaskStatus
): String {
    return when (status) {
        TaskStatus.OVERDUE -> "VENCIDA"
        TaskStatus.TODAY -> "HOY"
        TaskStatus.UPCOMING -> "PROXIMA"
        TaskStatus.NO_DATE -> "SIN FECHA"
    }
}

private fun formatTaskSchedule(task: Task): String {
    val date = task.fecha
        .trim()
        .ifBlank { "Sin fecha" }

    val time = task.hora
        .trim()
        .ifBlank { "Sin hora" }

    return "Texto"
}
