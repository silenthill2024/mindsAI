package com.example.proyectdeswear.presentation.homepro

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyectdeswear.presentation.Task
import com.example.proyectdeswear.presentation.agenda.AgendaScreen
import com.example.proyectdeswear.presentation.community.CommunityScreen
import com.example.proyectdeswear.presentation.homepro.components.AcademicIndicator
import com.example.proyectdeswear.presentation.homepro.components.AcademicProgressRing
import com.example.proyectdeswear.presentation.homepro.components.HomeBottomNavigation
import com.example.proyectdeswear.presentation.homepro.components.HomeNavItem
import com.example.proyectdeswear.presentation.homepro.components.IndicatorType
import com.example.proyectdeswear.presentation.homepro.components.MindsAIHeader
import com.example.proyectdeswear.presentation.homepro.components.NeuralBackground
import com.example.proyectdeswear.presentation.homepro.components.NeuralRecommendationCard
import com.example.proyectdeswear.presentation.homepro.components.SubjectCards
import com.example.proyectdeswear.presentation.homepro.components.SubjectSummary
import com.example.proyectdeswear.presentation.mindsai.MindsAIRecommendationsScreen
import kotlinx.coroutines.delay

@Composable
fun MindsAIProfessionalHome(
    tasks: List<Task>,
    notificationCount: Int,
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val completed = tasks.count { it.completado }
    val pending = tasks.count { !it.completado }

    val progress = if (tasks.isEmpty()) {
        0f
    } else {
        completed.toFloat() / tasks.size.toFloat()
    }

    val recommendation = when {
        pending >= 7 ->
            "La red neuronal detecta carga alta. Empieza por la tarea mas urgente."

        pending >= 4 ->
            "La red neuronal recomienda trabajar en bloques cortos."

        pending > 0 ->
            "Tu carga esta controlada. Avanza en la siguiente actividad."

        else ->
            "No tienes pendientes. Aprovecha para descansar."
    }

    val subjects = listOf(
        SubjectSummary(
            title = "Materia",
            subtitle = "Seminario de IA",
            progress = 80,
            color = MindsAIColors.Purple
        ),
        SubjectSummary(
            title = "Materia",
            subtitle = "Filosofia de la mente",
            progress = 55,
            color = MindsAIColors.Cyan
        ),
        SubjectSummary(
            title = "Tesis",
            subtitle = "Capitulo actual",
            progress = 40,
            color = MindsAIColors.Blue
        )
    )

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { 3 }
    )

    var selectedNav by remember {
        mutableStateOf(HomeNavItem.HOME)
    }

    LaunchedEffect(
        pagerState.currentPage,
        selectedNav
    ) {
        if (selectedNav == HomeNavItem.HOME) {
            delay(5_000)

            if (!pagerState.isScrollInProgress) {
                pagerState.animateScrollToPage(
                    (pagerState.currentPage + 1) % 3
                )
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MindsAIColors.Background)
    ) {
        NeuralBackground(
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (selectedNav) {
                HomeNavItem.HOME -> {
                    Spacer(modifier = Modifier.height(13.dp))

                    MindsAIHeader(
                        notificationCount = notificationCount,
                        onNotificationsClick = onNotificationsClick
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) { page ->
                        when (page) {
                            0 -> {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 25.dp),
                                    horizontalArrangement =
                                        Arrangement.SpaceBetween,
                                    verticalAlignment =
                                        Alignment.CenterVertically
                                ) {
                                    AcademicIndicator(
                                        title = "Clases",
                                        value = 80,
                                        color = MindsAIColors.Purple,
                                        type = IndicatorType.CLASSES
                                    )

                                    AcademicProgressRing(
                                        progress = progress
                                    )

                                    AcademicIndicator(
                                        title = "Investigacion",
                                        value = 20,
                                        color = MindsAIColors.Cyan,
                                        type = IndicatorType.RESEARCH
                                    )
                                }
                            }

                            1 -> {
                                SubjectCards(subjects = subjects)
                            }

                            2 -> {
                                NeuralRecommendationCard(
                                    recommendation = recommendation
                                )
                            }
                        }
                    }
                }

                HomeNavItem.AGENDA -> {
                    AgendaScreen(
                        tasks = tasks,
                        modifier = Modifier.weight(1f)
                    )
                }

                HomeNavItem.MINDSAI -> {
                    MindsAIRecommendationsScreen(
                        tasks = tasks,
                        modifier = Modifier.weight(1f)
                    )
                }

                HomeNavItem.COMMUNITY -> {
                    CommunityScreen(
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            HomeBottomNavigation(
                selected = selectedNav,
                onSelect = { selectedNav = it }
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
