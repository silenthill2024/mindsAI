package com.example.proyectdeswear.presentation.homev2

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.proyectdeswear.presentation.Task
import kotlinx.coroutines.delay

private enum class HomeV2Page {
    SUMMARY,
    NEXT_TASK,
    RECOMMENDATION
}

@Composable
fun HomeV2AutomaticPager(
    tasks: List<Task>,
    modifier: Modifier = Modifier
) {
    val orderedTasks = orderedHomeV2Tasks(tasks)
    val nextTask = orderedTasks.firstOrNull()

    val completed = tasks.count {
        it.completado
    }

    val pending = tasks.count {
        !it.completado
    }

    val progress = if (tasks.isEmpty()) {
        0f
    } else {
        completed.toFloat() / tasks.size.toFloat()
    }

    val pages = HomeV2Page.entries

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { pages.size }
    )

    /*
     * Reinicia el tiempo cada vez que cambia la página,
     * incluso cuando el usuario desliza manualmente.
     */
    LaunchedEffect(pagerState.currentPage) {
        delay(5_000)

        if (!pagerState.isScrollInProgress) {
            val nextPage =
                (pagerState.currentPage + 1) %
                    pages.size

            pagerState.animateScrollToPage(nextPage)
        }
    }

    androidx.compose.foundation.layout.Column(
        modifier = modifier,
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(226.dp),
            pageSpacing = 8.dp,
            verticalAlignment = Alignment.CenterVertically
        ) { page ->
            when (pages[page]) {
                HomeV2Page.SUMMARY -> {
                    HomeV2SummaryCard(
                        progress = progress,
                        completed = completed,
                        pending = pending
                    )
                }

                HomeV2Page.NEXT_TASK -> {
                    HomeV2NextTaskCard(
                        task = nextTask
                    )
                }

                HomeV2Page.RECOMMENDATION -> {
                    HomeV2RecommendationCard(
                        task = nextTask
                    )
                }
            }
        }

        HomeV2PaginationDots(
            pagerState = pagerState,
            total = pages.size
        )
    }
}

@Composable
private fun HomeV2PaginationDots(
    pagerState: PagerState,
    total: Int
) {
    Row(
        modifier = Modifier.height(16.dp),
        horizontalArrangement =
            Arrangement.Center,
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        repeat(total) { index ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .size(
                        if (
                            pagerState.currentPage == index
                        ) {
                            8.dp
                        } else {
                            5.dp
                        }
                    )
                    .background(
                        color = if (
                            pagerState.currentPage == index
                        ) {
                            HomeV2Colors.Purple
                        } else {
                            HomeV2Colors.SurfaceLight
                        },
                        shape = CircleShape
                    )
            )
        }
    }
}
