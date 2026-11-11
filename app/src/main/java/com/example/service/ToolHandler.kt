package com.example.service

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import com.example.model.GeneratedIdea
import com.example.model.ToolCall

private const val TAG = "ToolHandler"

class ToolHandler(private val context: Context) {

    fun executeTool(toolName: String, args: Map<String, String>): ToolCall {
        Log.d(TAG, "Executing tool: $toolName with args: $args")
        return when (toolName.lowercase()) {
            "openwebsite" -> executeOpenWebsite(args)
            "searchweb" -> executeSearchWeb(args)
            "openyoutube" -> executeOpenYouTube(args)
            "launchapp" -> executeLaunchApp(args)
            "generateidea" -> executeGenerateIdea(args)
            else -> ToolCall(
                name = toolName,
                arguments = args,
                result = "Unknown command or unsupported action.",
                isSuccess = false
            )
        }
    }

    private fun executeOpenWebsite(args: Map<String, String>): ToolCall {
        val rawUrl = args["url"] ?: args["query"] ?: "https://google.com"
        val formattedUrl = if (!rawUrl.startsWith("http://") && !rawUrl.startsWith("https://")) {
            "https://$rawUrl"
        } else {
            rawUrl
        }
        return try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(formattedUrl)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            ToolCall(
                name = "openWebsite",
                arguments = args,
                result = "Successfully opened $formattedUrl in your browser.",
                isSuccess = true
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error opening website: $formattedUrl", e)
            ToolCall(
                name = "openWebsite",
                arguments = args,
                result = "Failed to launch browser: ${e.localizedMessage}",
                isSuccess = false
            )
        }
    }

    private fun executeSearchWeb(args: Map<String, String>): ToolCall {
        val query = args["query"] ?: "Technology and Innovation"
        return try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=${Uri.encode(query)}")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            ToolCall(
                name = "searchWeb",
                arguments = args,
                result = "Executed Google search for '$query'.",
                isSuccess = true
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error in web search", e)
            ToolCall(
                name = "searchWeb",
                arguments = args,
                result = "Search failed: ${e.localizedMessage}",
                isSuccess = false
            )
        }
    }

    private fun executeOpenYouTube(args: Map<String, String>): ToolCall {
        val query = args["query"] ?: ""
        return try {
            val url = if (query.isNotBlank()) {
                "https://www.youtube.com/results?search_query=${Uri.encode(query)}"
            } else {
                "https://www.youtube.com"
            }
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            ToolCall(
                name = "openYouTube",
                arguments = args,
                result = if (query.isNotBlank()) "Opened YouTube search for '$query'." else "Opened YouTube.",
                isSuccess = true
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error opening YouTube", e)
            ToolCall(
                name = "openYouTube",
                arguments = args,
                result = "Could not open YouTube: ${e.localizedMessage}",
                isSuccess = false
            )
        }
    }

    private fun executeLaunchApp(args: Map<String, String>): ToolCall {
        val targetName = (args["appname"] ?: args["name"] ?: "").trim().lowercase()
        if (targetName.isBlank()) {
            return ToolCall(
                name = "launchApp",
                arguments = args,
                result = "App name not provided.",
                isSuccess = false
            )
        }

        val pm: PackageManager = context.packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)

        // Find best match by label or package name
        var matchedPackage: String? = null
        var matchedLabel: String? = null

        for (app in packages) {
            val label = pm.getApplicationLabel(app).toString().lowercase()
            val pkg = app.packageName.lowercase()
            if (label == targetName || label.contains(targetName) || pkg.contains(targetName)) {
                if (pm.getLaunchIntentForPackage(app.packageName) != null) {
                    matchedPackage = app.packageName
                    matchedLabel = pm.getApplicationLabel(app).toString()
                    break
                }
            }
        }

        return if (matchedPackage != null) {
            try {
                val launchIntent = pm.getLaunchIntentForPackage(matchedPackage)?.apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                if (launchIntent != null) {
                    context.startActivity(launchIntent)
                    ToolCall(
                        name = "launchApp",
                        arguments = args,
                        result = "Launched $matchedLabel ($matchedPackage).",
                        isSuccess = true
                    )
                } else {
                    ToolCall(
                        name = "launchApp",
                        arguments = args,
                        result = "Found $matchedLabel but cannot launch directly.",
                        isSuccess = false
                    )
                }
            } catch (e: Exception) {
                ToolCall(
                    name = "launchApp",
                    arguments = args,
                    result = "Error launching $targetName: ${e.localizedMessage}",
                    isSuccess = false
                )
            }
        } else {
            // Offer to find in Play Store
            try {
                val marketIntent = Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=${Uri.encode(targetName)}")).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(marketIntent)
                ToolCall(
                    name = "launchApp",
                    arguments = args,
                    result = "'$targetName' is not installed locally. Opened Google Play Store to install.",
                    isSuccess = true
                )
            } catch (e: Exception) {
                ToolCall(
                    name = "launchApp",
                    arguments = args,
                    result = "App '$targetName' not found on device.",
                    isSuccess = false
                )
            }
        }
    }

    private fun executeGenerateIdea(args: Map<String, String>): ToolCall {
        val domain = (args["domain"] ?: "Autonomous AI & High Impact Innovation").trim()
        val idea = createVisionaryIdea(domain)
        return ToolCall(
            name = "generateIdea",
            arguments = args,
            result = "Generated Visionary Project: '${idea.title}' in $domain.\nCore: ${idea.coreVision}\nPillars: ${idea.inspirationalPillars}",
            isSuccess = true
        )
    }

    fun createVisionaryIdea(domain: String): GeneratedIdea {
        val timestamp = System.currentTimeMillis()
        val title = when {
            domain.contains("ai", ignoreCase = true) || domain.contains("tech", ignoreCase = true) ->
                "AeroNeural: Decentralized Edge AI Network for Empowering Rural Connectivity"
            domain.contains("education", ignoreCase = true) || domain.contains("learn", ignoreCase = true) ->
                "PrernaVidya: AI Mentorship Pods Inspired by Kalam's Wings of Fire"
            domain.contains("health", ignoreCase = true) || domain.contains("bio", ignoreCase = true) ->
                "Sanjeevani AI: Low-Cost Portable Quantum Diagnostic Sensor"
            domain.contains("energy", ignoreCase = true) || domain.contains("green", ignoreCase = true) ->
                "SolarisGrid: Clean Community Micro-Grids with Ethical Tata Supply Chain Standards"
            else ->
                "NexGen Vanguard: Social Impact Innovation Engine for Young Engineers"
        }

        return GeneratedIdea(
            title = title,
            domain = domain,
            coreVision = "Building scalable, ethical technology that solves foundational human challenges while creating employment and uplifting youth.",
            inspirationalPillars = "Sir Ratan Tata's relentless integrity & social stewardship + Dr. APJ Abdul Kalam's ignite-the-mind scientific optimism.",
            actionSteps = listOf(
                "Phase 1: Build minimum lovable prototype with local edge hardware.",
                "Phase 2: Validate user impact directly with community stakeholders.",
                "Phase 3: Scale sustainably with zero compromise on ethics and quality."
            ),
            timestamp = timestamp
        )
    }
}
