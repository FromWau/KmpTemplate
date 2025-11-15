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

Each feature follows Clean Architecture with three layers:

- **Domain**: Business logic, models, repository interfaces
- **Data**: Repository implementations, database entities & DAOs, mappers
- **Presentation**: ViewModels (MVI pattern), Compose UI screens

RPC communication uses shared interfaces in `sharedRpc/`, implemented on the server and consumed by clients via generated proxies.
