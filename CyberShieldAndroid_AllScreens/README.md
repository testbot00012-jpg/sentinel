# CyberShield — All Screens Android Frontend

This is a Kotlin + Jetpack Compose frontend/navigation implementation for the CyberShield consumer Android product. It registers the complete 174-screen information architecture and connects screens through Navigation Compose.

## Structure
- `MainActivity.kt` — app entry point
- `ui/CyberShieldApp.kt` — navigation graph and screen implementations
- `ui/AppComponents.kt` — shared glassmorphism UI components
- `ui/theme/Theme.kt` — dark security theme
- `model/ScreenRegistry.kt` — all screen routes and parent groups
- `NAVIGATION_MAP.md` — complete ordered hierarchy

## Build
Open the folder in Android Studio and sync Gradle. The project uses AGP 8.7.3, Kotlin 2.0.21, Compose BOM 2024.12.01, minSdk 26 and targetSdk 35.

No Gradle wrapper is bundled in this generated source package, so I have not claimed a successful local build in this environment.

## Backend boundary
The screens are frontend/navigation implementations. Detection engines, AI models, Firebase authentication/Firestore, real-time monitoring, complaint workflows, evidence storage, and privileged Android capabilities must be connected separately with explicit permissions and consent.
