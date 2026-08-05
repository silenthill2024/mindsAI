package com.example.proyectodesdisint.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.proyectodesdisint.data.FirebaseService
import com.example.proyectodesdisint.data.SuggestionEngine
import com.example.proyectodesdisint.model.Task
import com.example.proyectodesdisint.streaming.WearSyncManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel(private val appContext: Context) : ViewModel() {

    private val firebaseService = FirebaseService()

    private val wearSyncManager = WearSyncManager(appContext)

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

        firebaseService.addTask(
            task = task,

            onSuccess = { createdTask ->

                val json = """
                    {
                      "type": "TASK_CREATED",
                      "data": {
                        "documentId": "${createdTask.documentId}",
                        "titulo": "${createdTask.titulo}",
                        "descripcion": "${createdTask.descripcion}",
                        "fecha": "${createdTask.fecha}",
                        "hora": "${createdTask.hora}",
                        "prioridad": "${createdTask.prioridad}",
                        "completado": ${createdTask.completado}
                      }
                    }
                """.trimIndent()

                wearSyncManager.sendEventToWatch(
                    json = json,
                    path = "/stream_event"
                )
            },

            onFailure = { error ->
                println("ERROR AGREGANDO TAREA: ${error.message}")
            }
        )
    }

    fun toggleTaskCompletion(task: Task) {

        val isNowCompleted = !task.completado

        val updatedTask = task.copy(
            completado = isNowCompleted,
            completionTime =
                if (isNowCompleted) {
                    System.currentTimeMillis()
                } else {
                    task.completionTime
                }
        )

        firebaseService.updateTask(
            task = updatedTask,

            onSuccess = { savedTask ->

                val json = """
                    {
                      "type": "TASK_UPDATED",
                      "data": {
                        "documentId": "${savedTask.documentId}",
                        "titulo": "${savedTask.titulo}",
                        "descripcion": "${savedTask.descripcion}",
                        "fecha": "${savedTask.fecha}",
                        "hora": "${savedTask.hora}",
                        "prioridad": "${savedTask.prioridad}",
                        "completado": ${savedTask.completado},
                        "completionTime": ${savedTask.completionTime ?: 0}
                      }
                    }
                """.trimIndent()

                wearSyncManager.sendEventToWatch(
                    json = json,
                    path = "/stream_event"
                )
            },

            onFailure = { error ->
                println("ERROR ACTUALIZANDO TAREA: ${error.message}")
            }
        )
    }

    fun deleteTask(task: Task) {

        firebaseService.deleteTask(
            documentId = task.documentId,

            onSuccess = { documentId ->

                val json = """
                    {
                      "type": "TASK_DELETED",
                      "data": {
                        "documentId": "$documentId"
                      }
                    }
                """.trimIndent()

                wearSyncManager.sendEventToWatch(
                    json = json,
                    path = "/stream_event"
                )
            },

            onFailure = { error ->
                println("ERROR ELIMINANDO TAREA: ${error.message}")
            }
        )
    }

    fun getRecommendedTime(): String {
        return SuggestionEngine.recommendBestHour(_tasks.value)
    }

    fun updateTask(task: Task) {

        firebaseService.updateTask(
            task = task,

            onSuccess = { updatedTask ->

                val json = """
                    {
                      "type": "TASK_UPDATED",
                      "data": {
                        "documentId": "${updatedTask.documentId}",
                        "titulo": "${updatedTask.titulo}",
                        "descripcion": "${updatedTask.descripcion}",
                        "fecha": "${updatedTask.fecha}",
                        "hora": "${updatedTask.hora}",
                        "prioridad": "${updatedTask.prioridad}",
                        "completado": ${updatedTask.completado},
                        "completionTime": ${updatedTask.completionTime ?: 0}
                      }
                    }
                """.trimIndent()

                wearSyncManager.sendEventToWatch(
                    json = json,
                    path = "/stream_event"
                )
            },

            onFailure = { error ->
                println("ERROR EDITANDO TAREA: ${error.message}")
            }
        )
    }
}
