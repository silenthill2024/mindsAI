package com.example.proyectdeswear.presentation.notificationsv3

import com.example.proyectdeswear.presentation.Task
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object NotificationEngine {

    fun createNotifications(
        tasks: List<Task>
    ): List<SmartNotification> {
        val pending = tasks.filter {
            !it.completado
        }

        val notifications = mutableListOf<SmartNotification>()

        val overdue = pending.filter {
            getDateState(it.fecha) == DateState.OVERDUE
        }

        val today = pending.filter {
            getDateState(it.fecha) == DateState.TODAY
        }

        val upcoming = pending.filter {
            getDateState(it.fecha) == DateState.UPCOMING
        }

        if (pending.size >= 5) {
            notifications += SmartNotification(
                id = "ai-workload",
                title = "Carga academica alta",
                message = "MindsAI recomienda empezar por la actividad mas urgente.",
                timeLabel = "Ahora",
                type = SmartNotificationType.AI,
                accent = NotificationV3Colors.Purple,
                priority = 100
            )
        }

        overdue.forEachIndexed { index, task ->
            notifications += SmartNotification(
                id = "overdue-$index-${task.titulo}",
                title = "Tarea vencida",
                message = task.titulo.ifBlank {
                    "Tarea sin titulo"
                },
                timeLabel = task.hora.ifBlank {
                    "Pendiente"
                },
                type = SmartNotificationType.OVERDUE,
                accent = NotificationV3Colors.Red,
                priority = 90
            )
        }

        today.forEachIndexed { index, task ->
            notifications += SmartNotification(
                id = "today-$index-${task.titulo}",
                title = "Actividad para hoy",
                message = task.titulo.ifBlank {
                    "Tarea sin titulo"
                },
                timeLabel = task.hora.ifBlank {
                    "Hoy"
                },
                type = SmartNotificationType.TODAY,
                accent = NotificationV3Colors.Orange,
                priority = 70
            )
        }

        upcoming.take(3).forEachIndexed { index, task ->
            notifications += SmartNotification(
                id = "upcoming-$index-${task.titulo}",
                title = "Proxima actividad",
                message = task.titulo.ifBlank {
                    "Tarea sin titulo"
                },
                timeLabel = shortDate(task.fecha),
                type = SmartNotificationType.UPCOMING,
                accent = NotificationV3Colors.Cyan,
                priority = 50
            )
        }

        if (notifications.isEmpty()) {
            notifications += SmartNotification(
                id = "empty",
                title = "Todo bajo control",
                message = "No tienes avisos importantes en este momento.",
                timeLabel = "Ahora",
                type = SmartNotificationType.INFORMATION,
                accent = NotificationV3Colors.Green,
                priority = 10
            )
        }

        return notifications.sortedByDescending {
            it.priority
        }
    }

    private enum class DateState {
        OVERDUE,
        TODAY,
        UPCOMING,
        UNKNOWN
    }

    private fun getDateState(
        value: String
    ): DateState {
        val taskDate = parseDate(value)
            ?: return DateState.UNKNOWN

        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        return when {
            taskDate.before(today) ->
                DateState.OVERDUE

            sameDay(taskDate, today) ->
                DateState.TODAY

            else ->
                DateState.UPCOMING
        }
    }

    private fun parseDate(
        value: String
    ): Date? {
        val normalized = value.trim().take(10)

        val patterns = listOf(
            "yyyy-MM-dd",
            "dd/MM/yyyy",
            "dd-MM-yyyy"
        )

        for (pattern in patterns) {
            try {
                val formatter = SimpleDateFormat(
                    pattern,
                    Locale.getDefault()
                )

                formatter.isLenient = false

                val result = formatter.parse(normalized)

                if (result != null) {
                    return result
                }
            } catch (_: Exception) {
            }
        }

        return null
    }

    private fun sameDay(
        first: Date,
        second: Date
    ): Boolean {
        val firstCalendar = Calendar.getInstance().apply {
            time = first
        }

        val secondCalendar = Calendar.getInstance().apply {
            time = second
        }

        return firstCalendar.get(Calendar.YEAR) ==
            secondCalendar.get(Calendar.YEAR) &&
            firstCalendar.get(Calendar.DAY_OF_YEAR) ==
            secondCalendar.get(Calendar.DAY_OF_YEAR)
    }

    private fun shortDate(
        value: String
    ): String {
        val parsed = parseDate(value)
            ?: return "Proxima"

        return SimpleDateFormat(
            "dd MMM",
            Locale.getDefault()
        ).format(parsed)
    }
}
