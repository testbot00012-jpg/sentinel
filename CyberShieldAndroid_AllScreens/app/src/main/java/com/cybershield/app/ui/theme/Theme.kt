package com.cybershield.app.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkScheme = darkColorScheme(
    primary = Color(0xFF31D7FF),
    secondary = Color(0xFFA78BFA),
    tertiary = Color(0xFF34D399),
    background = Color(0xFF07090D),
    surface = Color(0xFF10141B),
    surfaceVariant = Color(0xFF171D27),
    onBackground = Color(0xFFF7F9FC),
    onSurface = Color(0xFFF7F9FC),
    onSurfaceVariant = Color(0xFF9AA5B5),
    error = Color(0xFFFF5C72)
)

@Composable
fun CyberShieldTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = DarkScheme, content = content)
}
