package com.example.proyectdeswear.presentation.homev2

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.example.proyectdeswear.presentation.Task

@Composable
fun MindsAIHomeV2(
    tasks: List<Task>,
    notificationCount: Int,
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(HomeV2Colors.Background)
    ) {
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            drawCircle(
                color = HomeV2Colors.Purple.copy(
                    alpha = 0.07f
                ),
                radius = size.minDimension * 0.43f,
                center = Offset(
                    size.width * 0.10f,
                    size.height * 0.12f
                )
            )

            drawCircle(
                color = HomeV2Colors.Cyan.copy(
                    alpha = 0.04f
                ),
                radius = size.minDimension * 0.40f,
                center = Offset(
                    size.width * 0.90f,
                    size.height * 0.88f
                )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    top = 7.dp,
                    bottom = 6.dp
                ),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {
            HomeV2Header(
                notificationCount = notificationCount,
                onNotificationsClick =
                    onNotificationsClick
            )

            Spacer(modifier = Modifier.height(3.dp))

            HomeV2AutomaticPager(
                tasks = tasks
            )
        }
    }
}
