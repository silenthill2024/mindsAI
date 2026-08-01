package com.example.proyectdeswear.presentation.mindsai.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import com.example.proyectdeswear.presentation.mindsai.model.MoodResult
import com.example.proyectdeswear.presentation.mindsai.model.WellnessData

private val Surface = Color(0xFF181525)
private val Purple = Color(0xFF9D7BFF)
private val Cyan = Color(0xFF62D9E8)
private val Green = Color(0xFF55D6A9)
private val TextPrimary = Color(0xFFF7F4FF)
private val TextSecondary = Color(0xFFABA6BA)

@Composable
fun MoodCard(result: MoodResult) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 26.dp)
            .background(
                Surface,
                RoundedCornerShape(28.dp)
            )
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "ESTADO DE ANIMO",
            color = Purple,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .size(62.dp)
                .background(
                    Purple.copy(alpha = 0.16f),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = result.score.toString(),
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(7.dp))

        Text(
            text = result.title,
            color = Green,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = result.description,
            color = TextSecondary,
            fontSize = 9.sp,
            textAlign = TextAlign.Center,
            maxLines = 3
        )
    }
}

@Composable
fun HeartRateCard(data: WellnessData) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 26.dp)
            .background(
                Surface,
                RoundedCornerShape(28.dp)
            )
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "SALUD CARDIOVASCULAR",
            color = Cyan,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(9.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        Color(0xFFFF4D86).copy(alpha = 0.16f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "HR",
                    color = Color(0xFFFF4D86),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = Modifier.padding(start = 12.dp)
            ) {
                Text(
                    text = "${data.heartRate} bpm",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Ultima medicion",
                    color = TextSecondary,
                    fontSize = 8.sp
                )
            }
        }
    }
}

@Composable
fun RecommendationCard(result: MoodResult) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 26.dp)
            .background(
                Surface,
                RoundedCornerShape(28.dp)
            )
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "RECOMENDACION MINDSAI",
            color = Purple,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = result.recommendation,
            color = TextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            maxLines = 4
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = result.healthMessage,
            color = Cyan,
            fontSize = 8.sp,
            textAlign = TextAlign.Center,
            maxLines = 3
        )
    }
}
