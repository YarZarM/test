# MyApp - Android Kotlin Project

A complete Android Studio project built with modern Android development practices.

## Features

- **Jetpack Compose** - Modern declarative UI toolkit
- **MVVM Architecture** - ViewModel pattern for separation of concerns
- **Navigation Compose** - Type-safe navigation between screens
- **Retrofit** - HTTP client for API calls
- **Coroutines** - Asynchronous programming
- **DataStore** - Modern data storage solution

## Project Structure

```
app/
├── src/
│   └── main/
│       ├── java/com/example/myapp/
│       │   ├── data/
│       │   │   ├── local/          # DataStore implementation
│       │   │   ├── model/          # Data models
│       │   │   ├── remote/         # Retrofit API service
│       │   │   └── repository/     # Repository pattern
│       │   ├── navigation/         # Navigation setup
│       │   ├── ui/
│       │   │   ├── screens/        # Compose screens
│       │   │   ├── theme/          # Theme configuration
│       │   │   └── viewmodel/      # ViewModels
│       │   └── MainActivity.kt
│       └── res/                     # Resources
└── build.gradle.kts
```

## Setup

1. Open the project in Android Studio (Hedgehog or later)
2. Sync Gradle files
3. Build and run the app

## API Configuration

The app uses JSONPlaceholder API by default. To change the base URL, edit:
`app/src/main/java/com/example/myapp/data/remote/RetrofitClient.kt`

## Dependencies

- Compose BOM: 2023.10.01
- Kotlin: 1.9.20
- Compile SDK: 34
- Min SDK: 24
- Target SDK: 34

