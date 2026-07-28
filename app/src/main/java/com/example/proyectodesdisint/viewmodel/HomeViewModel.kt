package com.example.proyectodesdisint.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.proyectodesdisint.data.FirebaseService
import com.example.proyectodesdisint.data.SuggestionEngine
import com.example.proyectodesdisint.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel(private val appContext: Context) : ViewModel() {

    private val firebaseService = FirebaseService()

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    private val _suggestion = MutableStateFlow("")
    val suggestion: StateFlow<String> = _suggestion

    init {
        firebaseService.listenTasks { taskList ->
            _tasks.value = taskList
            _suggestion.value = SuggestionEngine.generateSuggestion(taskList)
        }
    }

    fun addTask(task: Task) {
        println("AGREGANDO TAREA: ${task.titulo}")
        firebaseService.addTask(task)
    }

    fun toggleTaskCompletion(task: Task) {
        val isNowCompleted = !task.completado

        val updatedTask = task.copy(
            completado = isNowCompleted,
            completionTime = if (isNowCompleted) System.currentTimeMillis() else task.completionTime
        )
        firebaseService.updateTask(updatedTask)
    }

    fun deleteTask(task: Task) {
        firebaseService.deleteTask(task.documentId)
    }

    fun getRecommendedTime(): String {
        return SuggestionEngine.recommendBestHour(_tasks.value)
    }

    fun updateTask(task: Task) {
        firebaseService.updateTask(task)
    }
}
