package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MProDarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = DeepObsidian,
    primaryContainer = SurfaceDark,
    onPrimaryContainer = NeonCyan,
    secondary = ElectricPurple,
    onSecondary = Color.White,
    secondaryContainer = GlassCard,
    onSecondaryContainer = ElectricPurple,
    tertiary = QuantumViolet,
    onTertiary = Color.White,
    background = DeepObsidian,
    onBackground = TextPrimary,
    surface = DarkNavy,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceDark,
    onSurfaceVariant = TextSecondary,
    outline = GlassCardBorder
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = MProDarkColorScheme,
        typography = Typography,
        content = content
    )
}
