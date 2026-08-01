package com.example.proyectodesdisint.ui.youtube

data class YouTubeSuggestion(
    val videoId: String,
    val title: String,
    val description: String,
    val startSeconds: Int = 0
)

val suggestedVideos = listOf(
    YouTubeSuggestion(
        videoId = "T8_7vV2sY-M",
        title = "Video recomendado 1",
        description = "Contenido recomendado por MindsAI."
    ),
    YouTubeSuggestion(
        videoId = "o65KewRwq_4",
        title = "Video recomendado 2",
        description = "Vista previa desde el minuto seleccionado.",
        startSeconds = 268
    ),
    YouTubeSuggestion(
        videoId = "GL0ZPEhebag",
        title = "Video recomendado 3",
        description = "Contenido relacionado con aprendizaje."
    ),
    YouTubeSuggestion(
        videoId = "aOepWJeb7bQ",
        title = "Video recomendado 4",
        description = "Sugerencia seleccionada para el usuario."
    ),
    YouTubeSuggestion(
        videoId = "CJUdYgJuYfA",
        title = "Video recomendado 5",
        description = "Material para ampliar conocimientos."
    ),
    YouTubeSuggestion(
        videoId = "69qKUoGhoQo",
        title = "Video recomendado 6",
        description = "Contenido sugerido por MindsAI."
    )
)
