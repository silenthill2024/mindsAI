package com.example.proyectodesdisint.ui.youtube

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
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color

@Composable
fun YouTubeSuggestionsCarousel(
    modifier: Modifier = Modifier
) {
    val videos = suggestedVideos

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = {
            videos.size
        }
    )

    val screenWidth =
        LocalConfiguration.current.screenWidthDp.dp

    androidx.compose.foundation.layout.Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
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
            modifier = Modifier.padding(top = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            videos.indices.forEach { index ->
                androidx.compose.foundation.layout.Box(
                    modifier = Modifier
                        .size(
                            if (pagerState.currentPage == index) {
                                9.dp
                            } else {
                                6.dp
                            }
                        )
                        .background(
                            color =
                                if (pagerState.currentPage == index) {
                                    Color(0xFF8B3DFF)
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
