package com.example.proyectodesdisint.ui.components

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import coil.compose.AsyncImage

@Composable
fun ProfileImageDisplay(
    photoUrl: String,
    userName: String,
    size: Dp,
    modifier: Modifier = Modifier
) {
    val bitmapState = remember(photoUrl) {
        if (photoUrl.startsWith("data:image")) {
            try {
                val base64String = photoUrl.substringAfter(",")
                val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
                BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            } catch (e: Exception) {
                null
            }
        } else {
            null
        }
    }

    if (bitmapState != null) {
        Image(
            bitmap = bitmapState.asImageBitmap(),
            contentDescription = null,
            modifier = modifier
                .size(size)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    } else if (photoUrl.isNotBlank() && !photoUrl.startsWith("data:image")) {
        AsyncImage(
            model = photoUrl,
            contentDescription = null,
            modifier = modifier
                .size(size)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    } else {
        LetterAvatar(name = userName, size = size, modifier = modifier)
    }
}
