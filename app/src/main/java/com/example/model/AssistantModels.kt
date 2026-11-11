package com.example.model

enum class AssistantState(val label: String) {
    IDLE("Ready (Idle)"),
    CONNECTING("Syncing Neural Core..."),
    LISTENING("Listening to Boss..."),
    THINKING("Thinking / Processing..."),
    SPEAKING("Speaking (Lodi Bhai)"),
    INTERRUPTED("Interrupted"),
    DISCONNECTED("Neural Link Offline")
}

data class ToolCall(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val arguments: Map<String, String>,
    val result: String? = null,
    val isSuccess: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: String, // "user", "assistant", "system"
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val toolCall: ToolCall? = null
)

data class GeneratedIdea(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val domain: String,
    val coreVision: String,
    val inspirationalPillars: String, // Tata & Kalam principles
    val actionSteps: List<String>,
    val timestamp: Long = System.currentTimeMillis()
)

data class AssistantMetrics(
    val conversationsCount: Int = 0,
    val toolsExecutedCount: Int = 0,
    val ideasGeneratedCount: Int = 0,
    val sessionStartTime: Long = System.currentTimeMillis()
) {
    val uptimeMinutes: Long
        get() = ((System.currentTimeMillis() - sessionStartTime) / 60000).coerceAtLeast(1)
}

data class SystemStatus(
    val neuralEngineStatus: String = "Online (Gemini 3.8 Flash)",
    val latencyMs: Int = 42,
    val memoryUsageMb: Int = 128,
    val activeSessionId: String = "MPRO-LIVE-${System.currentTimeMillis() % 100000}",
    val ownerName: String = "CHANDAN KUMAR",
    val ownerRole: String = "Owner, Creator and Administrator",
    val assistantAlias: String = "Lodi Bhai"
)
