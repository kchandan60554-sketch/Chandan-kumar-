package com.example.service

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

private const val TAG = "VoiceEngine"

class VoiceEngine(
    private val context: Context,
    private val onSpeechRecognized: (String) -> Unit,
    private val onWakeWordDetected: () -> Unit,
    private val onSpeakingStarted: () -> Unit,
    private val onSpeakingFinished: () -> Unit,
    private val onError: (String) -> Unit
) : TextToSpeech.OnInitListener {

    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null
    private var isTtsInitialized = false
    private var toneGenerator: ToneGenerator? = null

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    // Normalized RMS amplitude (0.0 to 1.0) for live waveform visualization
    private val _audioAmplitude = MutableStateFlow(0f)
    val audioAmplitude: StateFlow<Float> = _audioAmplitude.asStateFlow()

    var wakeWordEnabled: Boolean = true
    var speechRate: Float = 1.05f
    var speechPitch: Float = 1.0f

    init {
        try {
            textToSpeech = TextToSpeech(context.applicationContext, this)
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 60)
        } catch (e: Exception) {
            Log.e(TAG, "ToneGenerator or TTS init exception", e)
        }
        setupSpeechRecognizer()
    }

    private fun setupSpeechRecognizer() {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            Log.w(TAG, "Speech recognition is not available on this device.")
            return
        }

        try {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        Log.d(TAG, "SpeechRecognizer: onReadyForSpeech")
                    }

                    override fun onBeginningOfSpeech() {
                        Log.d(TAG, "SpeechRecognizer: onBeginningOfSpeech")
                    }

                    override fun onRmsChanged(rmsdB: Float) {
                        // Map rmsdB (-2 to 10 typical) to 0.0 - 1.0 range
                        val normalized = ((rmsdB + 2f) / 12f).coerceIn(0.05f, 1.0f)
                        _audioAmplitude.value = normalized
                    }

                    override fun onBufferReceived(buffer: ByteArray?) {}

                    override fun onEndOfSpeech() {
                        Log.d(TAG, "SpeechRecognizer: onEndOfSpeech")
                        _isListening.value = false
                        _audioAmplitude.value = 0f
                    }

                    override fun onError(error: Int) {
                        _isListening.value = false
                        _audioAmplitude.value = 0f
                        val message = when (error) {
                            SpeechRecognizer.ERROR_NO_MATCH -> "No speech detected. Say 'Hey Lodi Bhai' or ask a question."
                            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Speech timed out."
                            SpeechRecognizer.ERROR_AUDIO -> "Audio recording error."
                            SpeechRecognizer.ERROR_NETWORK -> "Network issue for speech recognition."
                            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission is required."
                            else -> "Recognition error: code $error"
                        }
                        Log.w(TAG, "SpeechRecognizer error: $message")
                        // Don't show toast for benign timeout or no-match
                        if (error != SpeechRecognizer.ERROR_NO_MATCH && error != SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                            onError(message)
                        }
                    }

                    override fun onResults(results: Bundle?) {
                        _isListening.value = false
                        _audioAmplitude.value = 0f
                        playDeactivationSound()
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val text = matches?.firstOrNull()?.trim().orEmpty()
                        Log.d(TAG, "Speech recognized: $text")

                        if (text.isNotBlank()) {
                            // Check wake word
                            if (wakeWordEnabled && (text.contains("hey lodi bhai", ignoreCase = true) ||
                                        text.contains("lodi bhai", ignoreCase = true) ||
                                        text.contains("lodi", ignoreCase = true))) {
                                onWakeWordDetected()
                            }
                            onSpeechRecognized(text)
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        val partial = matches?.firstOrNull()?.trim().orEmpty()
                        if (partial.isNotBlank()) {
                            _audioAmplitude.value = 0.6f
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize SpeechRecognizer", e)
        }
    }

    fun startListening() {
        stopSpeaking() // Instant interruption
        playActivationSound()

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        }

        try {
            speechRecognizer?.startListening(intent)
            _isListening.value = true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start listening", e)
            _isListening.value = false
            onError("Could not start microphone: ${e.localizedMessage}")
        }
    }

    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (e: Exception) {
            Log.w(TAG, "Error stopping listening", e)
        }
        _isListening.value = false
        _audioAmplitude.value = 0f
    }

    fun speak(text: String) {
        if (!isTtsInitialized || text.isBlank()) {
            return
        }

        // Stop any current speech
        stopSpeaking()

        // Clean any tool tags or markdown before speech
        val cleanedText = text
            .replace(Regex("\\[TOOL:[^\\]]+\\]"), "")
            .replace(Regex("[*#_`~]"), "")
            .trim()

        if (cleanedText.isBlank()) return

        textToSpeech?.setSpeechRate(speechRate)
        textToSpeech?.setPitch(speechPitch)

        val utteranceId = "MPRO_${System.currentTimeMillis()}"
        textToSpeech?.speak(cleanedText, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    fun stopSpeaking() {
        if (_isSpeaking.value) {
            try {
                textToSpeech?.stop()
            } catch (e: Exception) {
                Log.w(TAG, "Error stopping TTS", e)
            }
            _isSpeaking.value = false
            _audioAmplitude.value = 0f
            onSpeakingFinished()
        }
    }

    fun playActivationSound() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 80)
        } catch (e: Exception) {
            Log.w(TAG, "Could not play activation tone", e)
        }
    }

    fun playDeactivationSound() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 70)
        } catch (e: Exception) {
            Log.w(TAG, "Could not play deactivation tone", e)
        }
    }

    fun playInterruptionSound() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 90)
        } catch (e: Exception) {
            Log.w(TAG, "Could not play interrupt tone", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsInitialized = true
            textToSpeech?.language = Locale.US
            textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                    _audioAmplitude.value = 0.75f
                    onSpeakingStarted()
                }

                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                    _audioAmplitude.value = 0f
                    onSpeakingFinished()
                }

                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                    _audioAmplitude.value = 0f
                    onSpeakingFinished()
                }
            })
        } else {
            Log.e(TAG, "Failed to initialize TextToSpeech: status=$status")
        }
    }

    fun release() {
        try {
            speechRecognizer?.destroy()
            textToSpeech?.stop()
            textToSpeech?.shutdown()
            toneGenerator?.release()
        } catch (e: Exception) {
            Log.w(TAG, "Error releasing audio engine", e)
        }
    }
}
