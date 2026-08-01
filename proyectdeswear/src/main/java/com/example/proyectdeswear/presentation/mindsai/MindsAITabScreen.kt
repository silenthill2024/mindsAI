package com.example.proyectdeswear.presentation.mindsai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import com.example.proyectdeswear.presentation.Task
import com.example.proyectdeswear.presentation.mindsai.components.HeartRateCard
import com.example.proyectdeswear.presentation.mindsai.components.MoodCard
import com.example.proyectdeswear.presentation.mindsai.components.RecommendationCard
import com.example.proyectdeswear.presentation.mindsai.model.WellnessData

@Composable
fun MindsAITabScreen(
    tasks: List<Task>,
    heartRate: Int = 72,
    stressLevel: Int = 24,
    sleepHours: Float = 7.5f
) {
    val wellnessData = remember(
        tasks,
        heartRate,
        stressLevel,
        sleepHours
    ) {
        WellnessData(
            heartRate = heartRate,
            stressLevel = stressLevel,
            sleepHours = sleepHours,
            pendingTasks = tasks.count {
                !it.completado
            },
            completedTasks = tasks.count {
                it.completado
            }
        )
    }

    val result = remember(wellnessData) {
        MoodAnalyzer.analyze(wellnessData)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF080711))
            .verticalScroll(rememberScrollState())
            .padding(
                top = 24.dp,
                bottom = 28.dp
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "MindsAI",
            color = Color(0xFF9D7BFF),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Bienestar inteligente",
            color = Color(0xFFABA6BA),
            fontSize = 9.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        MoodCard(result)

        Spacer(modifier = Modifier.height(10.dp))

        HeartRateCard(wellnessData)

        Spacer(modifier = Modifier.height(10.dp))

        RecommendationCard(result)
    }
}
