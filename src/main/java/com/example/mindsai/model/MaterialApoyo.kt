package com.example.mindsai.model

data class MaterialApoyo(
    var id: String = "",
    val titulo: String = "",
    val descripcion: String = "",
    val urlMaterial: String = "", 
    val autor: String = "",
    val autorID: String = "",
    val fecha: Long = System.currentTimeMillis(),
    val materia: String = "General",
    val tipo: String = "LINK", // "LINK" o "ARCHIVO"
    val nombreArchivo: String? = null
)
