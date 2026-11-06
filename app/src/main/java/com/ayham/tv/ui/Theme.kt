package com.ayham.tv.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object AyhamColors {
    val Background = Color(0xFF05020A)
    val Surface = Color(0xFF140A24)
    val SurfaceFocused = Color(0xFF2A1450)
    val Purple = Color(0xFF7B2FF7)
    val PurpleLight = Color(0xFFB388FF)
    val TextPrimary = Color(0xFFFFFFFF)
    val TextSecondary = Color(0xFFB9A8D6)
}

private val scheme = darkColorScheme(
    primary = AyhamColors.Purple,
    background = AyhamColors.Background,
    surface = AyhamColors.Surface,
    onBackground = AyhamColors.TextPrimary,
    onSurface = AyhamColors.TextPrimary,
)

@Composable
fun AyhamTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = scheme, content = content)
}
