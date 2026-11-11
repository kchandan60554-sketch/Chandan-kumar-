package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AssistantState
import com.example.ui.components.GlassCard
import com.example.ui.components.MicrophoneButton
import com.example.ui.components.StatusIndicator
import com.example.ui.components.ToolCallCard
import com.example.ui.components.VoiceVisualizer
import com.example.ui.theme.DarkNavy
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.GlassCardBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.AssistantUiState

@Composable
fun VoiceCoreScreen(
    uiState: AssistantUiState,
    onMicTapped: () -> Unit,
    onQuickPrompt: (String) -> Unit,
    onInterrupt: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        DeepObsidian,
                        DarkNavy,
                        DeepObsidian
                    )
                )
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Header: Logo, Identity & Status
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(NeonCyan.copy(alpha = 0.25f), ElectricPurple.copy(alpha = 0.35f))
                                )
                            )
                            .border(1.5.dp, NeonCyan, CircleShape)
                    ) {
                        Text(
                            text = "M",
                            color = NeonCyan,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "MPro AI",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(ElectricPurple.copy(alpha = 0.25f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "LODI BHAI",
                                    color = ElectricPurple,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(
                            text = "Owner: Chandan Kumar",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                StatusIndicator(state = uiState.state)
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Visionary Banner Pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF131B2E).copy(alpha = 0.8f))
                    .border(1.dp, GlassCardBorder.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.ElectricBolt,
                        contentDescription = "Vision",
                        tint = NeonCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Inspired by Sir Ratan Tata & Dr. APJ Abdul Kalam",
                        color = NeonCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dynamic Voice Waveform Visualizer
            VoiceVisualizer(
                assistantState = uiState.state,
                audioAmplitude = uiState.audioAmplitude,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Live Speech Transcript & Subtitles Glass Panel
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = if (uiState.state == AssistantState.SPEAKING) ElectricPurple.copy(alpha = 0.5f) else NeonCyan.copy(alpha = 0.3f),
                cornerRadius = 16.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = when (uiState.state) {
                                AssistantState.LISTENING -> "LIVE INPUT [CHANDAN KUMAR]"
                                AssistantState.SPEAKING -> "VOICE STREAM [LODI BHAI]"
                                AssistantState.THINKING -> "NEURAL PROCESSING..."
                                AssistantState.INTERRUPTED -> "STATUS: INTERRUPTED"
                                else -> "VOICE INTERACTION HUD"
                            },
                            color = when (uiState.state) {
                                AssistantState.SPEAKING -> ElectricPurple
                                AssistantState.LISTENING -> NeonCyan
                                else -> TextSecondary
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        if (uiState.state == AssistantState.SPEAKING) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(ElectricPurple.copy(alpha = 0.2f))
                                    .clickable { onInterrupt() }
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.VolumeUp,
                                    contentDescription = "Tap to interrupt",
                                    tint = ElectricPurple,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "TAP MIC TO INTERRUPT",
                                    color = ElectricPurple,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val displayText = if (uiState.currentSubtitles.isNotBlank()) {
                        uiState.currentSubtitles
                    } else {
                        uiState.messages.lastOrNull()?.text ?: "Lodi Bhai is ready. Tap the microphone or say 'Hey Lodi Bhai' to begin."
                    }

                    Text(
                        text = displayText,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            // Latest Tool Execution Result (if any)
            if (uiState.latestToolCall != null) {
                Spacer(modifier = Modifier.height(14.dp))
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically()
                ) {
                    ToolCallCard(toolCall = uiState.latestToolCall)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Central Glowing Holographic Microphone Button
            MicrophoneButton(
                assistantState = uiState.state,
                amplitude = uiState.audioAmplitude,
                onClick = onMicTapped
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = when (uiState.state) {
                    AssistantState.LISTENING -> "LISTENING TO CHANDAN KUMAR..."
                    AssistantState.SPEAKING -> "TAP TO INTERRUPT"
                    AssistantState.THINKING -> "SYNAPSE COMPUTING..."
                    AssistantState.INTERRUPTED -> "INTERRUPTED — READY"
                    else -> "TAP MIC TO SPEAK"
                },
                color = when (uiState.state) {
                    AssistantState.LISTENING -> NeonCyan
                    AssistantState.SPEAKING -> ElectricPurple
                    else -> TextSecondary
                },
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Action Suggestion Chips
            Text(
                text = "VOICE QUICK ACTIONS",
                color = TextSecondary.copy(alpha = 0.7f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start
            )

            Spacer(modifier = Modifier.height(8.dp))

            val quickActions = listOf(
                "Hey Lodi Bhai" to Icons.Filled.ElectricBolt,
                "Generate Visionary Idea" to Icons.Filled.Lightbulb,
                "YouTube Innovation" to Icons.Filled.PlayArrow,
                "Search AI Technology" to Icons.Filled.Search
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(quickActions) { (label, icon) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF162036))
                            .border(1.dp, NeonCyan.copy(alpha = 0.25f), RoundedCornerShape(20.dp))
                            .clickable { onQuickPrompt(label) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                            .testTag("quick_action_${label.lowercase().replace(" ", "_")}")
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = label,
                            tint = NeonCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = label,
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
