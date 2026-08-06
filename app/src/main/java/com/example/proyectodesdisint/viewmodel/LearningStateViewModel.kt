package com.example.proyectodesdisint.viewmodel

import androidx.lifecycle.ViewModel
import com.example.proyectodesdisint.model.UserProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.exp

/**
 * Analiza el estado de aprendizaje del alumno usando una red neuronal
 * de arquitectura fija (4 entradas -> 5 ocultas -> 3 salidas).
 *
 * IMPORTANTE: los pesos NO son aleatorios. Fueron diseñados a mano para que
 * cada neurona de la capa oculta represente un criterio pedagógico concreto
 * (ver comentarios en TinyLearningNN). Esto garantiza que, para un mismo
 * perfil de alumno, el resultado sea siempre el mismo (determinista) y que
 * la clasificación tenga sentido: más XP, más tareas completadas, más horas
 * de estudio y mejor promedio deben acercar al alumno a "Óptimo", y lo
 * contrario debe acercarlo a "Requiere apoyo".
 */
class LearningStateViewModel : ViewModel() {

    private val _learningState = MutableStateFlow<LearningAnalysis?>(null)
    val learningState: StateFlow<LearningAnalysis?> = _learningState

    // La red se crea una sola vez (pesos fijos), no en cada análisis.
    private val network = TinyLearningNN()

    fun analyzeLearningState(profile: UserProfile) {
        val features = buildFeatureVector(profile)

        val predictions = network.predict(features)

        val states = listOf("Óptimo", "En desarrollo", "Requiere apoyo")
        val bestIndex = predictions.indices.maxByOrNull { predictions[it] } ?: 1

        _learningState.value = LearningAnalysis(
            state = states[bestIndex],
            index = (predictions[0] * 100).toInt(), // Probabilidad de "Óptimo" (0-100)
            confidence = predictions[bestIndex],
            recommendations = generateRecommendations(states[bestIndex], profile)
        )
    }

    private fun buildFeatureVector(profile: UserProfile): DoubleArray {
        return doubleArrayOf(
            (profile.xp.toDouble() / profile.xpMax.coerceAtLeast(1).toDouble()).coerceIn(0.0, 1.0),
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

    /**
     * Red neuronal feed-forward con pesos fijos (hand-crafted), no entrenados
     * por gradiente pero diseñados para que cada neurona tenga un significado:
     *
     * Entradas: [xpRatio, tareasRatio, horasRatio, promedioRatio]  (cada una 0.0 a 1.0)
     *
     * Capa oculta (5 neuronas):
     *  h0 = Rendimiento general (promedio ponderado de las 4 entradas)
     *  h1 = Esfuerzo/constancia (horas de estudio + tareas completadas)
     *  h2 = Desempeño académico (promedio de calificaciones con más peso)
     *  h3 = Indicador de riesgo (se activa cuando TODAS las entradas son bajas)
     *  h4 = Progreso/constancia (xp + tareas completadas)
     *
     * Capa de salida (3 neuronas -> softmax):
     *  out0 = Óptimo         (combina h0,h1,h2,h4 positivo; h3 negativo)
     *  out1 = En desarrollo  (zona media, poco sensible a los extremos)
     *  out2 = Requiere apoyo (h3 positivo; el resto negativo)
     *
     * Cada neurona oculta tiene un bias calculado para que, con un alumno
     * "promedio" (todas las entradas en 0.5), su activación sigmoide sea
     * cercana a 0.5. Así la red reacciona simétricamente hacia arriba o
     * hacia abajo según el desempeño real del alumno.
     */
    private class TinyLearningNN {

        // Pesos capa oculta: [neurona][entrada]
        private val weightsIH = arrayOf(
            doubleArrayOf(1.2, 1.2, 1.0, 1.4),   // h0 rendimiento general
            doubleArrayOf(0.2, 1.6, 1.6, 0.2),   // h1 esfuerzo/constancia
            doubleArrayOf(0.3, 0.3, 0.3, 2.2),   // h2 desempeño académico
            doubleArrayOf(-1.3, -1.3, -1.0, -1.6), // h3 riesgo (pesos negativos)
            doubleArrayOf(1.0, 1.4, 0.8, 0.4)    // h4 progreso/constancia
        )

        // Bias por neurona oculta (centra la sigmoide en 0.5 para un alumno promedio)
        private val biasIH = doubleArrayOf(-2.4, -1.8, -1.55, 2.6, -1.8)

        // Pesos capa de salida: [neurona salida][neurona oculta]
        private val weightsHO = arrayOf(
            doubleArrayOf(1.8, 0.8, 1.2, -2.4, 0.8),    // out0 Óptimo
            doubleArrayOf(0.6, 0.5, 0.5, -0.6, 0.5),    // out1 En desarrollo
            doubleArrayOf(-1.0, -0.6, -0.8, 2.6, -0.6)  // out2 Requiere apoyo
        )

        // Bias por neurona de salida (calibrado para que un alumno "promedio"
        // (todas las entradas en 0.5) caiga en "En desarrollo", y solo un
        // desempeño claramente alto o bajo mueva la clasificación al extremo)
        private val biasHO = doubleArrayOf(-1.1, 0.5, -1.3)

        fun predict(inputs: DoubleArray): DoubleArray {
            val hidden = DoubleArray(weightsIH.size) { i ->
                var sum = biasIH[i]
                for (j in inputs.indices) sum += inputs[j] * weightsIH[i][j]
                sigmoid(sum)
            }

            val output = DoubleArray(weightsHO.size) { i ->
                var sum = biasHO[i]
                for (j in hidden.indices) sum += hidden[j] * weightsHO[i][j]
                exp(sum)
            }

            val sumExp = output.sum()
            return output.map { it / sumExp }.toDoubleArray() // softmax
        }

        private fun sigmoid(x: Double): Double = 1.0 / (1.0 + exp(-x))
    }
}
