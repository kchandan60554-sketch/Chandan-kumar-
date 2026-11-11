package com.example.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.model.AssistantMetrics
import com.example.model.AssistantState
import com.example.model.ChatMessage
import com.example.model.GeneratedIdea
import com.example.model.SystemStatus
import com.example.model.ToolCall
import com.example.service.MProAiService
import com.example.service.ToolHandler
import com.example.service.VoiceEngine
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

private const val TAG = "AssistantViewModel"

data class AssistantUiState(
    val state: AssistantState = AssistantState.IDLE,
    val messages: List<ChatMessage> = emptyList(),
    val currentSubtitles: String = "",
    val latestToolCall: ToolCall? = null,
    val audioAmplitude: Float = 0f,
    val metrics: AssistantMetrics = AssistantMetrics(),
    val systemStatus: SystemStatus = SystemStatus(),
    val generatedIdeas: List<GeneratedIdea> = emptyList(),
    val isBooted: Boolean = false,
    val bootStep: String = "",
    val errorMessage: String? = null,
    val activeTab: Int = 0 // 0: Voice HUD, 1: Dashboard, 2: History, 3: Settings
)

class AssistantViewModel(application: Application) : AndroidViewModel(application) {

    private val aiService = MProAiService()
    private val toolHandler = ToolHandler(application)

    private val _uiState = MutableStateFlow(AssistantUiState())
    val uiState: StateFlow<AssistantUiState> = _uiState.asStateFlow()

    private var voiceEngine: VoiceEngine? = null

    init {
        initVoiceEngine()
        runBootSequence()
    }

    private fun initVoiceEngine() {
        voiceEngine = VoiceEngine(
            context = getApplication(),
            onSpeechRecognized = { text ->
                handleUserSpeech(text)
            },
            onWakeWordDetected = {
                onWakeWordTriggered()
            },
            onSpeakingStarted = {
                _uiState.value = _uiState.value.copy(state = AssistantState.SPEAKING)
            },
            onSpeakingFinished = {
                if (_uiState.value.state == AssistantState.SPEAKING) {
                    _uiState.value = _uiState.value.copy(
                        state = AssistantState.IDLE,
                        currentSubtitles = ""
                    )
                }
            },
            onError = { errMsg ->
                _uiState.value = _uiState.value.copy(errorMessage = errMsg)
            }
        )

        // Observe amplitude from voice engine
        viewModelScope.launch {
            voiceEngine?.audioAmplitude?.collect { amp ->
                _uiState.value = _uiState.value.copy(audioAmplitude = amp)
            }
        }
    }

    private fun runBootSequence() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(state = AssistantState.CONNECTING, isBooted = false)
            val steps = listOf(
                "Initializing MPro AI Neural Core...",
                "Syncing quantum frequency with Chandan Kumar...",
                "Calibrating Sir Ratan Tata & Kalam visionary engine...",
                "Lodi Bhai online! Ready to empower."
            )
            for (step in steps) {
                _uiState.value = _uiState.value.copy(bootStep = step)
                delay(600)
            }
            _uiState.value = _uiState.value.copy(
                isBooted = true,
                state = AssistantState.IDLE,
                messages = listOf(
                    ChatMessage(
                        sender = "assistant",
                        text = "Namaste Chandan Kumar Boss! Lodi Bhai is fully operational. How are we innovating today?"
                    )
                )
            )
            // Welcome audio
            speakText("Namaste Chandan Kumar Boss! Lodi Bhai is fully operational. How are we innovating today?")
        }
    }

    fun onMicTapped() {
        when (_uiState.value.state) {
            AssistantState.LISTENING -> {
                voiceEngine?.stopListening()
                _uiState.value = _uiState.value.copy(state = AssistantState.IDLE)
            }
            AssistantState.SPEAKING -> {
                // Instant interruption handling per prompt
                interruptSpeech()
            }
            AssistantState.THINKING -> {
                // Wait or cancel
            }
            else -> {
                startListening()
            }
        }
    }

    fun startListening() {
        _uiState.value = _uiState.value.copy(
            state = AssistantState.LISTENING,
            errorMessage = null,
            currentSubtitles = "Listening to you, Boss..."
        )
        voiceEngine?.startListening()
    }

    fun interruptSpeech() {
        voiceEngine?.playInterruptionSound()
        voiceEngine?.stopSpeaking()
        _uiState.value = _uiState.value.copy(
            state = AssistantState.INTERRUPTED,
            currentSubtitles = "[Interrupted by Boss]"
        )
        viewModelScope.launch {
            delay(1000)
            if (_uiState.value.state == AssistantState.INTERRUPTED) {
                _uiState.value = _uiState.value.copy(state = AssistantState.IDLE, currentSubtitles = "")
            }
        }
    }

    private fun onWakeWordTriggered() {
        voiceEngine?.playActivationSound()
        _uiState.value = _uiState.value.copy(
            currentSubtitles = "Hey Lodi Bhai wake word detected!"
        )
    }

    fun handleUserSpeech(userText: String) {
        if (userText.isBlank()) return

        val userMsg = ChatMessage(sender = "user", text = userText)
        val currentMessages = _uiState.value.messages + userMsg
        val updatedMetrics = _uiState.value.metrics.copy(
            conversationsCount = _uiState.value.metrics.conversationsCount + 1
        )

        _uiState.value = _uiState.value.copy(
            messages = currentMessages,
            metrics = updatedMetrics,
            state = AssistantState.THINKING,
            currentSubtitles = "Thinking..."
        )

        viewModelScope.launch {
            processAiInput(userText, currentMessages)
        }
    }

    private suspend fun processAiInput(userInput: String, currentMessages: List<ChatMessage>) {
        val history = currentMessages.takeLast(6).map { it.sender to it.text }
        val result = aiService.generateResponse(userInput, history)

        result.onSuccess { rawResponse ->
            parseAndExecuteAiResponse(rawResponse)
        }.onFailure { error ->
            Log.w(TAG, "Gemini call failed, using resilient neural fallback", error)
            handleFallbackResponse(userInput, error)
        }
    }

    private fun parseAndExecuteAiResponse(rawResponse: String) {
        // Regex to extract [TOOL:toolName:{"key":"val"}]
        val toolRegex = Regex("\\[TOOL:(\\w+):(\\{.*?\\})\\]")
        val match = toolRegex.find(rawResponse)

        var cleanText = rawResponse.replace(toolRegex, "").trim()
        var toolCall: ToolCall? = null

        if (match != null) {
            val toolName = match.groupValues[1]
            val jsonArgString = match.groupValues[2]
            val argsMap = mutableMapOf<String, String>()
            try {
                val json = JSONObject(jsonArgString)
                val keys = json.keys()
                while (keys.hasNext()) {
                    val k = keys.next()
                    argsMap[k] = json.optString(k)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing tool arguments", e)
            }

            // Execute the tool
            val executedCall = toolHandler.executeTool(toolName, argsMap)
            toolCall = executedCall

            val updatedMetrics = _uiState.value.metrics.copy(
                toolsExecutedCount = _uiState.value.metrics.toolsExecutedCount + 1
            )
            _uiState.value = _uiState.value.copy(
                metrics = updatedMetrics,
                latestToolCall = executedCall
            )

            // If cleanText is empty, provide a natural response
            if (cleanText.isBlank()) {
                cleanText = executedCall.result ?: "Executing $toolName for you, Boss."
            }
        }

        val assistantMsg = ChatMessage(
            sender = "assistant",
            text = cleanText,
            toolCall = toolCall
        )

        _uiState.value = _uiState.value.copy(
            state = AssistantState.SPEAKING,
            messages = _uiState.value.messages + assistantMsg,
            currentSubtitles = cleanText
        )

        speakText(cleanText)
    }

    private fun handleFallbackResponse(userInput: String, error: Throwable) {
        val lower = userInput.lowercase()
        val (fallbackText, toolCall) = when {
            lower.contains("youtube") -> {
                val query = lower.replace("open", "").replace("youtube", "").replace("search", "").trim()
                val call = toolHandler.executeTool("openYouTube", mapOf("query" to query))
                "On it, Boss! Launching YouTube for '$query'." to call
            }
            lower.contains("google") || lower.contains("search") -> {
                val query = lower.replace("search for", "").replace("google", "").replace("search", "").trim()
                val call = toolHandler.executeTool("searchWeb", mapOf("query" to query))
                "Executing web search for '$query', Chandan Kumar Boss." to call
            }
            lower.contains("open") || lower.contains("website") || lower.contains("launch") -> {
                val appOrSite = lower.replace("open", "").replace("launch", "").trim()
                val call = toolHandler.executeTool("launchApp", mapOf("appName" to appOrSite))
                "Accessing $appOrSite right away, Boss." to call
            }
            lower.contains("idea") || lower.contains("project") || lower.contains("innovate") -> {
                val domain = if (lower.contains("tech")) "Artificial Intelligence & Edge Tech" else "High Impact Innovation"
                val idea = toolHandler.createVisionaryIdea(domain)
                val newIdeas = _uiState.value.generatedIdeas + idea
                _uiState.value = _uiState.value.copy(
                    generatedIdeas = newIdeas,
                    metrics = _uiState.value.metrics.copy(
                        ideasGeneratedCount = _uiState.value.metrics.ideasGeneratedCount + 1
                    )
                )
                val call = ToolCall(
                    name = "generateIdea",
                    arguments = mapOf("domain" to domain),
                    result = "Project: ${idea.title}\nPrinciples: ${idea.inspirationalPillars}",
                    isSuccess = true
                )
                "Chandan Kumar Boss, here is an innovation inspired by Sir Ratan Tata & Dr. APJ Abdul Kalam: '${idea.title}'. Built for sustainable national and global impact!" to call
            }
            else -> {
                "Always with you, Boss Chandan Kumar! Lodi Bhai is fully calibrated with Sir Ratan Tata's ethics and Dr. Kalam's relentless drive. How can we move forward?" to null
            }
        }

        val assistantMsg = ChatMessage(
            sender = "assistant",
            text = fallbackText,
            toolCall = toolCall
        )

        val updatedMetrics = _uiState.value.metrics.copy(
            toolsExecutedCount = if (toolCall != null) _uiState.value.metrics.toolsExecutedCount + 1 else _uiState.value.metrics.toolsExecutedCount
        )

        _uiState.value = _uiState.value.copy(
            state = AssistantState.SPEAKING,
            messages = _uiState.value.messages + assistantMsg,
            metrics = updatedMetrics,
            latestToolCall = toolCall,
            currentSubtitles = fallbackText
        )

        speakText(fallbackText)
    }

    fun speakText(text: String) {
        voiceEngine?.speak(text)
    }

    fun executeQuickTool(toolName: String, args: Map<String, String>) {
        val call = toolHandler.executeTool(toolName, args)
        val assistantMsg = ChatMessage(
            sender = "assistant",
            text = call.result ?: "Executed $toolName for Chandan Kumar.",
            toolCall = call
        )
        val updatedMetrics = _uiState.value.metrics.copy(
            toolsExecutedCount = _uiState.value.metrics.toolsExecutedCount + 1
        )
        _uiState.value = _uiState.value.copy(
            messages = _uiState.value.messages + assistantMsg,
            latestToolCall = call,
            metrics = updatedMetrics
        )
        speakText(call.result ?: "Executed successfully, Boss.")
    }

    fun generateNewVisionaryIdea(domain: String) {
        val idea = toolHandler.createVisionaryIdea(domain)
        val newIdeas = _uiState.value.generatedIdeas + idea
        val call = ToolCall(
            name = "generateIdea",
            arguments = mapOf("domain" to domain),
            result = "${idea.title}: ${idea.coreVision}",
            isSuccess = true
        )
        val assistantMsg = ChatMessage(
            sender = "assistant",
            text = "Boss Chandan Kumar, generated a new visionary blueprint: '${idea.title}' under ${idea.domain}.",
            toolCall = call
        )
        _uiState.value = _uiState.value.copy(
            generatedIdeas = newIdeas,
            messages = _uiState.value.messages + assistantMsg,
            latestToolCall = call,
            metrics = _uiState.value.metrics.copy(
                ideasGeneratedCount = _uiState.value.metrics.ideasGeneratedCount + 1
            )
        )
        speakText("Boss, generated a new visionary blueprint: ${idea.title}.")
    }

    fun setActiveTab(index: Int) {
        _uiState.value = _uiState.value.copy(activeTab = index)
    }

    fun clearHistory() {
        _uiState.value = _uiState.value.copy(
            messages = emptyList(),
            currentSubtitles = ""
        )
    }

    fun setSpeechRate(rate: Float) {
        voiceEngine?.speechRate = rate
    }

    fun setSpeechPitch(pitch: Float) {
        voiceEngine?.speechPitch = pitch
    }

    fun toggleWakeWord(enabled: Boolean) {
        voiceEngine?.wakeWordEnabled = enabled
    }

    override fun onCleared() {
        super.onCleared()
        voiceEngine?.release()
    }
}
