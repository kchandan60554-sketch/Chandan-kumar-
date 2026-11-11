package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.theme.DarkNavy
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.QuantumViolet
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.AssistantUiState

@Composable
fun SettingsScreen(
    uiState: AssistantUiState,
    onSpeechRateChanged: (Float) -> Unit,
    onSpeechPitchChanged: (Float) -> Unit,
    onWakeWordToggled: (Boolean) -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    var speechRate by remember { mutableFloatStateOf(1.05f) }
    var speechPitch by remember { mutableFloatStateOf(1.0f) }
    var wakeWordEnabled by remember { mutableStateOf(true) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DeepObsidian, DarkNavy, DeepObsidian)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(20.dp)
        ) {
            Text(
                text = "NEURAL PREFERENCES & SECURITY",
                color = NeonCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = "Assistant Settings & Privacy",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Voice Tuning
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = ElectricPurple.copy(alpha = 0.35f),
                cornerRadius = 16.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.RecordVoiceOver, contentDescription = null, tint = ElectricPurple, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Voice Acoustics & Synthesizer", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Speech Speed Slider
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Speech Rate", color = TextSecondary, fontSize = 12.sp)
                        Text(String.format("%.2fx", speechRate), color = ElectricPurple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = speechRate,
                        onValueChange = {
                            speechRate = it
                            onSpeechRateChanged(it)
                        },
                        valueRange = 0.75f..1.4f,
                        colors = SliderDefaults.colors(
                            thumbColor = ElectricPurple,
                            activeTrackColor = ElectricPurple,
                            inactiveTrackColor = Color(0xFF1E293B)
                        ),
                        modifier = Modifier.testTag("speech_rate_slider")
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Speech Pitch Slider
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Voice Pitch", color = TextSecondary, fontSize = 12.sp)
                        Text(String.format("%.2fx", speechPitch), color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = speechPitch,
                        onValueChange = {
                            speechPitch = it
                            onSpeechPitchChanged(it)
                        },
                        valueRange = 0.8f..1.3f,
                        colors = SliderDefaults.colors(
                            thumbColor = NeonCyan,
                            activeTrackColor = NeonCyan,
                            inactiveTrackColor = Color(0xFF1E293B)
                        ),
                        modifier = Modifier.testTag("speech_pitch_slider")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Wake Word Setting
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = NeonCyan.copy(alpha = 0.35f),
                cornerRadius = 16.dp
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Mic, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Wake Word: 'Hey Lodi Bhai'", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Triggers voice interaction when speech matches phrase.",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Switch(
                        checked = wakeWordEnabled,
                        onCheckedChange = {
                            wakeWordEnabled = it
                            onWakeWordToggled(it)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = DeepObsidian,
                            checkedTrackColor = NeonCyan,
                            uncheckedThumbColor = TextSecondary,
                            uncheckedTrackColor = Color(0xFF1E293B)
                        ),
                        modifier = Modifier.testTag("wake_word_switch")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Security & Privacy Controls
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = StatusSuccess.copy(alpha = 0.35f),
                cornerRadius = 16.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Security, contentDescription = null, tint = StatusSuccess, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Zero-Trust Security & App Check", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    SecurityItemRow(
                        title = "App Check Attestation",
                        status = "Active & Verified",
                        isOk = true
                    )
                    SecurityItemRow(
                        title = "Backend Model",
                        status = "Gemini 3.8 Flash (Firebase AI)",
                        isOk = true
                    )
                    SecurityItemRow(
                        title = "Client-Side Secrets",
                        status = "Zero embedded API keys (Protected)",
                        isOk = true
                    )
                    SecurityItemRow(
                        title = "Owner Access Level",
                        status = "Administrator (Chandan Kumar)",
                        isOk = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Session Data Wipe
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = Color.Red.copy(alpha = 0.35f),
                cornerRadius = 16.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Delete, contentDescription = null, tint = Color.Red, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Session Privacy Controls", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Wipe active session memory and interaction logs from device RAM.",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onClearHistory,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7F1D1D)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("wipe_session_button")
                    ) {
                        Text("WIPE ACTIVE SESSION MEMORY", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun SecurityItemRow(
    title: String,
    status: String,
    isOk: Boolean
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
    ) {
        Text(text = title, color = TextSecondary, fontSize = 12.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = if (isOk) StatusSuccess else Color.Red,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = status,
                color = if (isOk) StatusSuccess else Color.Red,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
