package com.example.proyectodesdisint.ui.youtube

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

@Composable
fun YouTubeThumbnail(
    videoId: String,
    modifier: Modifier = Modifier
) {
    val imageUrl =
        "https://img.youtube.com/vi/$videoId/hqdefault.jpg"

    val bitmap by produceState<Bitmap?>(
        initialValue = null,
        key1 = imageUrl
    ) {
        value = withContext(Dispatchers.IO) {
            loadThumbnail(imageUrl)
        }
    }

    Box(
        modifier = modifier.background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap!!.asImageBitmap(),
                contentDescription = "Miniatura de YouTube",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            CircularProgressIndicator(
                color = Color(0xFF8B3DFF)
            )

            Text(
                text = "Cargando...",
                color = Color.White,
                modifier = Modifier.align(
                    Alignment.BottomCenter
                )
            )
        }
    }
}

private fun loadThumbnail(
    imageUrl: String
): Bitmap? {
    return try {
        val connection =
            URL(imageUrl).openConnection() as HttpURLConnection

        connection.connectTimeout = 10_000
        connection.readTimeout = 10_000
        connection.instanceFollowRedirects = true
        connection.doInput = true
        connection.connect()

        connection.inputStream.use {
            BitmapFactory.decodeStream(it)
        }
    } catch (_: Exception) {
        null
    }
}
