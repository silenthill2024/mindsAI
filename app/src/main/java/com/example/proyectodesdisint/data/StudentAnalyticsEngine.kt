package com.example.proyectodesdisint.data

import com.example.proyectodesdisint.model.Task
import java.text.SimpleDateFormat
import java.util.*

/**
 * Motor de análisis de rendimiento estudiantil (Simulación de Red Neuronal Logic)
 * Mide el progreso y proporciona insights basados en tareas.
 */
object StudentAnalyticsEngine {

    data class StudentPerformance(
        val progress: Float, // 0.0 to 1.0
        val performanceStatus: String, // "Excelente", "Regular", "En Riesgo"
        val statusColor: Long, // Color Hex
        val insights: String
    )

    fun analyzeStudentPerformance(tasks: List<Task>): StudentPerformance {
        if (tasks.isEmpty()) {
            return StudentPerformance(
                progress = 0f,
                performanceStatus = "Sin Datos",
                statusColor = 0xFF9E9E9E,
                insights = "El alumno aún no tiene tareas asignadas."
            )
        }

        val totalTasks = tasks.size
        val completedTasks = tasks.count { it.completado }
        val progress = completedTasks.toFloat() / totalTasks

        // Lógica de "Red Neuronal" simplificada basada en heurísticas de rendimiento
        return when {
            progress >= 0.8f -> StudentPerformance(
                progress = progress,
                performanceStatus = "Excelente",
                statusColor = 0xFF4CAF50, // Verde
                insights = "Alto compromiso y entrega puntual."
            )
            progress >= 0.5f -> StudentPerformance(
                progress = progress,
                performanceStatus = "Promedio",
                statusColor = 0xFF2196F3, // Azul
                insights = "Buen progreso, pero puede mejorar la constancia."
            )
            else -> StudentPerformance(
                progress = progress,
                performanceStatus = "En Riesgo",
                statusColor = 0xFFF44336, // Rojo
                insights = "Bajo nivel de entrega. Requiere atención."
            )
        }
    }
}
