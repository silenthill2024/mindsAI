package com.example.proyectodesdisint.model

data class User(
    val uid: String = "",
    val nombre: String = "",
    val email: String = "",
    val role: String = "ALUMNO",
    val photoUrl: String = ""
) {
    val canManageMaterials: Boolean
        get() = role.uppercase() == "ADMIN" ||
            role.uppercase() == "PROFE" ||
            email == "admin@mindsai.com"

    val isAdmin: Boolean
        get() = role.uppercase() == "ADMIN" || email == "admin@mindsai.com"
}
