package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.GlassCard
import com.example.ui.theme.DarkNavy
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.GlassCardBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.QuantumViolet
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.AssistantUiState

@Composable
fun DashboardScreen(
    uiState: AssistantUiState,
    onExecuteTool: (toolName: String, args: Map<String, String>) -> Unit,
    onGenerateIdea: (domain: String) -> Unit,
    onSwitchTab: (tabIndex: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var searchQuery by remember { mutableStateOf("") }
    var youtubeQuery by remember { mutableStateOf("") }
    var selectedDomain by remember { mutableStateOf("Autonomous AI & Robotics") }

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
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "NEURAL COMMAND DASHBOARD",
                        color = NeonCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "MPro AI Operating System",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(StatusSuccess.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(StatusSuccess)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SYSTEM ACTIVE",
                            color = StatusSuccess,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Owner Identity Card (Chandan Kumar)
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = NeonCyan.copy(alpha = 0.4f),
                cornerRadius = 18.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(NeonCyan.copy(alpha = 0.3f), ElectricPurple.copy(alpha = 0.4f))
                                        )
                                    )
                                    .border(1.5.dp, NeonCyan, CircleShape)
                            ) {
                                Text(
                                    text = "CK",
                                    color = NeonCyan,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = uiState.systemStatus.ownerName,
                                    color = TextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = uiState.systemStatus.ownerRole,
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1E293B))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "ADMIN",
                                color = NeonCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text("Assistant Identity", color = TextSecondary, fontSize = 10.sp)
                            Text("MPro AI (Lodi Bhai)", color = ElectricPurple, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Philosophical Pillars", color = TextSecondary, fontSize = 10.sp)
                            Text("Tata & Kalam Vision", color = NeonCyan, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text("Session ID", color = TextSecondary, fontSize = 10.sp)
                            Text(uiState.systemStatus.activeSessionId, color = TextPrimary, fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Telemetry & Metrics Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                MetricTile(
                    title = "Conversations",
                    value = "${uiState.metrics.conversationsCount}",
                    subtitle = "Speech Exchanges",
                    icon = Icons.Filled.Mic,
                    tint = NeonCyan,
                    modifier = Modifier.weight(1f)
                )
                MetricTile(
                    title = "Tools Executed",
                    value = "${uiState.metrics.toolsExecutedCount}",
                    subtitle = "Automated Actions",
                    icon = Icons.Filled.AutoAwesome,
                    tint = ElectricPurple,
                    modifier = Modifier.weight(1f)
                )
                MetricTile(
                    title = "Ideas Created",
                    value = "${uiState.metrics.ideasGeneratedCount}",
                    subtitle = "Visionary Projects",
                    icon = Icons.Filled.Lightbulb,
                    tint = QuantumViolet,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Tool Launchers
            Text(
                text = "NEURAL TOOL CONTROLS",
                color = NeonCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Smart Web Search Box
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = NeonCyan.copy(alpha = 0.3f),
                cornerRadius = 14.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Search, contentDescription = "Search", tint = NeonCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Smart Web Search (Google)", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Query Google...", color = TextSecondary, fontSize = 12.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = GlassCardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("search_input")
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (searchQuery.isNotBlank()) {
                                    onExecuteTool("searchWeb", mapOf("query" to searchQuery))
                                    searchQuery = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan),
                            modifier = Modifier.testTag("search_submit_button")
                        ) {
                            Text("SEARCH", color = DeepObsidian, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // YouTube Search Box
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = Color(0xFFFF0033).copy(alpha = 0.35f),
                cornerRadius = 14.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.PlayArrow, contentDescription = "YouTube", tint = Color(0xFFFF0033), modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("YouTube Innovation Stream", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = youtubeQuery,
                            onValueChange = { youtubeQuery = it },
                            placeholder = { Text("e.g. Ratan Tata inspiring speech...", color = TextSecondary, fontSize = 12.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFFFF0033),
                                unfocusedBorderColor = GlassCardBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("youtube_input")
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (youtubeQuery.isNotBlank()) {
                                    onExecuteTool("openYouTube", mapOf("query" to youtubeQuery))
                                    youtubeQuery = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0033)),
                            modifier = Modifier.testTag("youtube_submit_button")
                        ) {
                            Text("PLAY", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Visionary Idea Generator (Tata & Kalam Model)
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = QuantumViolet.copy(alpha = 0.4f),
                cornerRadius = 14.dp
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Lightbulb, contentDescription = "Idea Generator", tint = QuantumViolet, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Tata & Kalam Idea Generator", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Generate strategic projects combining ethical integrity with high-scale technology.",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    val domains = listOf(
                        "Autonomous AI & Robotics",
                        "Clean Energy & Sustainability",
                        "Youth Education & Skill Pods",
                        "Quantum Diagnostic Healthcare"
                    )

                    domains.forEach { domain ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selectedDomain == domain) QuantumViolet.copy(alpha = 0.2f) else Color.Transparent)
                                .clickable { selectedDomain = domain }
                                .padding(horizontal = 8.dp, vertical = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (selectedDomain == domain) QuantumViolet else Color.Gray)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = domain,
                                color = if (selectedDomain == domain) TextPrimary else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = if (selectedDomain == domain) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { onGenerateIdea(selectedDomain) },
                        colors = ButtonDefaults.buttonColors(containerColor = QuantumViolet),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("generate_idea_button")
                    ) {
                        Icon(Icons.Filled.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("GENERATE VISIONARY BLUEPRINT", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Quick Navigation Hub
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                QuickNavButton(
                    icon = Icons.Filled.Mic,
                    label = "Voice HUD",
                    tint = NeonCyan,
                    onClick = { onSwitchTab(0) },
                    modifier = Modifier.weight(1f)
                )
                QuickNavButton(
                    icon = Icons.Filled.History,
                    label = "Session Log",
                    tint = ElectricPurple,
                    onClick = { onSwitchTab(2) },
                    modifier = Modifier.weight(1f)
                )
                QuickNavButton(
                    icon = Icons.Filled.Settings,
                    label = "Settings",
                    tint = QuantumViolet,
                    onClick = { onSwitchTab(3) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun MetricTile(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    tint: Color,
    modifier: Modifier = Modifier
) {
    GlassCard(
        modifier = modifier,
        borderColor = tint.copy(alpha = 0.35f),
        cornerRadius = 14.dp
    ) {
        Column {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = title,
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun QuickNavButton(
    icon: ImageVector,
    label: String,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF141C30))
            .border(1.dp, tint.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = icon, contentDescription = label, tint = tint, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = label, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}
