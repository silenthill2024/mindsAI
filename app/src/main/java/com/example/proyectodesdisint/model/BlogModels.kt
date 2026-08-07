package com.example.proyectodesdisint.model

data class BlogPost(
    var id: String = "",
    val titulo: String = "",
    val contenido: String = "",
    val fecha: Long = System.currentTimeMillis(),
    val autor: String = "Usuario MindsAI",
    val autorID: String = "",
    val autorPhotoUrl: String = ""
)

data class BlogReply(
    var id: String = "",
    val texto: String = "",
    val autor: String = "MindsAI Assistant",
    val fecha: Long = System.currentTimeMillis(),
    val autorID: String = "",
    val autorPhotoUrl: String = ""
)
