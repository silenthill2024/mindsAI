package com.example.proyectodesdisint.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectodesdisint.data.FirebaseService
import com.example.proyectodesdisint.model.MaterialApoyo
import com.example.proyectodesdisint.model.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MaterialViewModel : ViewModel() {
    private val service = FirebaseService()
    
    private val _materials = MutableStateFlow<List<MaterialApoyo>>(emptyList())
    val materials: StateFlow<List<MaterialApoyo>> = _materials.asStateFlow()

    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        _isLoading.value = true
        service.listenMaterial { 
            _materials.value = it
            _isLoading.value = false
        }
        
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            viewModelScope.launch {
                _userProfile.value = service.getUserProfile(it.uid)
            }
        }
    }

    fun deleteMaterial(id: String) {
        service.deleteMaterial(id)
    }
}
