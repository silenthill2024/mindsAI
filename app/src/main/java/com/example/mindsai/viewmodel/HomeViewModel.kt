package com.example.mindsai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindsai.local.UserSession
import com.example.mindsai.local.data.TaskEntity
import com.example.mindsai.repository.StudyRepository
import com.example.mindsai.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: StudyRepository,
    private val userRepository: UserRepository
) : ViewModel() {

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
            val wasCompleted = task.isCompleted
            val isNowCompleted = progress >= 1f
            
            repository.updateTask(task.copy(progress = progress, isCompleted = isNowCompleted))

            // Lógica de Gamificación: +50 XP si se completa la tarea
            if (!wasCompleted && isNowCompleted) {
                awardXp(50)
            }
        }
    }

    private fun awardXp(amount: Int) {
        val user = UserSession.currentUser ?: return
        var newXp = user.xp + amount
        var newLevel = user.level
        var newCompleted = user.tasksCompleted + 1

        // Lógica de nivel (cada 1000 XP sube nivel)
        if (newXp >= 1000) {
            newLevel++
            newXp -= 1000
        }

        viewModelScope.launch {
            userRepository.updateGamification(user.id, newXp, newLevel, newCompleted)
            // Actualizamos la sesión en memoria
            UserSession.currentUser = user.copy(xp = newXp, level = newLevel, tasksCompleted = newCompleted)
        }
    }
}
