package com.example.proyectodesdisint.ui.homev3

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.proyectodesdisint.data.TaskVideoRecommendationEngine
import com.example.proyectodesdisint.ui.youtube.YouTubeSuggestion
import com.example.proyectodesdisint.ui.youtube.YouTubeSuggestionsCarousel
import com.example.proyectodesdisint.viewmodel.HomeViewModel
import com.example.proyectodesdisint.viewmodel.HomeViewModelFactory

@Composable
fun HomeV3Screen(
    navController: NavController
) {
    val context = LocalContext.current

    val homeViewModel: HomeViewModel = viewModel(
        factory = HomeViewModelFactory(
            context.applicationContext
        )
    )

    val tasks by homeViewModel.tasks.collectAsState()

    val recommendedVideos:
        List<YouTubeSuggestion> = remember(tasks) {
            TaskVideoRecommendationEngine.recommend(
                tasks = tasks
            )
        }

    val recommendationMessage = remember(tasks) {
        TaskVideoRecommendationEngine
            .recommendationMessage(tasks)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(
                MaterialTheme.colorScheme.background
            ),
        verticalArrangement =
            Arrangement.spacedBy(14.dp),
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {
        item {
            HomeV3Header(
                pendingTasks =
                    tasks.count { !it.completado }
            )
        }

        item {
            YouTubeSection(
                videos = recommendedVideos
            )
        }

        item {
            NeuralRecommendationCard(
                message = recommendationMessage
            )
        }

        item {
            DailyOverviewCard(
                totalTasks = tasks.size,
                pendingTasks =
                    tasks.count { !it.completado },
                completedTasks =
                    tasks.count { it.completado }
            )
        }
        item {
            TaskListSection(
                tasks = tasks,
                viewModel = homeViewModel
            )
        }

        item {
            Spacer(
                modifier = Modifier.height(80.dp)
            )
        }
    }
}

@Composable
private fun HomeV3Header(
    pendingTasks: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = 20.dp
            )
    ) {
        Text(
            text = "MindsAI",
            style =
                MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = if (pendingTasks == 0) {
                "No tienes tareas pendientes"
            } else {
                "Hoy tienes $pendingTasks tarea(s) pendiente(s)"
            },
            style = MaterialTheme.typography.bodyMedium,
            color =
                MaterialTheme.colorScheme
                    .onSurfaceVariant
        )
    }
}

@Composable
private fun YouTubeSection(
    videos: List<YouTubeSuggestion>
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Videos recomendados para tus tareas",
            style =
                MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color =
                MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(
                horizontal = 20.dp,
                vertical = 8.dp
            )
        )

        YouTubeSuggestionsCarousel(
            videos = videos,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun NeuralRecommendationCard(
    message: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .background(
                color =
                    MaterialTheme.colorScheme
                        .primaryContainer,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(18.dp)
    ) {
        Text(
            text = "Recomendacion MindsAI",
            style =
                MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color =
                MaterialTheme.colorScheme
                    .onPrimaryContainer
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        Text(
            text = message,
            style =
                MaterialTheme.typography.bodyMedium,
            color =
                MaterialTheme.colorScheme
                    .onPrimaryContainer
        )
    }
}

@Composable
private fun DailyOverviewCard(
    totalTasks: Int,
    pendingTasks: Int,
    completedTasks: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .background(
                color =
                    MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(24.dp)
            )
            .padding(18.dp)
    ) {
        Text(
            text = "Resumen de hoy",
            style =
                MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color =
                MaterialTheme.colorScheme.onSurface
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text =
                "Total: $totalTasks  |  Pendientes: $pendingTasks  |  Completadas: $completedTasks",
            style =
                MaterialTheme.typography.bodyMedium,
            color =
                MaterialTheme.colorScheme
                    .onSurfaceVariant
        )
    }
}
