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
                videoId = "T8_7vV2sY-M",
                title = "Concentracion para estudiar",
                description = "MindsAI lo recomienda para comenzar tareas pendientes."
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
                videoId = "o65KewRwq_4",
                title = "Organiza mejor tu tiempo",
                description = "Recomendado para tareas urgentes o con fecha proxima.",
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
                videoId = "GL0ZPEhebag",
                title = "Aprendizaje y memoria",
                description = "Ideal para examenes, repasos y actividades academicas."
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
                videoId = "aOepWJeb7bQ",
                title = "Productividad para proyectos",
                description = "Sugerencia para avanzar en proyectos y entregas largas."
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
                videoId = "CJUdYgJuYfA",
                title = "Reduce el estres academico",
                description = "Recomendado cuando tienes varias tareas pendientes."
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
                videoId = "69qKUoGhoQo",
                title = "Motivacion para completar tareas",
                description = "Una recomendacion para recuperar el ritmo de trabajo."
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
