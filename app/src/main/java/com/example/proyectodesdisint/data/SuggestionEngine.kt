package com.example.proyectodesdisint.data

import com.example.proyectodesdisint.model.Task
import java.util.Calendar
import kotlin.math.exp
import kotlin.math.roundToInt
import kotlin.random.Random

object SuggestionEngine {

    private const val INPUT_SIZE = 4
    private const val HIDDEN_SIZE = 6
    private const val OUTPUT_SIZE = 3

    fun generateSuggestion(tasks: List<Task>): String {
        if (tasks.isEmpty()) {
            return "No tienes tareas. Planea tu dia."
        }

        val overload = detectOverload(tasks)
        if (overload != null) {
            return overload
        }

        val pending = tasks.filter { !it.completado }
        val neuralRecommendation = getNeuralRecommendation(tasks)
        val nextTask = pending.firstOrNull()

        if (nextTask == null) {
            return "Has completado todas tus tareas."
        }

        return if (neuralRecommendation != null) {
            "La red neuronal recomienda trabajar en la ${neuralRecommendation.period.label} alrededor de las ${neuralRecommendation.hour}:00. Empieza con: ${nextTask.titulo}"
        } else {
            "Te recomiendo empezar con: ${nextTask.titulo}"
        }
    }

    fun recommendBestHour(tasks: List<Task>): String {
        val neuralRecommendation = getNeuralRecommendation(tasks)
            ?: return "Sin suficiente historial aun para entrenar la red neuronal"

        val confidence = (neuralRecommendation.confidence * 100).roundToInt()
        return "La red neuronal recomienda trabajar en la ${neuralRecommendation.period.label} alrededor de las ${neuralRecommendation.hour}:00 (confianza ${confidence}%)"
    }

    private fun getNeuralRecommendation(tasks: List<Task>): NeuralRecommendation? {
        val trainingSamples = buildTrainingSamples(tasks)
        if (trainingSamples.size < 3) {
            return null
        }

        val completed = tasks.filter { it.completado && it.completionTime != null }
        val completionHours = getCompletionHours(completed)
        val pendingCount = tasks.count { !it.completado }
        val completedCount = completed.size
        val averageHour = if (completionHours.isNotEmpty()) {
            completionHours.average()
        } else {
            Period.AFTERNOON.defaultHour.toDouble()
        }

        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY).toDouble()
        val currentFeatures = buildFeatureVector(
            currentHour = currentHour,
            pendingCount = pendingCount,
            completedCount = completedCount,
            averageCompletionHour = averageHour
        )

        val network = TinyNeuralNetwork(
            inputSize = INPUT_SIZE,
            hiddenSize = HIDDEN_SIZE,
            outputSize = OUTPUT_SIZE
        )
        network.train(trainingSamples, epochs = 450, learningRate = 0.18)

        val probabilities = network.predict(currentFeatures)
        val bestIndex = probabilities.indices.maxByOrNull { probabilities[it] } ?: return null
        val bestPeriod = Period.values()[bestIndex]
        val bestHour = representativeHourForPeriod(completionHours, bestPeriod)

        return NeuralRecommendation(
            period = bestPeriod,
            hour = bestHour,
            confidence = probabilities[bestIndex]
        )
    }

    private fun buildTrainingSamples(tasks: List<Task>): List<TrainingSample> {
        val completed = tasks.filter { it.completado && it.completionTime != null }
        val completionHours = getCompletionHours(completed)
        val pendingCount = tasks.count { !it.completado }
        val completedCount = completed.size
        val averageHour = if (completionHours.isNotEmpty()) {
            completionHours.average()
        } else {
            Period.AFTERNOON.defaultHour.toDouble()
        }

        val samples = mutableListOf<TrainingSample>()

        for (hour in completionHours) {
            val period = Period.fromHour(hour)
            samples += TrainingSample(
                inputs = buildFeatureVector(
                    currentHour = hour.toDouble(),
                    pendingCount = pendingCount,
                    completedCount = completedCount,
                    averageCompletionHour = averageHour
                ),
                target = oneHot(period.ordinal)
            )
        }


        for (period in Period.values()) {
            samples += TrainingSample(
                inputs = buildFeatureVector(
                    currentHour = period.defaultHour.toDouble(),
                    pendingCount = pendingCount,
                    completedCount = completedCount,
                    averageCompletionHour = averageHour
                ),
                target = oneHot(period.ordinal)
            )
        }

        return samples
    }

    private fun buildFeatureVector(
        currentHour: Double,
        pendingCount: Int,
        completedCount: Int,
        averageCompletionHour: Double
    ): DoubleArray {
        val totalTasks = (pendingCount + completedCount).coerceAtLeast(1)

        return doubleArrayOf(
            currentHour / 23.0,
            averageCompletionHour / 23.0,
            pendingCount.toDouble() / totalTasks.toDouble(),
            completedCount.toDouble() / totalTasks.toDouble()
        )
    }

    private fun oneHot(index: Int): DoubleArray {
        return DoubleArray(OUTPUT_SIZE) { if (it == index) 1.0 else 0.0 }
    }

    private fun getCompletionHours(tasks: List<Task>): List<Int> {
        return tasks.mapNotNull { task ->
            val completionTime = task.completionTime ?: return@mapNotNull null
            Calendar.getInstance().apply {
                timeInMillis = completionTime
            }.get(Calendar.HOUR_OF_DAY)
        }
    }

    private fun representativeHourForPeriod(hours: List<Int>, period: Period): Int {
        val matchingHours = hours.filter { it in period.range }
        if (matchingHours.isEmpty()) {
            return period.defaultHour
        }

        return matchingHours.average().roundToInt()
    }

    private fun detectOverload(tasks: List<Task>): String? {
        val pending = tasks.count { !it.completado }

        return when {
            pending > 10 -> "Tienes muchas tareas pendientes. Considera priorizar."
            pending > 6 -> "Tienes una carga alta de trabajo hoy."
            else -> null
        }
    }

    private data class TrainingSample(
        val inputs: DoubleArray,
        val target: DoubleArray
    )

    private data class NeuralRecommendation(
        val period: Period,
        val hour: Int,
        val confidence: Double
    )

    private enum class Period(val label: String, val defaultHour: Int, val range: IntRange) {
        MORNING("manana", 9, 5..11),
        AFTERNOON("tarde", 15, 12..17),
        NIGHT("noche", 20, 18..23);

        companion object {
            fun fromHour(hour: Int): Period {
                return when (hour) {
                    in MORNING.range -> MORNING
                    in AFTERNOON.range -> AFTERNOON
                    else -> NIGHT
                }
            }
        }
    }

    private class TinyNeuralNetwork(
        inputSize: Int,
        private val hiddenSize: Int,
        private val outputSize: Int
    ) {
        private val weightsInputHidden = Array(hiddenSize) { hiddenIndex ->
            DoubleArray(inputSize) { inputIndex ->
                Random(100 + hiddenIndex * 17 + inputIndex).nextDouble(-0.4, 0.4)
            }
        }
        private val hiddenBias = DoubleArray(hiddenSize)
        private val weightsHiddenOutput = Array(outputSize) { outputIndex ->
            DoubleArray(hiddenSize) { hiddenIndex ->
                Random(500 + outputIndex * 31 + hiddenIndex).nextDouble(-0.4, 0.4)
            }
        }
        private val outputBias = DoubleArray(outputSize)

        fun train(samples: List<TrainingSample>, epochs: Int, learningRate: Double) {
            repeat(epochs) {
                for (sample in samples) {
                    trainSingle(sample, learningRate)
                }
            }
        }

        fun predict(inputs: DoubleArray): DoubleArray {
            val hiddenLayer = forwardHidden(inputs)
            return forwardOutput(hiddenLayer)
        }

        private fun trainSingle(sample: TrainingSample, learningRate: Double) {
            val hiddenLayer = forwardHidden(sample.inputs)
            val outputLayer = forwardOutput(hiddenLayer)

            val outputDelta = DoubleArray(outputSize) { outputIndex ->
                outputLayer[outputIndex] - sample.target[outputIndex]
            }

            val hiddenDelta = DoubleArray(hiddenSize)
            for (hiddenIndex in 0 until hiddenSize) {
                var downstreamError = 0.0
                for (outputIndex in 0 until outputSize) {
                    downstreamError += outputDelta[outputIndex] * weightsHiddenOutput[outputIndex][hiddenIndex]
                }
                hiddenDelta[hiddenIndex] = downstreamError * hiddenLayer[hiddenIndex] * (1.0 - hiddenLayer[hiddenIndex])
            }

            for (outputIndex in 0 until outputSize) {
                for (hiddenIndex in 0 until hiddenSize) {
                    weightsHiddenOutput[outputIndex][hiddenIndex] -=
                        learningRate * outputDelta[outputIndex] * hiddenLayer[hiddenIndex]
                }
                outputBias[outputIndex] -= learningRate * outputDelta[outputIndex]
            }

            for (hiddenIndex in 0 until hiddenSize) {
                for (inputIndex in sample.inputs.indices) {
                    weightsInputHidden[hiddenIndex][inputIndex] -=
                        learningRate * hiddenDelta[hiddenIndex] * sample.inputs[inputIndex]
                }
                hiddenBias[hiddenIndex] -= learningRate * hiddenDelta[hiddenIndex]
            }
        }

        private fun forwardHidden(inputs: DoubleArray): DoubleArray {
            return DoubleArray(hiddenSize) { hiddenIndex ->
                var sum = hiddenBias[hiddenIndex]
                for (inputIndex in inputs.indices) {
                    sum += weightsInputHidden[hiddenIndex][inputIndex] * inputs[inputIndex]
                }
                sigmoid(sum)
            }
        }

        private fun forwardOutput(hiddenLayer: DoubleArray): DoubleArray {
            val rawOutput = DoubleArray(outputSize) { outputIndex ->
                var sum = outputBias[outputIndex]
                for (hiddenIndex in 0 until hiddenSize) {
                    sum += weightsHiddenOutput[outputIndex][hiddenIndex] * hiddenLayer[hiddenIndex]
                }
                sum
            }

            return softmax(rawOutput)
        }

        private fun sigmoid(value: Double): Double {
            return 1.0 / (1.0 + exp(-value))
        }

        private fun softmax(values: DoubleArray): DoubleArray {
            val maxValue = values.maxOrNull() ?: 0.0
            val exponentials = DoubleArray(values.size) { index ->
                exp(values[index] - maxValue)
            }
            val total = exponentials.sum().coerceAtLeast(1e-9)

            return DoubleArray(values.size) { index ->
                exponentials[index] / total
            }
        }
    }
}
