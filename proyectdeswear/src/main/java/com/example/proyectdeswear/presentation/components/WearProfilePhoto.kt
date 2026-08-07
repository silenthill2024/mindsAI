package com.example.proyectdeswear.presentation.components

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Text
import coil.compose.AsyncImage

@Composable
fun WearProfilePhoto(
    photoUrl: String,
    userName: String,
    size: Dp = 38.dp
) {

    val normalized =
        photoUrl.trim()

    when {

        normalized.startsWith(
            "data:image",
            ignoreCase = true
        ) -> {

            val imageBitmap =
                remember(normalized) {

                    try {

                        val base64 =
                            normalized.substringAfter(
                                "base64,"
                            )

                        val bytes =
                            Base64.decode(
                                base64,
                                Base64.DEFAULT
                            )

                        BitmapFactory
                            .decodeByteArray(
                                bytes,
                                0,
                                bytes.size
                            )
                            ?.asImageBitmap()

                    } catch (_: Exception) {
                        null
                    }
                }

            if (imageBitmap != null) {

                Image(
                    bitmap = imageBitmap,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(size)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )

            } else {

                ProfileInitial(
                    userName = userName,
                    size = size
                )
            }
        }

        normalized.startsWith("http://") ||
        normalized.startsWith("https://") -> {

            AsyncImage(
                model = normalized,
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        else -> {

            ProfileInitial(
                userName = userName,
                size = size
            )
        }
    }
}

@Composable
private fun ProfileInitial(
    userName: String,
    size: Dp
) {

    val initial =
        userName
            .trim()
            .firstOrNull()
            ?.uppercase()
            ?: "?"

    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(Color(0xFF5D4FDB)),
        contentAlignment = Alignment.Center
    ) {

        Text(
            text = initial,
            color = Color.White
        )
    }
}