package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkNavy
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun BootAnimationOverlay(
    bootStep: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "boot_anim")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        DarkNavy,
                        DeepObsidian
                    )
                )
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Quantum Core Logo Box
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(120.dp)
                    .scale(pulseScale)
            ) {
                // Outer rotating ring
                Box(
                    modifier = Modifier
                        .size(118.dp)
                        .rotate(rotation)
                        .border(
                            width = 2.dp,
                            brush = Brush.sweepGradient(
                                colors = listOf(NeonCyan, ElectricPurple, Color.Transparent, NeonCyan)
                            ),
                            shape = CircleShape
                        )
                )

                // Inner core
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    NeonCyan.copy(alpha = 0.2f),
                                    ElectricPurple.copy(alpha = 0.3f)
                                )
                            )
                        )
                        .border(1.5.dp, NeonCyan, CircleShape)
                ) {
                    Text(
                        text = "M",
                        color = NeonCyan,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = "MPro AI",
                color = TextPrimary,
                fontSize = 26.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp
            )

            Text(
                text = "PERSONAL VOICE COMPANION",
                color = ElectricPurple,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "OWNER: CHANDAN KUMAR",
                color = NeonCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            CircularProgressIndicator(
                color = NeonCyan,
                trackColor = Color(0xFF1E293B),
                strokeWidth = 2.5.dp,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = bootStep.ifBlank { "Initializing Neural Core..." },
                color = TextSecondary,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
