package com.example.proyectodesdisint.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.proyectodesdisint.data.LocalPhotoService
import com.example.proyectodesdisint.data.ProfileImageService
import com.example.proyectodesdisint.data.ProfileRepository
import com.example.proyectodesdisint.model.UserProfile
import com.example.proyectodesdisint.streaming.WearSyncManager
import android.net.Uri
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ProfileRepository()
    private val imageService = ProfileImageService(application)
    private val localPhotoService = LocalPhotoService(application)
    private val wearSyncManager = WearSyncManager(application)

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
                // 1. Obtener la URL de Firestore (Base64 o URL remota)
                val remotePhotoUrl = userProfile.photoUrl
                
                // 2. Intentar obtener el path local si existe en este dispositivo
                val localPath = localPhotoService.getLocalPhotoPath(userProfile.uid)
                
                // 3. Priorizar local, pero si no existe o no es válida, usar la remota de Firestore
                val finalPhotoUrl = if (localPath != null && java.io.File(localPath).exists()) {
                    localPath
                } else {
                    remotePhotoUrl
                }
                
                val finalProfile = userProfile.copy(photoUrl = finalPhotoUrl)
                _profile.value = finalProfile
                wearSyncManager.syncUserProfile(finalProfile)
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
                wearSyncManager.syncUserProfile(profile)
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

    fun uploadProfileImage(uri: Uri) {
        _isLoading.value = true
        val currentUid = _profile.value.uid
        
        if (currentUid.isBlank()) {
            _message.value = "Error: Usuario no identificado"
            _isLoading.value = false
            return
        }

        // 1. Guardar localmente vinculado al UID
        val localPath = localPhotoService.savePhotoLocally(uri, currentUid)
        
        if (localPath != null) {
            _profile.value = _profile.value.copy(photoUrl = localPath)
            _message.value = "Foto actualizada"
            
            // 2. Sincronizar con Firestore (Base64 optimizado)
            imageService.uploadProfileImage(
                imageUri = uri,
                onSuccess = { base64Url ->
                    // Actualizar el perfil en el repositorio para que persista el cambio de photoUrl (Base64)
                    val updatedProfile = _profile.value.copy(photoUrl = base64Url)
                    repository.saveProfile(
                        profile = updatedProfile,
                        onSuccess = {
                            // Cargar de nuevo para asegurar consistencia
                            loadProfile()
                        },
                        onFailure = {
                            _isLoading.value = false
                        }
                    )
                },
                onFailure = { 
                    _isLoading.value = false
                }
            )
        } else {
            _message.value = "Error al guardar foto local"
            _isLoading.value = false
        }
    }
}

class ProfileViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
