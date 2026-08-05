package com.example.proyectodesdisint.viewmodel

import androidx.lifecycle.ViewModel
import com.example.proyectodesdisint.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.exp
import kotlin.random.Random

class LearningStateViewModel : ViewModel() {

    private val _learningState = MutableStateFlow<LearningAnalysis?>(null)
    val learningState: StateFlow<LearningAnalysis?> = _learningState

    fun analyzeLearningState(profile: UserProfile) {
        val features = buildFeatureVector(profile)
        
        // Simulación de Red Neuronal para el Estado de Aprendizaje
        // Capa de entrada (4) -> Oculta (5) -> Salida (3: Optimo, En desarrollo, Requiere apoyo)
        val network = TinyLearningNN(4, 5, 3)
        val predictions = network.predict(features)
        
        val states = listOf("Óptimo", "En desarrollo", "Requiere apoyo")
        val bestIndex = predictions.indices.maxByOrNull { predictions[it] } ?: 1
        
        _learningState.value = LearningAnalysis(
            state = states[bestIndex],
            index = (predictions[0] * 100).toInt(), // Índice basado en la probabilidad de "Óptimo"
            confidence = predictions[bestIndex],
            recommendations = generateRecommendations(states[bestIndex], profile)
        )
    }

    private fun buildFeatureVector(profile: UserProfile): DoubleArray {
        return doubleArrayOf(
            profile.xp.toDouble() / profile.xpMax.coerceAtLeast(1).toDouble(),
            (profile.tareasCompletadas.toDouble() / 50.0).coerceIn(0.0, 1.0),
            (profile.horasEstudio.toDouble() / 100.0).coerceIn(0.0, 1.0),
            (profile.promedioGeneral / 10.0).coerceIn(0.0, 1.0)
        )
    }

    private fun generateRecommendations(state: String, profile: UserProfile): List<String> {
        return when (state) {
            "Óptimo" -> listOf(
                "Mantén tu ritmo actual, estás destacando.",
                "Considera tutorar a otros compañeros para reforzar.",
                "Explora temas avanzados de tu carrera."
            )
            "En desarrollo" -> listOf(
                "Organiza mejor tus horas de estudio semanal.",
                "Completa 2 tareas más esta semana para subir de nivel.",
                "Revisa las materias en las que tienes menos XP."
            )
            else -> listOf(
                "Contacta a un profesor para asesoría personalizada.",
                "Establece metas diarias más pequeñas.",
                "Dedica al menos 1 hora diaria constante al estudio."
            )
        }
    }

    data class LearningAnalysis(
        val state: String,
        val index: Int,
        val confidence: Double,
        val recommendations: List<String>
    )

    private class TinyLearningNN(inputSize: Int, hiddenSize: Int, outputSize: Int) {
        private val weightsIH = Array(hiddenSize) { DoubleArray(inputSize) { Random.nextDouble(-1.0, 1.0) } }
        private val weightsHO = Array(outputSize) { DoubleArray(hiddenSize) { Random.nextDouble(-1.0, 1.0) } }

        fun predict(inputs: DoubleArray): DoubleArray {
            val hidden = DoubleArray(weightsIH.size) { i ->
                var sum = 0.0
                for (j in inputs.indices) sum += inputs[j] * weightsIH[i][j]
                1.0 / (1.0 + exp(-sum))
            }
            val output = DoubleArray(weightsHO.size) { i ->
                var sum = 0.0
                for (j in hidden.indices) sum += hidden[j] * weightsHO[i][j]
                exp(sum)
            }
            val sumExp = output.sum()
            return output.map { it / sumExp }.toDoubleArray()
        }
    }
}
