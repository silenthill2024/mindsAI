package com.example.proyectodesdisint.ui.youtube

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun YouTubeSuggestionCard(
    suggestion: YouTubeSuggestion,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    fun openVideo() {
        val youtubeUri =
            Uri.parse("vnd.youtube:${suggestion.videoId}")

        val webUrl = buildString {
            append("https://www.youtube.com/watch?v=")
            append(suggestion.videoId)

            if (suggestion.startSeconds > 0) {
                append("&t=")
                append(suggestion.startSeconds)
                append("s")
            }
        }

        try {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    youtubeUri
                )
            )
        } catch (_: ActivityNotFoundException) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(webUrl)
                )
            )
        }
    }

    Column(
        modifier = modifier
            .background(
                color = Color(0xFF151225),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(12.dp),
        verticalArrangement =
            Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .clip(RoundedCornerShape(18.dp))
                .clickable {
                    openVideo()
                },
            contentAlignment = Alignment.Center
        ) {
            YouTubeThumbnail(
                videoId = suggestion.videoId,
                modifier = Modifier.fillMaxWidth()
            )

            Box(
                modifier = Modifier
                    .size(58.dp)
                    .background(
                        Color.Black.copy(alpha = 0.70f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Reproducir",
                    tint = Color.White,
                    modifier = Modifier.size(36.dp)
                )
            }

            if (isActive) {
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(10.dp)
                        .background(
                            Color(0xFF8B3DFF),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(
                            horizontal = 9.dp,
                            vertical = 4.dp
                        )
                ) {
                    Text(
                        text = "Sugerencia MindsAI",
                        color = Color.White,
                        style =
                            MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Text(
            text = suggestion.title,
            color = Color.White,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = suggestion.description,
            color = Color(0xFFAAA6B8),
            style = MaterialTheme.typography.bodySmall
        )

        Button(
            onClick = {
                openVideo()
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8B3DFF)
            ),
            shape = RoundedCornerShape(18.dp)
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )

            Text(
                text = "Ver video",
                modifier = Modifier.padding(start = 6.dp)
            )
        }
    }
}
