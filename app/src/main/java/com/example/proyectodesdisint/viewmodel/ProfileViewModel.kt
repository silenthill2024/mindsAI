package com.example.proyectodesdisint.viewmodel

import androidx.lifecycle.ViewModel
import com.example.proyectodesdisint.data.ProfileRepository
import com.example.proyectodesdisint.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel : ViewModel() {

    private val repository = ProfileRepository()

    private val _profile = MutableStateFlow(UserProfile())
    val profile: StateFlow<UserProfile> = _profile

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    init {
        loadProfile()
    }

    fun loadProfile() {
        _isLoading.value = true
        _message.value = null

        repository.loadProfile(
            onSuccess = { userProfile ->
                _profile.value = userProfile
                _isLoading.value = false
            },
            onFailure = { error ->
                _message.value =
                    "No se pudo cargar el perfil: ${error.message}"
                _isLoading.value = false
            }
        )
    }

    fun saveProfile(profile: UserProfile) {
        _isLoading.value = true
        _message.value = null

        repository.saveProfile(
            profile = profile,
            onSuccess = {
                _profile.value = profile
                _message.value = "Perfil guardado correctamente"
                _isLoading.value = false
                loadProfile()
            },
            onFailure = { error ->
                _message.value =
                    "No se pudo guardar el perfil: ${error.message}"
                _isLoading.value = false
            }
        )
    }

    fun clearMessage() {
        _message.value = null
    }
}
