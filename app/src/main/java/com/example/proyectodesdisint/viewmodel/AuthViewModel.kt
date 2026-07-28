package com.example.proyectodesdisint.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectodesdisint.data.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repo = AuthRepository()

    fun register(
        nombre: String,
        email: String,
        password: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repo.register(nombre, email, password)

            if (result.isSuccess) {
                onResult(true, "Usuario registrado correctamente")
            } else {
                onResult(false, result.exceptionOrNull()?.message ?: "Error")
            }
        }
    }

    fun login(
        email: String,
        password: String,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            val result = repo.login(email, password)

            if (result.isSuccess) {
                onResult(true, "Login exitoso")
            } else {
                onResult(false, result.exceptionOrNull()?.message ?: "Error")
            }
        }
    }
    fun logout() {
        repo.logout()
    }
}