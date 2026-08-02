package com.example.mindsai.model

data class User(
    val uid: String = "",
    val nombre: String = "",
    val email: String = "",
    val role: String = "ALUMNO" // "ADMIN", "PROFE", "ALUMNO"
)
