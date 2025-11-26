# Kotlin Multiplatform Template

A production-ready Kotlin Multiplatform template with Compose Multiplatform, featuring a full-stack setup with client applications (Android, iOS, Desktop) and a Ktor server with type-safe RPC communication.

## Getting Started

After creating a new repository from this template, rename the project to your desired name:

```bash
./rename-project.sh
```

The script will prompt you for:
- **Project name** (e.g., `MyAwesomeApp`) - used for class names and identifiers
- **App display name** (e.g., `My Awesome App`) - shown to users
- **Package domain** (e.g., `com.example`) - your package namespace

The script automatically handles all case conversions (PascalCase, camelCase, snake_case, kebab-case) and renames:
- All source files and directories
- Package names and namespaces
- Build configurations
- iOS project settings
- Class and file names

## Features

- **Multi-platform Support**: Android, iOS, Desktop (JVM), and Server
- **Compose Multiplatform**: Shared UI across all client platforms
- **Clean Architecture**: Separation of Domain, Data, and Presentation layers
- **Type-safe RPC**: Client-server communication using kotlinx-rpc
- **Room Database**: Multiplatform local persistence
- **Dependency Injection**: Koin for DI across all platforms
- **Modern UI**: Material 3 theming with dynamic colors
- **Comprehensive Logging**: Platform-aware logging system
- **HTTP Client**: Pre-configured Ktor client with logging and JSON serialization
- **NavigationService**: Clean, testable navigation pattern with injectable service

## Project Structure

```
KmpTemplate/
├── core/                  # Shared foundation (database, logging, networking)
├── sharedRpc/             # RPC contracts shared between client & server
├── sharedClient/          # Shared client business logic & UI
├── composeApp/            # Platform-specific app entry points
├── server/                # Ktor server application
└── iosApp/                # iOS SwiftUI wrapper
```

## Running the Applications

### Android
```bash
./gradlew :composeApp:installDebug
```

### Desktop
```bash
./gradlew :composeApp:run
```

### iOS
Open `iosApp/iosApp.xcodeproj` in Xcode and run, or:
```bash
./gradlew :composeApp:iosSimulatorArm64Test
```

### Server
```bash
./gradlew :server:run
```
Server runs on `http://localhost:8080`

## Key Technologies

- **Kotlin 2.2.0**
- **Compose Multiplatform 1.8.2**
- **Ktor 3.2.3** (client & server)
- **Koin 4.1.0** (dependency injection)
- **Room 2.7.2** (database)
- **kotlinx-rpc 0.9.1** (RPC)
- **kotlinx-coroutines 1.10.2**
- **kotlinx-serialization 1.9.0**

## Architecture

### Clean Architecture Layers

Each feature follows Clean Architecture with three layers:

- **Domain**: Business logic, models, repository interfaces
- **Data**: Repository implementations, database entities & DAOs, mappers
- **Presentation**: ViewModels (MVI pattern), Compose UI screens

RPC communication uses shared interfaces in `sharedRpc/`, implemented on the server and consumed by clients via generated proxies.

### Navigation Architecture

This template uses a **NavigationService** pattern for clean, testable, and scalable navigation:

#### NavigationService - Injectable Singleton

```kotlin
class NavigationService {
    fun to(route: Route)                    // Navigate to route
    fun back()                               // Navigate back
    fun toAndClearUpTo(route, clearUpTo)    // Clear back stack
    fun toAndClearAll(route)                 // Reset navigation
}
```

#### ViewModel Usage

ViewModels inject `NavigationService` and use simple API calls:

```kotlin
class HomeViewModel(
    private val nav: NavigationService,  // Injected via Koin
) : ViewModel() {
    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.OnShowSettingsClicked -> {
                // Add business logic (analytics, validation, etc.)
                nav.to(Route.Settings)  // Simple!
            }
            HomeAction.OnBackClicked -> nav.back()
        }
    }
}
```

#### Screen Simplicity

Screens are ultra-clean with no navigation callbacks:

```kotlin
@Composable
fun HomeScreenRoot(
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    HomeScreen(
        state = state,
        onAction = viewModel::onAction,
    )
}
```

#### App Setup

Use `NavigationHost` wrapper that auto-observes NavigationService:

```kotlin
@Composable
fun App() {
    val navController = rememberNavController()

    NavigationHost(
        navController = navController,
        startDestination = Route.Graph,
    ) {
        composable<Route.Home> { HomeScreenRoot() }
        composable<Route.Settings> { SettingsScreenRoot() }
    }
}
```

#### Benefits

- ✅ **Simple API**: `nav.to(route)` instead of manual effect management
- ✅ **Testable**: Easy to mock NavigationService in unit tests
- ✅ **Centralized**: Add analytics, guards, deep links in one place
- ✅ **No Boilerplate**: No LaunchedEffect, callbacks, or when expressions
- ✅ **True MVI**: Pure unidirectional data flow maintained

#### Documentation

For complete details, see [Navigation Service Architecture](docs/NAVIGATION_SERVICE_ARCHITECTURE.md)
