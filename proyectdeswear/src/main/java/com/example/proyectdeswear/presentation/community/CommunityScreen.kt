package com.example.proyectdeswear.presentation.community

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import com.example.proyectdeswear.presentation.homepro.MindsAIColors

@Composable
fun CommunityScreen(
    modifier: Modifier = Modifier
) {
    ScalingLazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MindsAIColors.Background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "Comunidad",
                color = MindsAIColors.TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Actividad reciente",
                color = MindsAIColors.Cyan,
                fontSize = 9.sp
            )

            Spacer(modifier = Modifier.height(10.dp))
        }

        item {
            CommunityInfoCard(
                title = "Publicaciones",
                message = "Las nuevas publicaciones apareceran como notificaciones."
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            CommunityInfoCard(
                title = "Comentarios",
                message = "Recibiras un aviso cuando comenten o respondan."
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            CommunityInfoCard(
                title = "Sincronizacion",
                message = "La actividad de Comunidad se recibe desde el celular."
            )

            Spacer(modifier = Modifier.height(44.dp))
        }
    }
}

@Composable
private fun CommunityInfoCard(
    title: String,
    message: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp)
            .background(
                MindsAIColors.Card,
                RoundedCornerShape(22.dp)
            )
            .padding(13.dp)
    ) {
        Text(
            text = title,
            color = MindsAIColors.Purple,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = message,
            color = MindsAIColors.TextSecondary,
            fontSize = 9.sp,
            maxLines = 3
        )
    }
}
