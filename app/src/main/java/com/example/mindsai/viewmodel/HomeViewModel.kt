package com.example.mindsai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindsai.local.UserSession
import com.example.mindsai.local.data.TaskEntity
import com.example.mindsai.repository.StudyRepository
import com.example.mindsai.repository.UserRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: StudyRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    val userId = UserSession.currentUser?.id ?: 0

    private val _studyTimer = MutableStateFlow(0L) // segundos
    val studyTimer = _studyTimer.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning = _isTimerRunning.asStateFlow()

    private var timerJob: Job? = null

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

    fun toggleStudyTimer() {
        if (_isTimerRunning.value) {
            stopStudyTimer()
        } else {
            startStudyTimer()
        }
    }

    private fun startStudyTimer() {
        _isTimerRunning.value = true
        timerJob = viewModelScope.launch {
            while (_isTimerRunning.value) {
                delay(1000)
                _studyTimer.value++
            }
        }
    }

    private fun stopStudyTimer() {
        _isTimerRunning.value = false
        timerJob?.cancel()
        
        val seconds = _studyTimer.value
        val user = UserSession.currentUser ?: return
        
        // Convertir a minutos totales
        val sessionMinutes = (seconds / 60).toInt()
        if (sessionMinutes > 0) {
            val totalMinutes = user.studyMinutes + sessionMinutes
            val totalHours = user.studyHours + (totalMinutes / 60)
            val remainingMinutes = totalMinutes % 60
            
            viewModelScope.launch {
                userRepository.updateStudyTime(user.id, totalHours, remainingMinutes)
                UserSession.currentUser = user.copy(studyHours = totalHours, studyMinutes = remainingMinutes)
                awardXp(sessionMinutes * 2) // 2 XP por minuto de estudio
            }
        }
        _studyTimer.value = 0
    }

    fun updateAverage(newAverage: Float) {
        val user = UserSession.currentUser ?: return
        viewModelScope.launch {
            userRepository.updateAverageGrade(user.id, newAverage)
            UserSession.currentUser = user.copy(averageGrade = newAverage)
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
