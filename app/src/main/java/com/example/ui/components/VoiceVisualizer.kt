package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.model.AssistantState
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.QuantumViolet
import com.example.ui.theme.StatusInterrupted
import kotlin.math.PI
import kotlin.math.sin

@Composable
fun VoiceVisualizer(
    assistantState: AssistantState,
    audioAmplitude: Float,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "visualizer_anim")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val secondaryPhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2f * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "sec_phase"
    )

    // Base height multiplier based on state and amplitude
    val targetMultiplier = when (assistantState) {
        AssistantState.LISTENING -> (0.35f + audioAmplitude * 0.9f).coerceIn(0.2f, 1.2f)
        AssistantState.SPEAKING -> (0.45f + audioAmplitude * 0.95f).coerceIn(0.3f, 1.3f)
        AssistantState.THINKING -> 0.4f
        AssistantState.INTERRUPTED -> 0.2f
        AssistantState.IDLE -> 0.15f
        AssistantState.CONNECTING -> 0.3f
        AssistantState.DISCONNECTED -> 0.05f
    }

    val primaryColor = when (assistantState) {
        AssistantState.LISTENING -> NeonCyan
        AssistantState.SPEAKING -> ElectricPurple
        AssistantState.THINKING -> QuantumViolet
        AssistantState.INTERRUPTED -> StatusInterrupted
        AssistantState.IDLE -> NeonCyan.copy(alpha = 0.7f)
        else -> NeonCyan.copy(alpha = 0.4f)
    }

    val secondaryColor = when (assistantState) {
        AssistantState.LISTENING -> NeonPurple
        AssistantState.SPEAKING -> NeonCyan
        AssistantState.THINKING -> NeonCyan
        else -> ElectricPurple.copy(alpha = 0.5f)
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(110.dp)
    ) {
        val width = size.width
        val height = size.height
        val centerY = height / 2f
        val maxWaveHeight = height * 0.42f * targetMultiplier

        // 1. Draw glowing background horizontal baseline
        drawLine(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    primaryColor.copy(alpha = 0.3f),
                    secondaryColor.copy(alpha = 0.5f),
                    primaryColor.copy(alpha = 0.3f),
                    Color.Transparent
                )
            ),
            start = Offset(0f, centerY),
            end = Offset(width, centerY),
            strokeWidth = 2.dp.toPx()
        )

        // 2. Primary Sine Wave
        val path1 = Path()
        val path2 = Path()
        val points = 60
        val step = width / points

        for (i in 0..points) {
            val x = i * step
            val normalizedX = (x / width) * 2f * PI.toFloat()
            // Windowing envelope to taper edges (Hann window)
            val envelope = (0.5f * (1f - kotlin.math.cos(2f * PI.toFloat() * (x / width))))

            val y1 = centerY + sin(normalizedX * 2.2f + phase) * maxWaveHeight * envelope
            val y2 = centerY + sin(normalizedX * 3.5f - secondaryPhase) * (maxWaveHeight * 0.75f) * envelope

            if (i == 0) {
                path1.moveTo(x, y1)
                path2.moveTo(x, y2)
            } else {
                path1.lineTo(x, y1)
                path2.lineTo(x, y2)
            }
        }

        // Draw secondary wave (thin, electric)
        drawPath(
            path = path2,
            color = secondaryColor.copy(alpha = 0.65f),
            style = Stroke(width = 2.dp.toPx())
        )

        // Draw primary wave (thick, glowing)
        drawPath(
            path = path1,
            brush = Brush.horizontalGradient(
                colors = listOf(
                    secondaryColor.copy(alpha = 0.2f),
                    primaryColor,
                    secondaryColor,
                    primaryColor,
                    secondaryColor.copy(alpha = 0.2f)
                )
            ),
            style = Stroke(width = 3.5.dp.toPx())
        )

        // Ambient particles along the waveform in thinking / speaking mode
        if (assistantState == AssistantState.SPEAKING || assistantState == AssistantState.THINKING || assistantState == AssistantState.LISTENING) {
            val particleCount = 7
            for (p in 1..particleCount) {
                val px = (width / (particleCount + 1)) * p
                val normalizedX = (px / width) * 2f * PI.toFloat()
                val envelope = (0.5f * (1f - kotlin.math.cos(2f * PI.toFloat() * (px / width))))
                val py = centerY + sin(normalizedX * 2.2f + phase) * maxWaveHeight * envelope
                drawCircle(
                    color = primaryColor,
                    radius = (2.5f + (p % 3) * 1.5f).dp.toPx(),
                    center = Offset(px, py)
                )
            }
        }
    }
}
