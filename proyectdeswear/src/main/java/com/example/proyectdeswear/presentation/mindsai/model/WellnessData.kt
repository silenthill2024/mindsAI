package com.example.proyectdeswear.presentation.mindsai.model

data class WellnessData(
    val heartRate: Int = 72,
    val stressLevel: Int = 24,
    val sleepHours: Float = 7.5f,
    val pendingTasks: Int = 0,
    val completedTasks: Int = 0
)

data class MoodResult(
    val title: String,
    val description: String,
    val score: Int,
    val healthMessage: String,
    val recommendation: String
)
