package com.example.mindsai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindsai.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: UserRepository
) : ViewModel() {

    fun login(
        correo: String,
        password: String,
        onSuccess: () -> Unit,
        onError: () -> Unit
    ) {

        viewModelScope.launch {

            val user =
                repository.login(
                    correo,
                    password
                )

            if (user != null)
                onSuccess()
            else
                onError()
        }
    }
}