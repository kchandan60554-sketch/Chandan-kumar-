package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.components.BootAnimationOverlay
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.VoiceCoreScreen
import com.example.ui.theme.DarkNavy
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.ElectricPurple
import com.example.ui.theme.GlassCardBorder
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.configureAppCheck
import com.example.viewmodel.AssistantViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: AssistantViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Mandatory App Check configuration before UI or ViewModels
        configureAppCheck(this, intent)

        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: AssistantViewModel) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Audio recording permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startListening()
        }
    }

    fun requestMicAndListen() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            viewModel.onMicTapped()
        } else {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        val err = uiState.errorMessage
        if (!err.isNullOrBlank()) {
            snackbarHostState.showSnackbar(err)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            contentWindowInsets = WindowInsets.navigationBars,
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                if (uiState.isBooted) {
                    MProBottomNav(
                        activeTab = uiState.activeTab,
                        onTabSelected = { viewModel.setActiveTab(it) }
                    )
                }
            },
            containerColor = DeepObsidian
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (uiState.activeTab) {
                    0 -> VoiceCoreScreen(
                        uiState = uiState,
                        onMicTapped = { requestMicAndListen() },
                        onQuickPrompt = { prompt ->
                            if (prompt.contains("Lodi Bhai", ignoreCase = true)) {
                                requestMicAndListen()
                            } else if (prompt.contains("Idea", ignoreCase = true)) {
                                viewModel.generateNewVisionaryIdea("High Impact Social Innovation")
                            } else if (prompt.contains("YouTube", ignoreCase = true)) {
                                viewModel.executeQuickTool("openYouTube", mapOf("query" to "Ratan Tata and Kalam innovation"))
                            } else {
                                viewModel.executeQuickTool("searchWeb", mapOf("query" to "Future of Artificial Intelligence"))
                            }
                        },
                        onInterrupt = { viewModel.interruptSpeech() }
                    )
                    1 -> DashboardScreen(
                        uiState = uiState,
                        onExecuteTool = { toolName, args ->
                            viewModel.executeQuickTool(toolName, args)
                        },
                        onGenerateIdea = { domain ->
                            viewModel.generateNewVisionaryIdea(domain)
                        },
                        onSwitchTab = { viewModel.setActiveTab(it) }
                    )
                    2 -> HistoryScreen(
                        messages = uiState.messages,
                        onReplaySpeech = { viewModel.speakText(it) },
                        onClearHistory = { viewModel.clearHistory() }
                    )
                    3 -> SettingsScreen(
                        uiState = uiState,
                        onSpeechRateChanged = { viewModel.setSpeechRate(it) },
                        onSpeechPitchChanged = { viewModel.setSpeechPitch(it) },
                        onWakeWordToggled = { viewModel.toggleWakeWord(it) },
                        onClearHistory = { viewModel.clearHistory() }
                    )
                }
            }
        }

        // Boot Animation Overlay
        AnimatedVisibility(
            visible = !uiState.isBooted,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            BootAnimationOverlay(bootStep = uiState.bootStep)
        }
    }
}

@Composable
fun MProBottomNav(
    activeTab: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = DarkNavy,
        tonalElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
            .border(
                1.dp,
                GlassCardBorder.copy(alpha = 0.35f),
                RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            )
    ) {
        NavigationBarItem(
            selected = activeTab == 0,
            onClick = { onTabSelected(0) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Mic,
                    contentDescription = "Voice Assistant",
                    modifier = Modifier.size(22.dp)
                )
            },
            label = {
                Text(
                    text = "VOICE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DeepObsidian,
                selectedTextColor = NeonCyan,
                indicatorColor = NeonCyan,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
            ),
            modifier = Modifier.testTag("nav_voice")
        )

        NavigationBarItem(
            selected = activeTab == 1,
            onClick = { onTabSelected(1) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Dashboard,
                    contentDescription = "Command Dashboard",
                    modifier = Modifier.size(22.dp)
                )
            },
            label = {
                Text(
                    text = "DASHBOARD",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DeepObsidian,
                selectedTextColor = ElectricPurple,
                indicatorColor = ElectricPurple,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
            ),
            modifier = Modifier.testTag("nav_dashboard")
        )

        NavigationBarItem(
            selected = activeTab == 2,
            onClick = { onTabSelected(2) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.History,
                    contentDescription = "Session History",
                    modifier = Modifier.size(22.dp)
                )
            },
            label = {
                Text(
                    text = "HISTORY",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DeepObsidian,
                selectedTextColor = NeonCyan,
                indicatorColor = NeonCyan,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
            ),
            modifier = Modifier.testTag("nav_history")
        )

        NavigationBarItem(
            selected = activeTab == 3,
            onClick = { onTabSelected(3) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings",
                    modifier = Modifier.size(22.dp)
                )
            },
            label = {
                Text(
                    text = "SETTINGS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = DeepObsidian,
                selectedTextColor = ElectricPurple,
                indicatorColor = ElectricPurple,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
            ),
            modifier = Modifier.testTag("nav_settings")
        )
    }
}
