package com.example.proyectdeswear.data

data class WearTask(
    val id: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val fecha: String = "",
    val hora: String = "",
    val prioridad: String = "",
    val completado: Boolean = false,
    val asignadaPor: String = ""
)