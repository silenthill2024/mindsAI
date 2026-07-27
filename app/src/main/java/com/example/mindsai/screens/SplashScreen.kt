package com.example.mindsai.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

import androidx.compose.ui.res.painterResource
import com.example.mindsai.R

@Composable
fun SplashScreen(onNavigateToLogin: () -> Unit) {
    // Animación de escala y transparencia
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        // Animación de entrada
        scale.animateTo(
            targetValue = 1.2f,
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
        delay(1500) // Tiempo que dura la portada
        onNavigateToLogin()
    }

    val gradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF673AB7), Color(0xFF311B92))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradient),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.alpha(alpha.value).scale(scale.value)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_logo),
                contentDescription = "MindsAI Logo",
                tint = Color.Unspecified,
                modifier = Modifier.size(150.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "MindsAI",
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
            
            Text(
                text = "Potenciando tu aprendizaje",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.7f),
                fontWeight = FontWeight.Light
            )
        }
        
        // Indicador de carga sutil en la parte inferior
        Text(
            text = "Cargando sistemas SQL & NoSQL...",
            color = Color.White.copy(alpha = 0.4f),
            fontSize = 10.sp,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}
