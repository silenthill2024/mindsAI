package com.example.proyectodesdisint.ui.youtube

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

private const val AUTO_SCROLL_DELAY = 5_000L

@Composable
fun YouTubeSuggestionsCarousel(
    videos: List<YouTubeSuggestion> = suggestedVideos,
    modifier: Modifier = Modifier
) {
    if (videos.isEmpty()) {
        return
    }

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = {
            videos.size
        }
    )

    val screenWidth =
        LocalConfiguration.current.screenWidthDp.dp

    LaunchedEffect(
        pagerState.currentPage,
        pagerState.isScrollInProgress,
        videos.size
    ) {
        if (
            videos.size > 1 &&
            !pagerState.isScrollInProgress
        ) {
            delay(AUTO_SCROLL_DELAY)

            val nextPage =
                (pagerState.currentPage + 1) %
                    videos.size

            pagerState.animateScrollToPage(
                nextPage
            )
        }
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment =
            Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(
                horizontal = 20.dp
            ),
            pageSpacing = 12.dp
        ) { page ->
            YouTubeSuggestionCard(
                suggestion = videos[page],
                isActive =
                    pagerState.currentPage == page &&
                    !pagerState.isScrollInProgress,
                modifier = Modifier.width(
                    screenWidth - 40.dp
                )
            )
        }

        Row(
            modifier = Modifier.padding(
                top = 10.dp
            ),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            videos.indices.forEach { index ->
                Box(
                    modifier = Modifier
                        .size(
                            if (
                                pagerState.currentPage ==
                                index
                            ) {
                                9.dp
                            } else {
                                6.dp
                            }
                        )
                        .background(
                            color =
                                if (
                                    pagerState.currentPage ==
                                    index
                                ) {
                                    Color(0xFF8B5CF6)
                                } else {
                                    Color(0xFF4B465A)
                                },
                            shape = CircleShape
                        )
                )

                if (index < videos.lastIndex) {
                    Spacer(
                        modifier = Modifier.width(5.dp)
                    )
                }
            }
        }
    }
}
