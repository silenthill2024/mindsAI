package com.example.mindsai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindsai.local.UserSession
import com.example.mindsai.local.data.TaskEntity
import com.example.mindsai.repository.StudyRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: StudyRepository) : ViewModel() {

    val userId = UserSession.currentUser?.id ?: 0

    val tasks: StateFlow<List<TaskEntity>> = repository.getTasksForUser(userId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addTask(title: String, description: String) {
        viewModelScope.launch {
            repository.addTask(
                TaskEntity(
                    userId = userId,
                    title = title,
                    description = description
                )
            )
        }
    }

    fun updateTaskProgress(task: TaskEntity, progress: Float) {
        viewModelScope.launch {
            repository.updateTask(task.copy(progress = progress, isCompleted = progress >= 1f))
        }
    }
}
