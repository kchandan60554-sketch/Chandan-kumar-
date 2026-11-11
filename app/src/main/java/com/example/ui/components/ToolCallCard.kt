package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ToolCall
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.StatusSuccess
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ToolCallCard(
    toolCall: ToolCall,
    modifier: Modifier = Modifier
) {
    val (icon, iconTint, toolDisplayName) = when (toolCall.name.lowercase()) {
        "openwebsite" -> Triple(Icons.Filled.Language, NeonCyan, "Open Website")
        "searchweb" -> Triple(Icons.Filled.Search, NeonCyan, "Web Search")
        "openyoutube" -> Triple(Icons.Filled.PlayArrow, Color(0xFFFF0033), "YouTube Direct")
        "launchapp" -> Triple(Icons.Filled.Apps, ElectricPurple, "Launch App")
        "generateidea" -> Triple(Icons.Filled.Lightbulb, Color(0xFFFFB703), "Visionary Idea Engine")
        else -> Triple(Icons.Filled.CheckCircle, NeonCyan, toolCall.name)
    }

    GlassCard(
        modifier = modifier.fillMaxWidth(),
        borderColor = iconTint.copy(alpha = 0.4f),
        cornerRadius = 14.dp
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
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(iconTint.copy(alpha = 0.15f))
                            .border(1.dp, iconTint.copy(alpha = 0.5f), CircleShape)
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = toolDisplayName,
                            tint = iconTint,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = toolDisplayName.uppercase(),
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "MPro Neural Tool Protocol",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }

                // Success badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (toolCall.isSuccess) StatusSuccess.copy(alpha = 0.15f) else Color.Red.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = if (toolCall.isSuccess) Icons.Filled.CheckCircle else Icons.Filled.Error,
                        contentDescription = if (toolCall.isSuccess) "Success" else "Error",
                        tint = if (toolCall.isSuccess) StatusSuccess else Color.Red,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (toolCall.isSuccess) "EXECUTED" else "FAILED",
                        color = if (toolCall.isSuccess) StatusSuccess else Color.Red,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (toolCall.arguments.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    toolCall.arguments.forEach { (key, value) ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1E293B))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "$key: $value",
                                color = NeonCyan,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            if (!toolCall.result.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = toolCall.result,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}
