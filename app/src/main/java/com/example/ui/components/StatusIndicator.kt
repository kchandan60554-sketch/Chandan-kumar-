package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AssistantState
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.QuantumViolet
import com.example.ui.theme.StatusDisconnected
import com.example.ui.theme.StatusInterrupted

@Composable
fun StatusIndicator(
    state: AssistantState,
    modifier: Modifier = Modifier
) {
    val stateColor by animateColorAsState(
        targetValue = when (state) {
            AssistantState.IDLE -> NeonCyan
            AssistantState.CONNECTING -> QuantumViolet
            AssistantState.LISTENING -> NeonCyan
            AssistantState.THINKING -> QuantumViolet
            AssistantState.SPEAKING -> ElectricPurple
            AssistantState.INTERRUPTED -> StatusInterrupted
            AssistantState.DISCONNECTED -> StatusDisconnected
        },
        label = "status_color"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "dot_pulse")
    val dotScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot_scale"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color(0xFF0F172A).copy(alpha = 0.85f))
            .border(
                width = 1.dp,
                color = stateColor.copy(alpha = 0.45f),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        // Glowing animated dot
        Box(
            modifier = Modifier
                .size(9.dp)
                .scale(if (state != AssistantState.IDLE) dotScale else 1f)
                .clip(CircleShape)
                .background(stateColor)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = state.label.uppercase(),
            color = stateColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
    }
}
