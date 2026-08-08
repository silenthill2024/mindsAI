package com.example.proyectdeswear.presentation.homepro.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import com.example.proyectdeswear.presentation.homepro.MindsAIColors

data class SubjectSummary(
    val title: String,
    val subtitle: String,
    val progress: Int,
    val color: Color
)

@Composable
fun SubjectCards(
    subjects: List<SubjectSummary>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp)
    ) {
        subjects.take(3).forEach { subject ->
            SubjectCard(subject)

            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}

@Composable
private fun SubjectCard(
    subject: SubjectSummary
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MindsAIColors.Card.copy(alpha = 0.95f),
                shape = RoundedCornerShape(18.dp)
            )
            .padding(
                horizontal = 12.dp,
                vertical = 9.dp
            )
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = subject.title,
                color = MindsAIColors.TextPrimary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )

            Text(
                text = subject.subtitle,
                color = MindsAIColors.TextSecondary,
                fontSize = 8.sp,
                maxLines = 1
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = "${subject.progress}%",
            color = subject.color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
