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
                title = "Aritm\u00E9tica y \u00C1lgebra Elemental",
                description = "Curso recomendado para tareas de matem\u00E1ticas, aritm\u00E9tica y \u00E1lgebra."
            ),
            keywords = setOf(
                "matematicas",
                "aritmetica",
                "algebra",
                "ecuacion",
                "numeros",
                "calculo",
                "operaciones"
            ),
            fallbackScore = 6
        ),

        VideoRule(
            video = YouTubeSuggestion(
                videoId = "AvGb_Q0nBFo",
                title = "C\u00F3mo recordarlo",
                description = "T\u00E9cnicas para recordar mejor lo que estudias."
            ),
            keywords = setOf(
                "recordar",
                "memoria",
                "memorizar",
                "repasar",
                "examen",
                "aprender",
                "estudiar"
            ),
            fallbackScore = 5
        ),

        VideoRule(
            video = YouTubeSuggestion(
                videoId = "4_OSNX4i134",
                title = "5 H\u00C1BITOS que TRANSFORMAR\u00C1N tu VIDA",
                description = "H\u00E1bitos para mejorar tu productividad y organizaci\u00F3n."
            ),
            keywords = setOf(
                "habitos",
                "productividad",
                "organizacion",
                "disciplina",
                "rutina",
                "motivacion",
                "tiempo"
            ),
            fallbackScore = 4
        ),

        VideoRule(
            video = YouTubeSuggestion(
                videoId = "dhQHPjmk0-k",
                title = "CONSEJOS para MEJORAR tu oratoria",
                description = "Consejos para exposiciones, presentaciones y comunicaci\u00F3n oral."
            ),
            keywords = setOf(
                "oratoria",
                "exposicion",
                "presentacion",
                "hablar",
                "comunicacion",
                "discurso",
                "publico"
            ),
            fallbackScore = 3
        ),

        VideoRule(
            video = YouTubeSuggestion(
                videoId = "AV7mh5AWiJk",
                title = "Aprender Ingl\u00E9s",
                description = "Recurso recomendado para estudiar y practicar ingl\u00E9s."
            ),
            keywords = setOf(
                "ingles",
                "english",
                "idioma",
                "vocabulario",
                "pronunciacion",
                "grammar",
                "traduccion"
            ),
            fallbackScore = 2
        ),)

    fun recommend(
        tasks: List<Task>,
        limit: Int = 6
    ): List<YouTubeSuggestion> {

        val pendingTasks = tasks.filterNot {
            it.completado
        }

        if (pendingTasks.isEmpty()) {
            return rules
                .distinctBy { it.video.videoId }
                .sortedByDescending {
                    it.fallbackScore
                }
                .take(limit)
                .map {
                    it.video
                }
        }

        val taskText = pendingTasks
            .joinToString(" ") { task ->
                buildString {
                    append(task.titulo)
                    append(" ")
                    append(task.descripcion)
                    append(" ")
                    append(task.prioridad)
                }
            }
            .normalizeText()

        val hasHighPriority = pendingTasks.any {
            it.prioridad.equals(
                "Alta",
                ignoreCase = true
            )
        }

        return rules
            .distinctBy {
                it.video.videoId
            }
            .map { rule ->

                var score = rule.fallbackScore

                rule.keywords.forEach { keyword ->
                    if (
                        taskText.contains(
                            keyword.normalizeText()
                        )
                    ) {
                        score += 12
                    }
                }

                if (
                    hasHighPriority &&
                    rule.keywords.any {
                        it in setOf(
                            "tiempo",
                            "organizacion",
                            "concentracion",
                            "estudiar",
                            "productividad"
                        )
                    }
                ) {
                    score += 15
                }

                if (
                    pendingTasks.size >= 4 &&
                    rule.keywords.any {
                        it in setOf(
                            "habitos",
                            "organizacion",
                            "tiempo",
                            "concentracion"
                        )
                    }
                ) {
                    score += 10
                }

                rule.video to score
            }
            .sortedByDescending {
                it.second
            }
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

        val pending = tasks.filterNot {
            it.completado
        }

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
                "Detect\u00E9 $highPriority tarea(s) de prioridad alta y orden\u00E9 los recursos para ayudarte."

            pending.size >= 4 ->
                "Tienes ${pending.size} tareas pendientes. MindsAI seleccion\u00F3 recursos para organizarte y estudiar."

            else ->
                "Analic\u00E9 ${pending.size} tarea(s) pendiente(s) y seleccion\u00E9 recursos relacionados."
        }
    }

    private fun createReason(
        video: YouTubeSuggestion,
        pendingTasks: List<Task>,
        isFirst: Boolean
    ): String {

        val prefix = if (isFirst) {
            "Recomendaci\u00F3n principal de MindsAI."
        } else {
            "Seleccionado seg\u00FAn tus tareas."
        }

        val firstTask =
            pendingTasks.firstOrNull()
                ?.titulo
                .orEmpty()

        return if (firstTask.isNotBlank()) {
            "$prefix Relacionado con: $firstTask."
        } else {
            "$prefix ${video.description}"
        }
    }

    private fun String.normalizeText(): String {
        return lowercase()
            .replace("\u00E1", "a")
            .replace("\u00E9", "e")
            .replace("\u00ED", "i")
            .replace("\u00F3", "o")
            .replace("\u00FA", "u")
            .replace("\u00FC", "u")
            .replace("\u00F1", "n")
    }
}