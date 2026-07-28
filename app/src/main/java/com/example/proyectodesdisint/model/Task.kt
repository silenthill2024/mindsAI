package com.example.proyectodesdisint.model

data class Task(
    var id: String = "",
    var titulo: String = "",
    var descripcion: String = "",
    var fecha: String = "",
    var hora: String = "",
    var completado: Boolean = false,
    var completionTime: Long? = null,
    var documentId: String = ""
)