# MPro AI — Installation & Wireless Mobile Deployment Guide

Welcome to **MPro AI** (Personality: **Lodi Bhai**), created for Owner & Administrator **Chandan Kumar**.

This guide shows you how to install this app onto your Android phone directly from Google AI Studio without any USB cables, command-line terminal commands, or developer USB debugging.

---

## ⚡ Method 1: Automatic GitHub Actions APK Trigger (Zero Cable, Zero Coding)

We have configured an automated GitHub Actions workflow (`.github/workflows/build-apk.yml`) that automatically compiles your APK in the cloud whenever code is synced to GitHub.

### Step 1: Connect & Push to GitHub from AI Studio
1. In Google AI Studio, look at the top right header menu or the project settings icon.
2. Click **"Push to GitHub"** (or **"Export to GitHub"**).
3. Select or enter your GitHub account and repository name (e.g., `MPro-AI`).
4. Click **Confirm / Push**.

### Step 2: Cloud Build Triggers Automatically
1. Go to your repository on [GitHub](https://github.com).
2. Click the **"Actions"** tab at the top of your repository.
3. You will see a workflow running named **"Build Android APK Automatically"**.
4. The cloud runner will check out the project, configure Java and Gradle, restore the signing credentials, and build the APK in ~1–2 minutes.

### Step 3: Download and Install on Your Android Phone
1. Open your mobile browser (e.g. Chrome on your Android phone).
2. Navigate to your GitHub repository's **Actions** tab.
3. Tap on the latest green (successful) run.
4. Scroll down to the **Artifacts** section at the bottom.
5. Tap **`MPro-AI-Debug-APK`** to download the APK zip directly to your phone.
6. Open your phone's **Downloads** folder or Files app, extract the zip, and tap **`app-debug.apk`**.
7. If Android prompts "Install unknown apps", tap **Settings** and allow Chrome or your Files app to install, then tap **Install**.
8. Done! **MPro AI** will appear on your home screen with its custom neon emblem launcher icon.

---

## 📲 Method 2: Direct APK Download via AI Studio Settings Menu

If you don't want to use GitHub, Google AI Studio has a built-in export option:
1. In AI Studio, click the **Settings (gear icon)** or the project three-dot menu `⋮`.
2. Look for **"Generate APK / Export"** or **"Download APK"**.
3. Download the `.apk` file directly to your phone or computer and share it via Google Drive, WhatsApp, or Telegram to your phone.
4. Tap the file on your phone to install.

---

## 🤖 Features in MPro AI (Lodi Bhai)
- **Real-Time Voice Assistant**: Powered by Firebase AI Logic (`gemini-3.8-flash`) and Speech-to-Text.
- **Wake Word**: Say *"Hey Lodi Bhai"* or tap the central holographic microphone.
- **Instant Interruption**: Tap the mic anytime while Lodi Bhai is speaking to immediately pause or interrupt.
- **Smart Tools Executed on Device**:
  - `openWebsite()` — Opens any website in your browser.
  - `searchWeb()` — Direct Google search.
  - `openYouTube()` — Opens YouTube searches for videos, talks, and music.
  - `launchApp()` — Finds and launches installed apps on your device.
  - `generateIdea()` — Creates strategic project blueprints inspired by **Sir Ratan Tata** and **Dr. APJ Abdul Kalam**.
- **Command Dashboard**: Personal user metrics, system status (latency, memory, session ID), and quick action controls.
- **Interactive History**: View, replay, or copy full session conversation logs.
