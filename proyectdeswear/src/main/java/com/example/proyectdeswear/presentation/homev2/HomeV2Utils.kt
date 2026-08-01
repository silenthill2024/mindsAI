package com.example.proyectdeswear.presentation.homev2

import com.example.proyectdeswear.presentation.Task
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class HomeV2DateState {
    OVERDUE,
    TODAY,
    UPCOMING,
    UNKNOWN
}

fun parseHomeV2Date(value: String): Date? {
    val normalized = value.trim().take(10)

    if (normalized.isBlank()) {
        return null
    }

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

            val parsed = formatter.parse(normalized)

            if (parsed != null) {
                return Calendar.getInstance().apply {
                    time = parsed
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time
            }
        } catch (_: Exception) {
            // Intenta con el siguiente formato.
        }
    }

    return null
}

fun homeV2DateState(value: String): HomeV2DateState {
    val taskDate = parseHomeV2Date(value)
        ?: return HomeV2DateState.UNKNOWN

    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time

    return when {
        taskDate.before(today) -> HomeV2DateState.OVERDUE
        sameHomeV2Day(taskDate, today) -> HomeV2DateState.TODAY
        else -> HomeV2DateState.UPCOMING
    }
}

private fun sameHomeV2Day(
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

fun shortHomeV2Date(value: String): String {
    val parsed = parseHomeV2Date(value)

    if (parsed == null) {
        return value.take(10).ifBlank {
            "Sin fecha"
        }
    }

    return SimpleDateFormat(
        "dd MMM",
        Locale.getDefault()
    ).format(parsed)
}

fun orderedHomeV2Tasks(tasks: List<Task>): List<Task> {
    val pending = tasks.filter { !it.completado }

    return pending.sortedWith(
        compareBy<Task> {
            when (homeV2DateState(it.fecha)) {
                HomeV2DateState.OVERDUE -> 0
                HomeV2DateState.TODAY -> 1
                HomeV2DateState.UPCOMING -> 2
                HomeV2DateState.UNKNOWN -> 3
            }
        }.thenBy {
            parseHomeV2Date(it.fecha)?.time
                ?: Long.MAX_VALUE
        }
    )
}

fun homeV2Recommendation(task: Task): String {
    return when (homeV2DateState(task.fecha)) {
        HomeV2DateState.OVERDUE ->
            "Requiere atención inmediata"

        HomeV2DateState.TODAY ->
            "Completarla hoy reducirá tus pendientes"

        HomeV2DateState.UPCOMING ->
            "Es la siguiente actividad de tu agenda"

        HomeV2DateState.UNKNOWN ->
            "Asigna una fecha para organizarla"
    }
}
