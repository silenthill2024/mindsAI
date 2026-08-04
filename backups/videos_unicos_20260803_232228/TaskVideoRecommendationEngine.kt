package com.example.proyectodesdisint.data

import com.example.proyectodesdisint.model.Task
import com.example.proyectodesdisint.ui.youtube.YouTubeSuggestion

object TaskVideoRecommendationEngine {

    private data class VideoRule(
        val video: YouTubeSuggestion,
        val keywords: Set<String>,
        val fallbackScore: Int = 0
    )

    private val rules = listOf(
        VideoRule(
            video = YouTubeSuggestion(
                videoId = "RpVs0dF4qTc",
                title = "Aritmética y Álgebra Elemental",
                description = "Curso de aritmética y álgebra elemental recomendado por MindsAI."
            ),
            keywords = setOf(
                "estudiar",
                "estudio",
                "examen",
                "tarea",
                "proyecto",
                "entrega",
                "concentracion",
                "investigar"
            ),
            fallbackScore = 6
        ),
        VideoRule(
            video = YouTubeSuggestion(
                videoId = "RpVs0dF4qTc",
                title = "Organiza mejor tu tiempo",
                description = "Curso de aritmética y álgebra elemental recomendado por MindsAI.",
                startSeconds = 268
            ),
            keywords = setOf(
                "urgente",
                "hoy",
                "manana",
                "entrega",
                "pendiente",
                "organizar",
                "agenda",
                "tiempo"
            ),
            fallbackScore = 5
        ),
        VideoRule(
            video = YouTubeSuggestion(
                videoId = "RpVs0dF4qTc",
                title = "Aprendizaje y memoria",
                description = "Curso de aritmética y álgebra elemental recomendado por MindsAI."
            ),
            keywords = setOf(
                "examen",
                "repasar",
                "memorizar",
                "aprender",
                "lectura",
                "resumen",
                "estudiar"
            ),
            fallbackScore = 4
        ),
        VideoRule(
            video = YouTubeSuggestion(
                videoId = "RpVs0dF4qTc",
                title = "Productividad para proyectos",
                description = "Curso de aritmética y álgebra elemental recomendado por MindsAI."
            ),
            keywords = setOf(
                "proyecto",
                "programacion",
                "codigo",
                "software",
                "desarrollo",
                "reporte",
                "presentacion",
                "documento"
            ),
            fallbackScore = 3
        ),
        VideoRule(
            video = YouTubeSuggestion(
                videoId = "RpVs0dF4qTc",
                title = "Reduce el estres academico",
                description = "Curso de aritmética y álgebra elemental recomendado por MindsAI."
            ),
            keywords = setOf(
                "estres",
                "ansiedad",
                "dificil",
                "muchas",
                "cansancio",
                "descanso",
                "salud"
            ),
            fallbackScore = 2
        ),
        VideoRule(
            video = YouTubeSuggestion(
                videoId = "RpVs0dF4qTc",
                title = "Motivacion para completar tareas",
                description = "Curso de aritmética y álgebra elemental recomendado por MindsAI."
            ),
            keywords = setOf(
                "motivacion",
                "empezar",
                "terminar",
                "pendiente",
                "actividad",
                "trabajo"
            ),
            fallbackScore = 1
        )
    )

    fun recommend(
        tasks: List<Task>,
        limit: Int = 6
    ): List<YouTubeSuggestion> {
        val pendingTasks = tasks.filterNot { it.completado }

        if (pendingTasks.isEmpty()) {
            return rules
                .sortedByDescending { it.fallbackScore }
                .take(limit)
                .map { it.video }
        }

        val taskText = pendingTasks.joinToString(" ") { task ->
            buildString {
                append(task.titulo)
                append(" ")
                append(task.descripcion)
                append(" ")
                append(task.prioridad)
            }
        }.normalizeText()

        val hasHighPriority = pendingTasks.any {
            it.prioridad.equals(
                "Alta",
                ignoreCase = true
            )
        }

        return rules
            .map { rule ->
                var score = rule.fallbackScore

                rule.keywords.forEach { keyword ->
                    if (taskText.contains(keyword.normalizeText())) {
                        score += 12
                    }
                }

                if (
                    hasHighPriority &&
                    rule.keywords.any {
                        it in setOf(
                            "urgente",
                            "tiempo",
                            "organizar",
                            "entrega",
                            "concentracion"
                        )
                    }
                ) {
                    score += 15
                }

                if (
                    pendingTasks.size >= 4 &&
                    rule.keywords.any {
                        it in setOf(
                            "estres",
                            "organizar",
                            "tiempo",
                            "descanso"
                        )
                    }
                ) {
                    score += 10
                }

                rule.video to score
            }
            .sortedByDescending { it.second }
            .take(limit)
            .mapIndexed { index, result ->
                val video = result.first

                video.copy(
                    description = createReason(
                        video = video,
                        pendingTasks = pendingTasks,
                        isFirst = index == 0
                    )
                )
            }
    }

    fun recommendationMessage(
        tasks: List<Task>
    ): String {
        val pending = tasks.filterNot { it.completado }

        if (pending.isEmpty()) {
            return "No tienes tareas pendientes. Puedes aprovechar para repasar o descansar."
        }

        val highPriority = pending.count {
            it.prioridad.equals(
                "Alta",
                ignoreCase = true
            )
        }

        return when {
            highPriority > 0 ->
                "Detecte $highPriority tarea(s) de prioridad alta. Los videos se ordenaron para ayudarte a concentrarte y organizar tu tiempo."

            pending.size >= 4 ->
                "Tienes ${pending.size} tareas pendientes. Te recomiendo organizacion, concentracion y manejo del estres."

            else ->
                "Analice ${pending.size} tarea(s) pendiente(s) y seleccione videos relacionados con su contenido."
        }
    }

    private fun createReason(
        video: YouTubeSuggestion,
        pendingTasks: List<Task>,
        isFirst: Boolean
    ): String {
        val prefix = if (isFirst) {
            "Recomendacion principal de MindsAI."
        } else {
            "Seleccionado segun tus tareas."
        }

        val firstTask =
            pendingTasks.firstOrNull()?.titulo.orEmpty()

        return if (firstTask.isNotBlank()) {
            "$prefix Relacionado con: $firstTask."
        } else {
            "$prefix ${video.description}"
        }
    }

    private fun String.normalizeText(): String {
        return lowercase()
            .replace("á", "a")
            .replace("é", "e")
            .replace("í", "i")
            .replace("ó", "o")
            .replace("ú", "u")
            .replace("ü", "u")
            .replace("ñ", "n")
    }
}
