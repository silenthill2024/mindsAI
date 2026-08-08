package com.example.proyectdeswear.presentation.homepro.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Text
import com.example.proyectdeswear.presentation.homepro.MindsAIColors

enum class HomeNavItem {
    HOME,
    AGENDA,
    MINDSAI,
    COMMUNITY
}

@Composable
fun HomeBottomNavigation(
    selected: HomeNavItem,
    onSelect: (HomeNavItem) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 25.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NavigationButton("H", selected == HomeNavItem.HOME) {
            onSelect(HomeNavItem.HOME)
        }

        NavigationButton("A", selected == HomeNavItem.AGENDA) {
            onSelect(HomeNavItem.AGENDA)
        }

        NavigationButton("AI", selected == HomeNavItem.MINDSAI) {
            onSelect(HomeNavItem.MINDSAI)
        }

        NavigationButton("C", selected == HomeNavItem.COMMUNITY) {
            onSelect(HomeNavItem.COMMUNITY)
        }
    }
}

@Composable
private fun NavigationButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(if (selected) 38.dp else 32.dp)
            .background(
                color = if (selected) {
                    MindsAIColors.Purple
                } else {
                    MindsAIColors.CardSecondary
                },
                shape = CircleShape
            )
            .clickable(
                interactionSource = remember {
                    MutableInteractionSource()
                },
                indication = null,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = MindsAIColors.TextPrimary,
            fontSize = if (label == "AI") 8.sp else 11.sp
        )
    }
}
