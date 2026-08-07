package com.example.proyectodesdisint.ui.coach

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.proyectodesdisint.model.Task
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AiCoachBubble(
    tasks: List<Task>,
    modifier: Modifier = Modifier
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    var question by remember {
        mutableStateOf("")
    }

    var answer by remember(tasks) {
        mutableStateOf(
            buildInitialMessage(tasks)
        )
    }

    val infiniteTransition =
        rememberInfiniteTransition(
            label = "coach_pulse"
        )

    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "coach_scale"
    )

    Box(
        modifier = modifier,
        contentAlignment = Alignment.BottomEnd
    ) {
        AnimatedVisibility(
            visible = expanded,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(
                    end = 4.dp,
                    bottom = 78.dp
                )
        ) {
            Surface(
                modifier = Modifier
                    .width(330.dp)
                    .imePadding(),
                shape = RoundedCornerShape(26.dp),
                color =
                    MaterialTheme.colorScheme.surface,
                tonalElevation = 10.dp,
                shadowElevation = 12.dp
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
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
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        MaterialTheme
                                            .colorScheme
                                            .primaryContainer,
                                        CircleShape
                                    ),
                                contentAlignment =
                                    Alignment.Center
                            ) {
                                Icon(
                                    imageVector =
                                        Icons.Default.Psychology,
                                    contentDescription = null,
                                    tint =
                                        MaterialTheme
                                            .colorScheme
                                            .primary
                                )
                            }

                            Spacer(
                                modifier = Modifier.width(10.dp)
                            )

                            Column {
                                Text(
                                    text = "Coach MindsAI (DEMO)",
                                    fontWeight =
                                        FontWeight.Bold,
                                    color =
                                        MaterialTheme
                                            .colorScheme
                                            .onSurface
                                )

                                Text(
                                    text = "Asistente académico (PRÓXIMAMENTE)",
                                    style =
                                        MaterialTheme
                                            .typography
                                            .bodySmall,
                                    color =
                                        MaterialTheme
                                            .colorScheme
                                            .primary
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                expanded = false
                            }
                        ) {
                            Icon(
                                imageVector =
                                    Icons.Default.Close,
                                contentDescription =
                                    "Cerrar Coach IA"
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme
                                    .colorScheme
                                    .primaryContainer,
                                RoundedCornerShape(18.dp)
                            )
                            .padding(13.dp)
                    ) {
                        Text(
                            text = answer,
                            style =
                                MaterialTheme
                                    .typography
                                    .bodyMedium,
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onPrimaryContainer
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    QuickQuestions(
                        onQuestionSelected = {
                            question = it
                            answer =
                                answerQuestion(
                                    question = it,
                                    tasks = tasks
                                )
                        }
                    )

                    Spacer(
                        modifier = Modifier.height(10.dp)
                    )

                    OutlinedTextField(
                        value = question,
                        onValueChange = {
                            question = it
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                "Consultas básicas (DEMO)"
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(18.dp),
                        keyboardOptions =
                            KeyboardOptions(
                                imeAction = ImeAction.Send
                            ),
                        keyboardActions =
                            KeyboardActions(
                                onSend = {
                                    if (
                                        question.isNotBlank()
                                    ) {
                                        answer =
                                            answerQuestion(
                                                question,
                                                tasks
                                            )
                                    }
                                }
                            ),
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    if (
                                        question.isNotBlank()
                                    ) {
                                        answer =
                                            answerQuestion(
                                                question,
                                                tasks
                                            )
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector =
                                        Icons.Default.Send,
                                    contentDescription =
                                        "Enviar pregunta"
                                )
                            }
                        }
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .scale(pulse)
                .size(66.dp)
                .background(
                    MaterialTheme.colorScheme.primary,
                    CircleShape
                )
                .clickable(
                    interactionSource = remember {
                        MutableInteractionSource()
                    },
                    indication = null,
                    onClick = {
                        expanded = !expanded
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector =
                    Icons.Default.Psychology,
                contentDescription = "Abrir Coach IA",
                tint = Color.White,
                modifier = Modifier.size(34.dp)
            )
        }
    }
}

@Composable
private fun QuickQuestions(
    onQuestionSelected: (String) -> Unit
) {
    val questions = listOf(
        "¿Tengo tareas hoy?",
        "¿Cuál es la más importante?",
        "¿Qué debo estudiar?"
    )

    Column(
        verticalArrangement =
            Arrangement.spacedBy(6.dp)
    ) {
        questions.forEach { question ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme
                            .colorScheme
                            .surfaceVariant,
                        RoundedCornerShape(14.dp)
                    )
                    .clickable {
                        onQuestionSelected(question)
                    }
                    .padding(
                        horizontal = 12.dp,
                        vertical = 8.dp
                    )
            ) {
                Text(
                    text = question,
                    style =
                        MaterialTheme
                            .typography
                            .bodySmall,
                    color =
                        MaterialTheme
                            .colorScheme
                            .onSurfaceVariant
                )
            }
        }
    }
}

private fun buildInitialMessage(
    tasks: List<Task>
): String {
    val pending = tasks.filterNot {
        it.completado
    }

    return when {
        tasks.isEmpty() ->
            "Aún no tienes tareas registradas. " +
                "Agrega una actividad y podré ayudarte."

        pending.isEmpty() ->
            "¡Excelente! No tienes tareas pendientes."

        pending.size >= 6 ->
            "Tienes ${pending.size} tareas pendientes. " +
                "Tu carga es alta; conviene comenzar por una prioridad alta."

        else ->
            "Tienes ${pending.size} tareas pendientes. " +
                "Pregúntame cuál deberías realizar primero."
    }
}

private fun answerQuestion(
    question: String,
    tasks: List<Task>
): String {
    val normalized = normalize(question)

    val pending = tasks.filterNot {
        it.completado
    }

    val completed = tasks.filter {
        it.completado
    }

    val today =
        SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault()
        ).format(Date())

    val todayTasks = pending.filter {
        normalizeDate(it.fecha) == today ||
            normalize(it.fecha).contains(
                normalize(
                    SimpleDateFormat(
                        "dd/MM/yyyy",
                        Locale.getDefault()
                    ).format(Date())
                )
            )
    }

    val highPriority = pending.filter {
        normalize(it.prioridad) == "alta"
    }

    val importantTask =
        pending.sortedWith(
            compareBy<Task> {
                when (
                    normalize(it.prioridad)
                ) {
                    "alta" -> 0
                    "media" -> 1
                    "baja" -> 2
                    else -> 3
                }
            }
                .thenBy {
                    it.fecha
                }
                .thenBy {
                    it.hora
                }
        ).firstOrNull()

    return when {
        normalized.contains("hoy") -> {
            if (todayTasks.isEmpty()) {
                "No encontré tareas pendientes con fecha de hoy."
            } else {
                val names = todayTasks
                    .take(3)
                    .joinToString(", ") {
                        it.titulo.ifBlank {
                            "Tarea sin título"
                        }
                    }

                "Sí. Tienes ${todayTasks.size} tarea(s) para hoy: $names."
            }
        }

        normalized.contains("cuantas") ||
            normalized.contains("pendientes") -> {
            if (pending.isEmpty()) {
                "No tienes tareas pendientes."
            } else {
                "Tienes ${pending.size} tarea(s) pendiente(s)."
            }
        }

        normalized.contains("importante") ||
            normalized.contains("primero") ||
            normalized.contains("urgente") -> {
            if (importantTask == null) {
                "No tienes tareas pendientes."
            } else {
                "Te recomiendo comenzar por " +
                    "\"${importantTask.titulo}\". " +
                    "Su prioridad es ${importantTask.prioridad}."
            }
        }

        normalized.contains("prioridad alta") ||
            normalized.contains("alta") -> {
            if (highPriority.isEmpty()) {
                "No tienes tareas pendientes de prioridad alta."
            } else {
                "Tienes ${highPriority.size} tarea(s) " +
                    "de prioridad alta: " +
                    highPriority
                        .take(3)
                        .joinToString(", ") {
                            it.titulo
                        }
            }
        }

        normalized.contains("complete") ||
            normalized.contains("completadas") -> {
            "Has completado ${completed.size} " +
                "de ${tasks.size} tareas."
        }

        normalized.contains("estudiar") ||
            normalized.contains("recomienda") ||
            normalized.contains("hacer") -> {
            if (importantTask == null) {
                "No tienes pendientes. Puedes repasar " +
                    "la materia que más se te dificulte."
            } else {
                "Estudia primero el tema relacionado con " +
                    "\"${importantTask.titulo}\". " +
                    "Trabaja 25 minutos y descansa 5."
            }
        }

        normalized.contains("hola") -> {
            "¡Hola! Puedo revisar tus tareas, prioridades " +
                "y progreso académico."
        }

        else -> {
            "Puedo responder preguntas como: " +
                "¿Tengo tareas hoy?, ¿cuál es la más importante?, " +
                "¿cuántas completé? o ¿qué debo estudiar?"
        }
    }
}

private fun normalize(
    value: String
): String {
    return value
        .lowercase()
        .replace("á", "a")
        .replace("é", "e")
        .replace("í", "i")
        .replace("ó", "o")
        .replace("ú", "u")
        .replace("ñ", "n")
        .trim()
}

private fun normalizeDate(
    value: String
): String {
    val formats = listOf(
        "yyyy-MM-dd",
        "dd/MM/yyyy",
        "dd-MM-yyyy"
    )

    formats.forEach { format ->
        try {
            val parser =
                SimpleDateFormat(
                    format,
                    Locale.getDefault()
                )

            parser.isLenient = false

            val date = parser.parse(value.trim())

            if (date != null) {
                return SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                ).format(date)
            }
        } catch (_: Exception) {
            // Se prueba el siguiente formato.
        }
    }

    return value.trim()
}