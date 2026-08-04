package com.example.proyectodesdisint.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectodesdisint.data.AuthRepository
import com.example.proyectodesdisint.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val repo = AuthRepository()

    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile

    init {
        fetchUserProfile()
    }

    fun fetchUserProfile() {
        viewModelScope.launch {
            repo.getUserProfile().onSuccess {
                _userProfile.value = it
            }
        }
    }

    fun updateProfile(nombre: String, descripcion: String, profileImageUri: Uri? = null) {
        viewModelScope.launch {
            repo.updateUserProfile(nombre, descripcion, profileImageUri).onSuccess {
                fetchUserProfile()
            }
        }
    }

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
