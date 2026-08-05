package com.example.proyectodesdisint.ui.youtube

data class YouTubeSuggestion(
    val videoId: String,
    val title: String,
    val description: String,
    val startSeconds: Int = 0
)

val suggestedVideos = listOf(

    YouTubeSuggestion(
        videoId = "RpVs0dF4qTc",
        title = "Aritm\u00E9tica y \u00C1lgebra Elemental",
        description = "Curso de aritm\u00E9tica y \u00E1lgebra elemental."
    ),

    YouTubeSuggestion(
        videoId = "AvGb_Q0nBFo",
        title = "C\u00F3mo recordarlo",
        description = "T\u00E9cnicas para mejorar la memoria y el aprendizaje."
    ),

    YouTubeSuggestion(
        videoId = "4_OSNX4i134",
        title = "5 H\u00C1BITOS que TRANSFORMAR\u00C1N tu VIDA",
        description = "H\u00E1bitos para aumentar tu productividad."
    ),

    YouTubeSuggestion(
        videoId = "dhQHPjmk0-k",
        title = "CONSEJOS para MEJORAR tu oratoria",
        description = "Mejora tus exposiciones y tu comunicaci\u00F3n oral."
    ),

    YouTubeSuggestion(
        videoId = "AV7mh5AWiJk",
        title = "Aprender Ingl\u00E9s",
        description = "Contenido recomendado para practicar ingl\u00E9s."
    ),)