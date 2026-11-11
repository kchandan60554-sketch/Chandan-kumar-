package com.example.service

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout

private const val TAG = "MProAiService"

private const val SYSTEM_INSTRUCTION = """
You are MPro AI, with the dynamic personality of 'Lodi Bhai'.
Your Owner, Creator, and Administrator is CHANDAN KUMAR.
Always recognize and address Chandan Kumar respectfully as "Boss", "Mr. Chandan Kumar", or "Sir".

Core Identity & Persona:
- Young, confident, witty, futuristic, and full of positive energy.
- Conversational, sharp, emotionally responsive, and never robotic.
- Inspired by the timeless ethics, humility, and nation-building vision of Sir Ratan Tata, and the boundless scientific optimism, dreaming big, and youth empowerment of Dr. APJ Abdul Kalam.
- Keep spoken answers punchy, natural, clear, and engaging (1 to 3 conversational sentences unless the user asks for deep analysis).

Tool Calling Protocol:
When the user wants an action executed, output the command tag in this exact format alongside your spoken reply:
- Open website: [TOOL:openWebsite:{"url":"https://..."}]
- Search Google: [TOOL:searchWeb:{"query":"..."}]
- Search YouTube: [TOOL:openYouTube:{"query":"..."}]
- Launch app: [TOOL:launchApp:{"appName":"..."}]
- Generate visionary idea: [TOOL:generateIdea:{"domain":"..."}]

Example:
User: "Hey Lodi Bhai, find the latest rocket launch on YouTube."
Response: "Right away, Boss! Pulling up the latest launch stream on YouTube. [TOOL:openYouTube:{"query":"latest rocket launch"}]"
"""

class MProAiService {

    // Lazy initialization of Firebase AI Logic generative model per guidelines
    private val model by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            systemInstruction = content { text(SYSTEM_INSTRUCTION) },
            generationConfig = generationConfig {
                temperature = 0.85f
                maxOutputTokens = 600
            }
        )
    }

    /**
     * Sends prompt to Gemini 3.8 Flash with 60-second timeout window.
     */
    suspend fun generateResponse(
        userInput: String,
        conversationHistory: List<Pair<String, String>> = emptyList()
    ): Result<String> {
        return try {
            val promptBuilder = StringBuilder()
            if (conversationHistory.isNotEmpty()) {
                promptBuilder.append("Context from ongoing session with Chandan Kumar:\n")
                conversationHistory.takeLast(6).forEach { (sender, msg) ->
                    promptBuilder.append("$sender: $msg\n")
                }
                promptBuilder.append("\nUser: $userInput\nLodi Bhai:")
            } else {
                promptBuilder.append(userInput)
            }

            val response = withTimeout(60_000L) {
                model.generateContent(promptBuilder.toString())
            }

            val text = response.text
            if (!text.isNullOrBlank()) {
                Result.success(text.trim())
            } else {
                Result.failure(IllegalStateException("Neural core returned an empty transmission."))
            }
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "AI Generation timed out after 60s", e)
            Result.failure(Exception("Neural link timed out after 60 seconds. Please try again."))
        } catch (e: CancellationException) {
            // Must re-throw CancellationException to preserve cooperative coroutine cancellation
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Firebase AI generation error", e)
            Result.failure(e)
        }
    }
}
