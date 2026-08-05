package com.example.proyectodesdisint.ui.homev4

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.proyectodesdisint.model.Task
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeV4Dashboard(
    tasks: List<Task>,
    modifier: Modifier = Modifier
) {
    val stats = remember(tasks) {
        calculateDashboardStats(tasks)
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        AiCoachSummaryCard(
            stats = stats
        )

        DailyProgressCard(
            stats = stats
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement =
                Arrangement.spacedBy(10.dp)
        ) {
            SmallStatCard(
                title = "Racha",
                value = "${stats.streak} días",
                icon = {
                    Icon(
                        imageVector =
                            Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = Color(0xFFFF8A3D)
                    )
                },
                modifier = Modifier.weight(1f)
            )

            SmallStatCard(
                title = "Nivel",
                value = stats.level.toString(),
                icon = {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFC857)
                    )
                },
                modifier = Modifier.weight(1f)
            )

            SmallStatCard(
                title = "XP",
                value = stats.xp.toString(),
                icon = {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint =
                            MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier.weight(1f)
            )
        }

        NextTaskCard(
            task = stats.importantTask
        )
    }
}

@Composable
private fun AiCoachSummaryCard(
    stats: DashboardStats
) {
    val message = when {
        stats.pending == 0 -> {
            "No tienes tareas pendientes. " +
                "Puedes repasar, descansar o preparar tu próxima semana."
        }

        stats.highPriority > 0 &&
            stats.importantTask != null -> {
            "Detecté ${stats.highPriority} tarea(s) de prioridad alta. " +
                "Te recomiendo comenzar por " +
                "\"${stats.importantTask.titulo}\"."
        }

        stats.pending >= 6 &&
            stats.importantTask != null -> {
            "Tu carga académica es alta. Comienza por " +
                "\"${stats.importantTask.titulo}\" y trabaja " +
                "en bloques de 25 minutos."
        }

        stats.importantTask != null -> {
            "Tu carga está controlada. La siguiente actividad " +
                "recomendada es \"${stats.importantTask.titulo}\"."
        }

        else -> {
            "MindsAI está analizando tus actividades."
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(28.dp),
        color =
            MaterialTheme.colorScheme.primaryContainer,
        tonalElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(19.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector =
                            Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White
                    )
                }

                Spacer(
                    modifier = Modifier.width(12.dp)
                )

                Column {
                    Text(
                        text = "Coach IA",
                        style =
                            MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold,
                        color =
                            MaterialTheme.colorScheme
                                .onPrimaryContainer
                    )

                    Text(
                        text = "Análisis académico en tiempo real",
                        style =
                            MaterialTheme.typography.bodySmall,
                        color =
                            MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                color =
                    MaterialTheme.colorScheme
                        .onPrimaryContainer
            )

            if (stats.importantTask != null) {
                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text =
                        "Tiempo sugerido: ${stats.recommendedMinutes} minutos",
                    style =
                        MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color =
                        MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun DailyProgressCard(
    stats: DashboardStats
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector =
                            Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint =
                            MaterialTheme.colorScheme.primary
                    )

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Text(
                        text = "Progreso académico",
                        style =
                            MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "${stats.progressPercent}%",
                    style =
                        MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color =
                        MaterialTheme.colorScheme.primary
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            LinearProgressIndicator(
                progress = {
                    stats.progress
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp),
                color =
                    MaterialTheme.colorScheme.primary,
                trackColor =
                    MaterialTheme.colorScheme
                        .surfaceVariant
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text =
                    "${stats.completed} completadas · " +
                        "${stats.pending} pendientes · " +
                        "${stats.todayTasks} para hoy",
                style =
                    MaterialTheme.typography.bodyMedium,
                color =
                    MaterialTheme.colorScheme
                        .onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SmallStatCard(
    title: String,
    value: String,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 14.dp
            ),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {
            icon()

            Spacer(
                modifier = Modifier.height(7.dp)
            )

            Text(
                text = value,
                style =
                    MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1
            )

            Text(
                text = title,
                style =
                    MaterialTheme.typography.bodySmall,
                color =
                    MaterialTheme.colorScheme
                        .onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun NextTaskCard(
    task: Task?
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )

                Spacer(
                    modifier = Modifier.width(9.dp)
                )

                Text(
                    text = "Próxima actividad",
                    style =
                        MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            if (task == null) {
                Text(
                    text = "No tienes actividades pendientes.",
                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant
                )
            } else {
                Text(
                    text = task.titulo.ifBlank {
                        "Tarea sin título"
                    },
                    style =
                        MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(
                    modifier = Modifier.height(7.dp)
                )

                Text(
                    text = buildString {
                        append(
                            task.fecha.ifBlank {
                                "Sin fecha"
                            }
                        )

                        if (task.hora.isNotBlank()) {
                            append(" · ")
                            append(task.hora)
                        }
                    },
                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                PriorityBadge(
                    priority = task.prioridad
                )
            }
        }
    }
}

@Composable
private fun PriorityBadge(
    priority: String
) {
    val normalized =
        priority.trim().uppercase()

    val color = when (normalized) {
        "ALTA" -> Color(0xFFFF526D)
        "MEDIA" -> Color(0xFFFFA940)
        "BAJA" -> Color(0xFF36D399)
        else -> MaterialTheme.colorScheme.primary
    }

    Box(
        modifier = Modifier
            .background(
                color.copy(alpha = 0.15f),
                RoundedCornerShape(50)
            )
            .padding(
                horizontal = 12.dp,
                vertical = 6.dp
            )
    ) {
        Text(
            text = "Prioridad ${priority.ifBlank { "Media" }}",
            color = color,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun calculateDashboardStats(
    tasks: List<Task>
): DashboardStats {
    val completedTasks =
        tasks.filter { it.completado }

    val pendingTasks =
        tasks.filterNot { it.completado }

    val importantTask =
        pendingTasks.sortedWith(
            compareBy<Task> {
                when (
                    it.prioridad.trim().uppercase()
                ) {
                    "ALTA" -> 0
                    "MEDIA" -> 1
                    "BAJA" -> 2
                    else -> 3
                }
            }
                .thenBy { it.fecha }
                .thenBy { it.hora }
        ).firstOrNull()

    val todayFormats = listOf(
        SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        ).format(Date()),
        SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        ).format(Date()),
        SimpleDateFormat(
            "dd-MM-yyyy",
            Locale.getDefault()
        ).format(Date())
    )

    val todayTasks =
        pendingTasks.count { task ->
            task.fecha.trim() in todayFormats
        }

    val total = tasks.size

    val progress =
        if (total == 0) {
            0f
        } else {
            completedTasks.size.toFloat() /
                total.toFloat()
        }

    val xp = completedTasks.size * 25
    val level = (xp / 100) + 1

    /*
     * La racha es provisional mientras se agrega
     * persistencia diaria en Firebase.
     */
    val streak =
        completedTasks.size.coerceAtMost(30)

    val recommendedMinutes =
        when (
            importantTask
                ?.prioridad
                ?.trim()
                ?.uppercase()
        ) {
            "ALTA" -> 45
            "MEDIA" -> 30
            "BAJA" -> 20
            else -> 25
        }

    return DashboardStats(
        total = total,
        completed = completedTasks.size,
        pending = pendingTasks.size,
        highPriority =
            pendingTasks.count {
                it.prioridad.trim()
                    .equals(
                        "ALTA",
                        ignoreCase = true
                    )
            },
        todayTasks = todayTasks,
        progress = progress,
        progressPercent =
            (progress * 100).toInt(),
        xp = xp,
        level = level,
        streak = streak,
        recommendedMinutes =
            recommendedMinutes,
        importantTask = importantTask
    )
}

private data class DashboardStats(
    val total: Int,
    val completed: Int,
    val pending: Int,
    val highPriority: Int,
    val todayTasks: Int,
    val progress: Float,
    val progressPercent: Int,
    val xp: Int,
    val level: Int,
    val streak: Int,
    val recommendedMinutes: Int,
    val importantTask: Task?
)