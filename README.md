# Premium Stopwatch App ⏱️

A modern, high-precision stopwatch application built for Android using **Jetpack Compose** and **Material 3**. This project demonstrates a clean, reactive architecture with a focus on premium aesthetics and smooth user experience.

## ✨ Features

- **Precision Timing**: Accurate tracking of elapsed time down to milliseconds.
- **Lap History**:
  - **Live Active Lap**: View the current lap time updating in real-time at the top of the list.
  - **Auto-Scroll**: Newest laps are always visible thanks to automatic scrolling logic.
  - **Clean List**: A scrollable history of all recorded laps.
- **Dynamic Theming**:
  - **Toggle-able Dark/Light Mode**: Switch themes instantly from the app bar.
  - **System Sync**: Defaults to the system preference but allows manual override.
  - **Glassmorphism**: Elegant UI elements with transparency and blur effects.
- **Modern UI/UX**:
  - Large, readable typography.
  - Subtle micro-animations for play/pause and reset actions.
  - Custom adaptive app icon with a sleek teal stopwatch design.

## 🛠️ Tech Stack

- **Language**: [Kotlin](https://kotlinlang.org/)
- **UI Toolkit**: [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Design System**: [Material 3](https://m3.material.io/)
- **Architecture**: MVVM (Model-View-ViewModel)
- **State Management**: `StateFlow` and `MutableStateFlow`
- **Asynchronicity**: Kotlin Coroutines
- **Build System**: Gradle (Kotlin DSL)

## 🏗️ Architecture

The app follows the recommended **Android Architecture Guidelines**:

- **UI Layer**: `StopwatchScreen` is a pure Composable that renders the state provided by the ViewModel.
- **ViewModel**: `StopwatchViewModel` holds the business logic, manages the timer coroutine, and exposes the `StopwatchState` via a `StateFlow`.
- **State**: `StopwatchState` is an immutable data class representing the single source of truth for the UI.

## 🚀 Getting Started

1.  **Clone the repository**:
    ```bash
    git clone https://github.com/sahmedroni/stopwatch.git
    ```
2.  **Open in Android Studio**:
    - Open Android Studio and select "Open an existing Project".
    - Navigate to the cloned directory.
3.  **Build and Run**:
    - Wait for Gradle sync to complete.
    - Connect an Android device or start an emulator.
    - Click the **Run** button (green arrow).

## 🎨 Design

The app uses a custom color palette centered around **Deep Teal** and **Pale Teal**, providing a calm yet professional look.

- **Primary**: Teal for accents and progress indicators.
- **Surface**: Dark/Light variants with transparency for a modern feel.
- **Animations**:
  - Smooth progress circle animation.
  - Fade and scale transitions for control buttons.

## 📝 License

This project is open-source and available under the MIT License.
