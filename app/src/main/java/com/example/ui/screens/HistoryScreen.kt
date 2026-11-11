package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ChatMessage
import com.example.ui.components.GlassCard
import com.example.ui.components.ToolCallCard
import com.example.ui.theme.DarkNavy
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.GlassCardBorder
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    messages: List<ChatMessage>,
    onReplaySpeech: (String) -> Unit,
    onClearHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var filterType by remember { mutableStateOf("ALL") }

    val filteredMessages = when (filterType) {
        "TOOLS" -> messages.filter { it.toolCall != null }
        "VOICE" -> messages.filter { it.toolCall == null }
        else -> messages
    }

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
                        text = "SESSION INTERACTION LOGS",
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Conversation & Tools History",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                if (messages.isNotEmpty()) {
                    IconButton(
                        onClick = onClearHistory,
                        modifier = Modifier.testTag("clear_history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Clear History",
                            tint = Color.Red.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Filter Tabs
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                listOf("ALL", "VOICE", "TOOLS").forEach { filter ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (filterType == filter) NeonCyan.copy(alpha = 0.2f) else Color(0xFF141C30))
                            .border(
                                1.dp,
                                if (filterType == filter) NeonCyan else GlassCardBorder,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { filterType = filter }
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = filter,
                            color = if (filterType == filter) NeonCyan else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (filteredMessages.isEmpty()) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "NO ENTRIES RECORDED",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Start speaking with Lodi Bhai to log interactions.",
                            color = TextSecondary.copy(alpha = 0.6f),
                            fontSize = 11.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(filteredMessages.reversed(), key = { it.id }) { msg ->
                        MessageItemCard(
                            message = msg,
                            onReplay = { onReplaySpeech(msg.text) },
                            onCopy = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("MPro AI", msg.text)
                                clipboard.setPrimaryClip(clip)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MessageItemCard(
    message: ChatMessage,
    onReplay: () -> Unit,
    onCopy: () -> Unit
) {
    val isUser = message.sender == "user"
    val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
    val timeString = remember(message.timestamp) { timeFormat.format(Date(message.timestamp)) }

    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (isUser) NeonCyan.copy(alpha = 0.25f) else ElectricPurple.copy(alpha = 0.25f),
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
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(if (isUser) NeonCyan.copy(alpha = 0.15f) else ElectricPurple.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            imageVector = if (isUser) Icons.Filled.Person else Icons.Filled.SmartToy,
                            contentDescription = null,
                            tint = if (isUser) NeonCyan else ElectricPurple,
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = if (isUser) "CHANDAN KUMAR" else "LODI BHAI (MPro AI)",
                        color = if (isUser) NeonCyan else ElectricPurple,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = timeString,
                        color = TextSecondary,
                        fontSize = 10.sp
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = onCopy,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ContentCopy,
                            contentDescription = "Copy",
                            tint = TextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                    }

                    if (!isUser) {
                        IconButton(
                            onClick = onReplay,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.VolumeUp,
                                contentDescription = "Replay",
                                tint = NeonCyan,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = message.text,
                color = TextPrimary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            if (message.toolCall != null) {
                Spacer(modifier = Modifier.height(10.dp))
                ToolCallCard(toolCall = message.toolCall)
            }
        }
    }
}
