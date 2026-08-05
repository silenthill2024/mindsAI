package com.example.proyectodesdisint.model

data class MaterialApoyo(
    var id: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val enlace: String = "",
    val tipo: String = "LINK",
    val autorId: String = "",
    val autorNombre: String = "",
    val fecha: Long = System.currentTimeMillis()
)
