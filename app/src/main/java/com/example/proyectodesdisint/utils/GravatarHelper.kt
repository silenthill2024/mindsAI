package com.example.proyectodesdisint.utils

import java.security.MessageDigest
import java.util.Locale

object GravatarHelper {
    fun getGravatarUrl(email: String, size: Int = 200): String {
        val cleanEmail = email.trim().lowercase(Locale.getDefault())
        val hash = md5(cleanEmail)
        return "https://www.gravatar.com/avatar/$hash?s=$size&d=identicon"
    }

    private fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val bytes = md.digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
