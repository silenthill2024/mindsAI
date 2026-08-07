package com.example.proyectdeswear.data

import android.content.Context

data class WearUserSession(
    val uid: String = "",
    val nombre: String = "",
    val email: String = "",
    val role: String = "ALUMNO",
    val photoUrl: String = ""
)

class WearSessionStore(
    context: Context
) {

    private val prefs =
        context.getSharedPreferences(
            "mindsai_wear_session",
            Context.MODE_PRIVATE
        )

    fun saveSession(
        session: WearUserSession
    ) {
        prefs.edit()
            .putString("uid", session.uid)
            .putString("nombre", session.nombre)
            .putString("email", session.email)
            .putString("role", session.role)
            .putString("photoUrl", session.photoUrl)
            .apply()
    }

    fun getSession(): WearUserSession {
        return WearUserSession(
            uid = prefs.getString("uid", "") ?: "",
            nombre = prefs.getString("nombre", "") ?: "",
            email = prefs.getString("email", "") ?: "",
            role = prefs.getString("role", "ALUMNO") ?: "ALUMNO",
            photoUrl = prefs.getString("photoUrl", "") ?: ""
        )
    }

    fun hasSession(): Boolean {
        return getSession().uid.isNotBlank()
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}