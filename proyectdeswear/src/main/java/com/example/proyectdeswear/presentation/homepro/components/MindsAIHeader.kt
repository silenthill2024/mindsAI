package com.example.proyectdeswear.presentation.homepro.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import com.example.proyectdeswear.presentation.homepro.MindsAIColors

@Composable
fun MindsAIHeader(
    notificationCount: Int,
    onNotificationsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            BrainLogo(
                modifier = Modifier.size(27.dp)
            )

            Text(
                text = "Minds",
                color = MindsAIColors.TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "AI",
                color = MindsAIColors.Purple,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        NotificationBell(
            count = notificationCount,
            onClick = onNotificationsClick
        )
    }
}

@Composable
private fun BrainLogo(
    modifier: Modifier
) {
    Canvas(modifier = modifier) {
        drawCircle(
            color = MindsAIColors.Purple.copy(alpha = 0.20f),
            radius = size.minDimension * 0.48f
        )

        val left = Offset(
            size.width * 0.35f,
            size.height * 0.50f
        )

        val right = Offset(
            size.width * 0.65f,
            size.height * 0.50f
        )

        drawCircle(
            color = MindsAIColors.Purple,
            radius = size.minDimension * 0.18f,
            center = left
        )

        drawCircle(
            color = MindsAIColors.Cyan,
            radius = size.minDimension * 0.18f,
            center = right
        )

        drawLine(
            color = MindsAIColors.TextPrimary,
            start = Offset(
                size.width * 0.45f,
                size.height * 0.50f
            ),
            end = Offset(
                size.width * 0.55f,
                size.height * 0.50f
            ),
            strokeWidth = 2.dp.toPx()
        )
    }
}
