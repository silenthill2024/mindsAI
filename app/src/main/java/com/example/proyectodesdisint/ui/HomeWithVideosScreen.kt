package com.example.proyectodesdisint.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.proyectodesdisint.ui.youtube.YouTubeSuggestionsCarousel

@Composable
fun HomeWithVideosScreen(
    navController: NavController
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "Videos recomendados para ti",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(
                start = 20.dp,
                end = 20.dp,
                top = 12.dp,
                bottom = 8.dp
            )
        )

        YouTubeSuggestionsCarousel(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 385.dp)
        )

        HomeScreen(
            navController = navController,
            modifier = Modifier.weight(1f)
        )
    }
}
