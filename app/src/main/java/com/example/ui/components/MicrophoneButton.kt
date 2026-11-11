package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.AssistantState
import com.example.ui.theme.DarkNavy
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.QuantumViolet
import com.example.ui.theme.StatusInterrupted

@Composable
fun MicrophoneButton(
    assistantState: AssistantState,
    amplitude: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_scale"
    )

    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "pulse_alpha"
    )

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin_angle"
    )

    val dynamicScale = when (assistantState) {
        AssistantState.LISTENING -> 1f + (amplitude * 0.25f)
        AssistantState.SPEAKING -> 1.05f
        else -> 1f
    }

    val primaryColor = when (assistantState) {
        AssistantState.LISTENING -> NeonCyan
        AssistantState.SPEAKING -> ElectricPurple
        AssistantState.THINKING -> QuantumViolet
        AssistantState.INTERRUPTED -> StatusInterrupted
        AssistantState.IDLE -> NeonCyan
        else -> Color.Gray
    }

    val secondaryColor = when (assistantState) {
        AssistantState.LISTENING -> NeonPurple
        AssistantState.SPEAKING -> NeonCyan
        AssistantState.THINKING -> NeonCyan
        else -> ElectricPurple
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(130.dp)
    ) {
        // Outer pulsing concentric rings when active
        if (assistantState == AssistantState.LISTENING || assistantState == AssistantState.SPEAKING) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .scale(pulseScale)
                    .border(
                        width = 2.dp,
                        color = primaryColor.copy(alpha = pulseAlpha),
                        shape = CircleShape
                    )
            )
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .scale(pulseScale * 0.88f)
                    .border(
                        width = 1.5.dp,
                        color = secondaryColor.copy(alpha = pulseAlpha * 0.8f),
                        shape = CircleShape
                    )
            )
        }

        // Inner glowing ambient halo
        Box(
            modifier = Modifier
                .size(86.dp)
                .scale(dynamicScale)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            primaryColor.copy(alpha = 0.45f),
                            secondaryColor.copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Main holographic core button
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(76.dp)
                .scale(dynamicScale)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            DarkNavy,
                            Color(0xFF141C30)
                        )
                    )
                )
                .border(
                    width = 2.5.dp,
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            primaryColor,
                            secondaryColor,
                            primaryColor
                        )
                    ),
                    shape = CircleShape
                )
                .testTag("mic_button")
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = true, color = primaryColor),
                    onClick = onClick
                )
        ) {
            when (assistantState) {
                AssistantState.LISTENING -> {
                    Icon(
                        imageVector = Icons.Filled.Mic,
                        contentDescription = "Listening - Tap to finish or pause",
                        tint = NeonCyan,
                        modifier = Modifier.size(36.dp)
                    )
                }
                AssistantState.SPEAKING -> {
                    Icon(
                        imageVector = Icons.Filled.Stop,
                        contentDescription = "Speaking - Tap to interrupt Lodi Bhai",
                        tint = ElectricPurple,
                        modifier = Modifier.size(36.dp)
                    )
                }
                AssistantState.THINKING -> {
                    Icon(
                        imageVector = Icons.Filled.Sync,
                        contentDescription = "Thinking",
                        tint = QuantumViolet,
                        modifier = Modifier
                            .size(34.dp)
                            .rotate(rotation)
                    )
                }
                AssistantState.INTERRUPTED -> {
                    Icon(
                        imageVector = Icons.Filled.MicOff,
                        contentDescription = "Interrupted",
                        tint = StatusInterrupted,
                        modifier = Modifier.size(34.dp)
                    )
                }
                AssistantState.DISCONNECTED -> {
                    Icon(
                        imageVector = Icons.Filled.MicOff,
                        contentDescription = "Disconnected",
                        tint = Color.Gray,
                        modifier = Modifier.size(34.dp)
                    )
                }
                else -> {
                    Icon(
                        imageVector = Icons.Filled.Mic,
                        contentDescription = "Start Voice Assistant",
                        tint = NeonCyan,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
    }
}
