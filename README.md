# 💳 Spendify — Modern Personal Finance & Budget Tracker

Spendify is a state-of-the-art personal finance management app featuring modern Glassmorphic dark aesthetics, offline-first Room database storage, live Google Cloud Firestore synchronization, custom categories, smart budgeting with real-time alerts, spending analytics, and an integrated multi-device Wi-Fi Web Portal.

---

## ✨ Features

- **🎨 Premium Dark Glassmorphism UI**: Built with Jetpack Compose, Material 3, and rich ambient glow shaders.
- **⚡ Offline-First & Real-time Cloud Sync**:
  - Local persistence powered by **Room Database** & Kotlin Coroutines/Flow.
  - Seamless two-way cloud sync with **Google Cloud Firestore**.
- **📊 Analytics & Visual Breakdown**: Dynamic spending breakdown by category, weekly/monthly trends, and income vs expense summaries.
- **🎯 Smart Budget Management**: Monthly category limits with dynamic progress bars, threshold alerts, and status indicators.
- **📁 Multi-Format Export**: Export financial data to CSV, PDF, or JSON reports.
- **🌐 Built-in Web Portal & APK Wi-Fi Server**: Local Node.js server to run Spendify on any desktop or mobile browser and download/install the Android APK directly over Wi-Fi.

---

## 🛠️ Tech Stack

- **Android / Native Frontend**: Kotlin, Jetpack Compose, Material 3, Navigation Compose, Coroutines & Flow
- **Architecture**: Clean Architecture (Data, Domain, Presentation / MVI / MVVM)
- **Local Persistence**: Android Room Database (SQLite)
- **Cloud Backend**: Google Firebase (Authentication & Cloud Firestore)
- **Web Portal**: HTML5, Vanilla CSS3 (Custom Glassmorphism Design System), JavaScript (ES6+), Node.js HTTP Server

---

## 🚀 Getting Started

### 1. Android App Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/PRATHAM-BURAN/spendify.git
   cd spendify
   ```
2. Open the project in Android Studio (Ladybug / Iguana or newer).
3. Ensure Android SDK 35 is installed.
4. Build and install on your emulator or connected physical device:
   ```powershell
   .\gradlew.bat assembleDebug
   adb install -r app/build/outputs/apk/debug/app-debug.apk
   ```

### 2. Web Portal & Wi-Fi APK Server
To start the local Wi-Fi web portal and download server:
```bash
node web_portal/server.js
```
Open `http://localhost:8088` (or `http://<your-ip>:8088` on your phone).

---

## 📄 License
This project is open source and available under the [MIT License](LICENSE).
