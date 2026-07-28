package com.example.proyectodesdisint.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyectodesdisint.data.TareaRepository
import com.example.proyectodesdisint.model.Task
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateListOf

class TareaViewModel : ViewModel() {

    private val repo = TareaRepository()

    var tareas = mutableStateListOf<Task>()
        private set

    fun cargarTareas() {
        viewModelScope.launch {
            tareas.clear()
            tareas.addAll(repo.obtenerTareas())
        }
    }

    fun agregarTarea(titulo: String, descripcion: String, fecha: String) {
        viewModelScope.launch {
            val tarea = Task(
                titulo = titulo,
                descripcion = descripcion,
                fecha = fecha
            )
            repo.agregarTarea(tarea)
            cargarTareas()
        }
    }
}