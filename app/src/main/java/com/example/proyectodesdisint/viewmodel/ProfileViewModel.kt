package com.example.proyectodesdisint.viewmodel

import androidx.lifecycle.ViewModel
import com.example.proyectodesdisint.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel : ViewModel() {

    private val _user = MutableStateFlow(User())
    val user: StateFlow<User> = _user

    fun updateUser(newUser: User) {
        _user.value = newUser
    }
}
