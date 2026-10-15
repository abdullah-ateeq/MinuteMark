# MinuteMark ⏱️

**MinuteMark** is a modern, privacy-focused Android utility app built with **Jetpack Compose** and **Material 3**. It effortlessly converts clock times into total elapsed minutes and seconds—and performs reverse duration-to-time calculations with support for multi-day offsets.

---

## 🌟 Key Features

### 🕒 Time to Value (`TIME → VALUE`)
* **Clock Time Conversion**: Select or enter any time (12-hour AM/PM format) to instantly calculate total minutes and total seconds elapsed since midnight.
* **Decimal Breakdown**: Calculates precise decimal hours (e.g., `1.50 hrs`) and decimal minutes.
* **Seconds Precision**: Toggle optional seconds field for exact high-precision timing.
* **Visual Progress Timeline**: Interactive 24-hour progress bar visualizing the time's position across the day.
* **Quick Presets**: Instant one-tap preset selection:
  * 🕒 **Now** (Current system time)
  * 🌅 **Start of Day** (12:00 AM)
  * ☀️ **Noon** (12:00 PM)
  * 💼 **Work End** (05:00 PM)
  * 🌙 **End of Day** (11:59 PM)

### 🔄 Value to Time (`VALUE → TIME`)
* **Reverse Conversion**: Enter total minutes or total seconds to compute the corresponding clock time.
* **Multi-day Offset Support**: Handles duration values exceeding 24 hours (1,440+ minutes) and clearly displays day offset wrap indicators (e.g., `+1 day`).
* **Duration Presets**: Quick selection chips for standard durations:
  * `15 min`, `30 min`, `60 min`, `90 min`, `120 min`, `1440 min` (1 day)

### 📜 Calculation History
* **Auto-saving History**: Automatically retains up to 10 recent conversions.
* **One-tap Restoration**: Tap any past entry in history to reload it directly into the active calculation tab.
* **Swipe / Individual & Batch Clear**: Flexible deletion with optional confirmation dialogs.

### 🛡️ Privacy & 100% Offline
* **Zero Network Permissions**: MinuteMark does not request `android.permission.INTERNET`.
* **Complete Privacy**: Zero tracking, analytics, ads, or external server calls. All preferences and history remain strictly on your device.

### 🎨 Modern Material 3 UI & Customization
* **Dynamic Color Support**: Seamlessly adopts device wallpaper colors on Android 12+ (API 31+).
* **Flexible Themes**: Choose between Light, Dark, or System Default themes.
* **Haptic Feedback & Animations**: Optional tactile haptic feedback on button presses and smooth animated transition toggles.
* **Copy & Share**: Quick one-tap result clipboard copy and system sharing.

---

## 🛠️ Architecture & Tech Stack

MinuteMark is architected following Google's official Android architecture guidance (**MVVM / Clean Architecture**):

* **Language**: Kotlin `2.2.10`
* **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Material 3 components
* **Architecture**: Unidirectional Data Flow (UDF), `ViewModel`, `StateFlow`, Clean Domain Logic
* **Navigation**: Jetpack Navigation Compose
* **Data Storage**: `SharedPreferences` with GSON serialization
* **Build System**: Gradle `9.4.1` (Kotlin DSL)
* **SDK Version**:
  * Minimum SDK: `26` (Android 8.0)
  * Target SDK: `37` (Android 16)

---

## 📂 Project Structure

```
com.example.minutemark/
├── data/
│   ├── model/         # State data classes (MinuteMarkUiState, UserSettings, etc.)
│   └── repository/    # History & Settings persistence implementations
├── domain/
│   ├── MinuteMarkCalculator.kt  # Pure calculation math engine
│   ├── MinuteMarkFormatter.kt   # Formatting utilities for 12/24hr, decimals & units
│   └── MinuteMarkValidator.kt   # Input range and format validation logic
├── ui/
│   ├── about/         # About screen & privacy guarantee details
│   ├── components/    # Reusable Compose widgets (ResultCard, TimelineBar, PresetChips)
│   ├── history/       # History list & restoration screen
│   ├── home/          # Primary calculation workspace & conversion tabs
│   ├── navigation/    # Compose Navigation graph & bottom bar layout
│   ├── settings/      # Customization preferences & theme selection
│   ├── splash/        # Animated brand launch screen
│   ├── theme/         # Color palettes, typography, and Material 3 theme wrapper
│   └── viewmodel/     # MinuteMarkViewModel orchestrating UI state & actions
```

---

## 🚀 Getting Started

### Prerequisites
* **Android Studio**: Ladybug (2024.2.1) or newer recommended.
* **JDK**: Version 11 or higher.
* **Android SDK**: API level 37 installed.

### Building & Running

1. **Clone the repository**:
   ```bash
   git clone https://github.com/abdullah-ateeq/MinuteMark.git
   cd MinuteMark
   ```

2. **Build Debug APK**:
   ```bash
   ./gradlew assembleDebug
   ```

3. **Run Unit Tests**:
   ```bash
   ./gradlew test
   ```

---

## 📄 License

Distributed under the MIT License. See `LICENSE` for more details.

---

## 👤 Author

Developed by **[Abdullah Ateeq](https://github.com/abdullah-ateeq)**.
