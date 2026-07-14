package com.example.mindsai.local

import com.example.mindsai.local.data.UserEntity

/**
 * Objeto sencillo para mantener la sesión del usuario activo.
 * Aquí conectamos el mundo Relacional (UserEntity) con el resto de la app.
 */
object UserSession {
    var currentUser: UserEntity? = null
}
