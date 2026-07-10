package com.example.mindsai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindsai.local.data.UserEntity
import com.example.mindsai.repository.UserRepository
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val repository: UserRepository
) : ViewModel() {

    fun register(
        nombre: String,
        correo: String,
        password: String,
        onSuccess: () -> Unit
    ) {

        viewModelScope.launch {

            repository.register(
                UserEntity(
                    nombre = nombre,
                    correo = correo,
                    password = password
                )
            )
            onSuccess()
        }
    }
}