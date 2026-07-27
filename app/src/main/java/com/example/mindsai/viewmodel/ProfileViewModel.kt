package com.example.mindsai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindsai.local.UserSession
import com.example.mindsai.local.data.UserEntity
import com.example.mindsai.repository.UserRepository
import kotlinx.coroutines.launch

class ProfileViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun updateProfile(nombre: String, description: String) {
        val currentUser = UserSession.currentUser ?: return
        val updatedUser = currentUser.copy(nombre = nombre, description = description)
        
        viewModelScope.launch {
            userRepository.updateUser(updatedUser)
            UserSession.currentUser = updatedUser
        }
    }

    fun changePassword(newPassword: String) {
        val currentUser = UserSession.currentUser ?: return
        viewModelScope.launch {
            userRepository.updatePassword(currentUser.id, newPassword)
            UserSession.currentUser = currentUser.copy(password = newPassword)
        }
    }

    fun updateProfileImage(uri: String) {
        val currentUser = UserSession.currentUser ?: return
        val updatedUser = currentUser.copy(profileImageUrl = uri)
        
        viewModelScope.launch {
            userRepository.updateUser(updatedUser)
            UserSession.currentUser = updatedUser
        }
    }
}
