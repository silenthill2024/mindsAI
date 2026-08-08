package com.example.proyectdeswear.presentation.mindsai

import com.example.proyectdeswear.presentation.mindsai.model.MoodResult
import com.example.proyectdeswear.presentation.mindsai.model.WellnessData

object MoodAnalyzer {

    fun analyze(data: WellnessData): MoodResult {
        var score = 100

        if (data.heartRate > 100) score -= 20
        if (data.heartRate < 50) score -= 15

        if (data.stressLevel > 70) {
            score -= 30
        } else if (data.stressLevel > 45) {
            score -= 15
        }

        if (data.sleepHours < 5f) {
            score -= 25
        } else if (data.sleepHours < 7f) {
            score -= 10
        }

        if (data.pendingTasks >= 8) {
            score -= 20
        } else if (data.pendingTasks >= 4) {
            score -= 10
        }

        score = score.coerceIn(0, 100)

        val title = when {
            score >= 80 -> "Muy bien"
            score >= 60 -> "Estable"
            score >= 40 -> "Cansancio moderado"
            else -> "Necesitas descansar"
        }

        val description = when {
            score >= 80 ->
                "Tu estado general se observa positivo."

            score >= 60 ->
                "Tu estado es estable, pero cuida tus pausas."

            score >= 40 ->
                "Se detectan indicios de cansancio o carga elevada."

            else ->
                "Tu cuerpo y tu carga academica requieren atencion."
        }

        val healthMessage = when {
            data.heartRate in 60..100 ->
                "Ritmo cardiaco dentro de un rango comun."

            data.heartRate > 100 ->
                "Ritmo cardiaco elevado. Descansa y vuelve a medir."

            else ->
                "Ritmo cardiaco bajo. Repite la medicion en reposo."
        }

        val recommendation = when {
            data.sleepHours < 6f ->
                "Prioriza el descanso antes de continuar estudiando."

            data.stressLevel > 60 ->
                "Realiza una pausa breve y ejercicios de respiracion."

            data.pendingTasks >= 5 ->
                "Empieza por la tarea mas cercana a vencer."

            else ->
                "Mantiene tu rutina y avanza en tu siguiente actividad."
        }

        return MoodResult(
            title = title,
            description = description,
            score = score,
            healthMessage = healthMessage,
            recommendation = recommendation
        )
    }
}
