package com.example.proyectodesdisint.model

data class UserProfile(
    val uid: String = "",
    val nombre: String = "",
    val username: String = "",
    val email: String = "",
    val universidad: String = "",
    val carrera: String = "",
    val gradoEstudio: String = "",
    val semestre: String = "",
    val grupo: String = "",
    val matricula: String = "",
    val ciudad: String = "",
    val biografia: String = "",
    val materiasInteres: List<String> = emptyList(),
    val materiasActuales: List<String> = emptyList(),
    val asesorias: List<String> = emptyList(),
    val objetivos: List<String> = emptyList(),
    val github: String = "",
    val linkedin: String = "",
    val perfilCompleto: Int = 0
)
