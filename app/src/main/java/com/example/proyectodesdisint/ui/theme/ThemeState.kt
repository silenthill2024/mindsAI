package com.example.proyectodesdisint.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class ColorblindType {
    NONE, PROTANOPIA, DEUTERANOPIA, TRITANOPIA
}

object ThemeState {
    var isDarkTheme by mutableStateOf(true)
    var colorblindType by mutableStateOf(ColorblindType.NONE)
}
