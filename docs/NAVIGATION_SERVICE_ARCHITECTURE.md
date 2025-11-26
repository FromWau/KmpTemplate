# Navigation Service Architecture

## Overview

The final navigation architecture uses a **centralized NavigationService** that ViewModels can inject and use for navigation. This is the cleanest MVI implementation with true unidirectional data flow.

## Architecture Diagram

```
┌──────────────┐
│   UI Layer   │
└──────────────┘
       │
       │ User Action
       ↓
┌──────────────┐        ┌────────────────────┐
│  ViewModel   │───────→│ NavigationService  │
└──────────────┘ inject └────────────────────┘
       │                         │
       │ State                   │ Commands
       ↓                         ↓
┌──────────────┐        ┌────────────────────┐
│   UI Layer   │        │  NavigationHost    │
│  (observe)   │        │  (NavController)   │
└──────────────┘        └────────────────────┘
```

## Key Components

### 1. NavigationCommand

Sealed interface representing all possible navigation actions:

```kotlin
sealed interface NavigationCommand {
    data object Back : NavigationCommand
    data class To(val route: Route) : NavigationCommand
    data class ToAndClearUpTo(
        val route: Route,
        val clearUpTo: Route,
        val inclusive: Boolean = false
    ) : NavigationCommand
    data class ToAndClearAll(val route: Route) : NavigationCommand
}
```

**Benefits:**
- Type-safe navigation
- Exhaustive when expressions
- Easy to add new navigation patterns

### 2. NavigationService

Injectable singleton service that manages navigation:

```kotlin
class NavigationService {
    private val _commands = Channel<NavigationCommand>(Channel.BUFFERED)
    val commands: Flow<NavigationCommand> = _commands.receiveAsFlow()

    fun to(route: Route) {
        _commands.trySend(NavigationCommand.To(route))
    }

    fun back() {
        _commands.trySend(NavigationCommand.Back)
    }

    // ... more navigation methods
}
```

**Key Features:**
- Single source of truth for navigation
- Channel-based for one-time events
- Buffered to prevent command loss
- Simple API: `nav.to(route)`, `nav.back()`

### 3. NavigationHost

Composable wrapper that observes NavigationService and executes navigation:

```kotlin
@Composable
fun <T : Any> NavigationHost(
    navController: NavHostController,
    startDestination: T,
    navigationService: NavigationService = koinInject(),
    builder: NavGraphBuilder.() -> Unit,
) {
    LaunchedEffect(navigationService) {
        navigationService.commands.collectLatest { command ->
            when (command) {
                is NavigationCommand.To -> navController.navigate(command.route)
                NavigationCommand.Back -> navController.navigateUp()
                // ... handle other commands
            }
        }
    }

    NavHost(navController, startDestination, builder = builder)
}
```

**Benefits:**
- Encapsulates NavController interaction
- Auto-injects NavigationService
- Centralizes navigation execution
- Drop-in replacement for NavHost

## Usage Examples

### ViewModel Usage

```kotlin
class HomeViewModel(
    private val nav: NavigationService,  // Injected via Koin
) : ViewModel() {

    fun onAction(action: HomeAction) {
        when (action) {
            HomeAction.OnShowSettingsClicked -> {
                // Business logic here (analytics, validation, etc.)
                nav.to(Route.Settings)
            }

            HomeAction.OnLogoutClicked -> {
                viewModelScope.launch {
                    userRepository.logout()
                    nav.toAndClearAll(Route.Login)
                }
            }
        }
    }
}
```

**Clean API:**
- ✅ `nav.to(route)` - Navigate to a route
- ✅ `nav.back()` - Navigate back
- ✅ No manual effect/event management
- ✅ No LaunchedEffect in ViewModel

### Screen Usage

```kotlin
@Composable
fun HomeScreenRoot(
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    HomeScreen(
        state = state,
        onAction = viewModel::onAction,  // Simple delegation
    )
}
```

**Simplified:**
- ❌ No effect observation
- ❌ No navigation callbacks
- ❌ No when expressions for navigation
- ✅ Just state and actions

### App Setup

```kotlin
@Composable
fun App() {
    val navController = rememberNavController()

    NavigationHost(  // Drop-in replacement for NavHost
        navController = navController,
        startDestination = Route.Graph,
    ) {
        composable<Route.Home> { HomeScreenRoot() }
        composable<Route.Settings> { SettingsScreenRoot() }
    }
}
```

**Clean:**
- NavigationService auto-injected
- No manual navigation wiring
- Screens are self-contained

## Comparison: Before vs After

### Before (Effect Pattern)

```kotlin
// ViewModel
class HomeViewModel : ViewModel() {
    private val _effects = EffectChannel<NavigationEffect>()
    val effects = _effects.effects

    fun onAction(action: HomeAction) {
        viewModelScope.launch {
            _effects.send(NavigationEffect.NavigateTo(Route.Settings))
        }
    }
}

// Screen
@Composable
fun HomeScreenRoot(
    viewModel: HomeViewModel = koinViewModel(),
    onNavigate: (NavigationEffect.NavigateTo) -> Unit,
) {
    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect ->
            when (effect) {
                is NavigationEffect.NavigateTo -> onNavigate(effect)
            }
        }
    }
    // ...
}

// App
composable<Route.Home> {
    HomeScreenRoot(
        onNavigate = { effect -> navController.navigate(effect.route) }
    )
}
```

### After (NavigationService)

```kotlin
// ViewModel
class HomeViewModel(private val nav: NavigationService) : ViewModel() {
    fun onAction(action: HomeAction) {
        nav.to(Route.Settings)  // That's it!
    }
}

// Screen
@Composable
fun HomeScreenRoot(
    viewModel: HomeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    HomeScreen(state, viewModel::onAction)
}

// App
NavigationHost(navController, Route.Graph) {
    composable<Route.Home> { HomeScreenRoot() }
}
```

**Lines of code:**
- Before: ~40 lines
- After: ~10 lines
- **75% reduction!**

## Benefits

### 1. Simplicity
- ✅ No effect management in ViewModels
- ✅ No effect observation in screens
- ✅ No navigation callbacks in App
- ✅ Simple `nav.to()` API

### 2. Testability
```kotlin
@Test
fun `OnShowSettingsClicked navigates to settings`() = runTest {
    val navService = NavigationService()
    val viewModel = HomeViewModel(navService)
    val commands = mutableListOf<NavigationCommand>()

    launch { navService.commands.collect { commands.add(it) } }

    viewModel.onAction(HomeAction.OnShowSettingsClicked)

    assertEquals(NavigationCommand.To(Route.Settings), commands.first())
}
```

### 3. Centralized Navigation Logic
- Analytics tracking
- Deep link handling
- A/B testing navigation flows
- Global navigation guards

### 4. Dependency Injection
- Easy to mock for testing
- Can swap implementations
- Clear dependency graph

### 5. Scalability
```kotlin
// Easy to add new navigation patterns
fun NavigationService.toWithAnimation(route: Route, animation: NavAnim) {
    _commands.trySend(NavigationCommand.ToWithAnimation(route, animation))
}

// Easy to add interceptors
class AnalyticsNavigationService(
    private val delegate: NavigationService,
    private val analytics: Analytics,
) : NavigationService {
    override fun to(route: Route) {
        analytics.trackNavigation(route)
        delegate.to(route)
    }
}
```

## Advanced Patterns

### 1. Navigation with Result

```kotlin
// Define result type
data class SettingsResult(val mediaPath: String)

// Navigate and await result
suspend fun NavigationService.navigateForResult<T>(
    route: Route
): T? {
    // Implementation using saved state handle
}

// Usage in ViewModel
viewModelScope.launch {
    val result = nav.navigateForResult<SettingsResult>(Route.Settings)
    result?.let { handleSettingsResult(it) }
}
```

### 2. Conditional Navigation

```kotlin
fun onSettingsClicked() {
    if (userHasPermission) {
        nav.to(Route.Settings)
    } else {
        nav.to(Route.PermissionDenied)
    }
}
```

### 3. Navigation Guards

```kotlin
class GuardedNavigationService(
    private val delegate: NavigationService,
    private val authService: AuthService,
) : NavigationService {
    override fun to(route: Route) {
        if (route.requiresAuth && !authService.isLoggedIn) {
            delegate.to(Route.Login)
        } else {
            delegate.to(route)
        }
    }
}
```

### 4. Analytics Integration

```kotlin
class AnalyticsNavigationService(
    private val delegate: NavigationService,
    private val analytics: Analytics,
) : NavigationService {
    override fun to(route: Route) {
        analytics.logScreen(route.screenName)
        delegate.to(route)
    }
}
```

## Testing

### Unit Test ViewModel

```kotlin
@Test
fun `user can navigate to settings`() = runTest {
    val navService = NavigationService()
    val viewModel = HomeViewModel(navService)

    val commands = mutableListOf<NavigationCommand>()
    val job = launch { navService.commands.collect { commands.add(it) } }

    viewModel.onAction(HomeAction.OnShowSettingsClicked)

    assertEquals(
        NavigationCommand.To(Route.Settings),
        commands.first()
    )

    job.cancel()
}
```

### Integration Test

```kotlin
@Test
fun `navigation service executes commands`() = runTest {
    val navService = NavigationService()
    val navController = TestNavController()

    // Simulate NavigationHost logic
    launch {
        navService.commands.collect { command ->
            when (command) {
                is NavigationCommand.To -> navController.navigate(command.route)
            }
        }
    }

    navService.to(Route.Settings)

    assertEquals(Route.Settings, navController.currentRoute)
}
```

## Migration Guide

### Step 1: Add NavigationService to Koin

```kotlin
val sharedModules = module {
    single { NavigationService() }
}
```

### Step 2: Inject into ViewModels

```kotlin
class MyViewModel(
    private val nav: NavigationService,  // Add this
) : ViewModel()
```

### Step 3: Replace Effect Emissions

```kotlin
// Before
_effects.send(NavigationEffect.NavigateTo(Route.Settings))

// After
nav.to(Route.Settings)
```

### Step 4: Simplify Screens

```kotlin
// Before
@Composable
fun MyScreenRoot(
    viewModel: MyViewModel,
    onNavigate: (NavigationEffect.NavigateTo) -> Unit,
) {
    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest { effect -> ... }
    }
}

// After
@Composable
fun MyScreenRoot(
    viewModel: MyViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    MyScreen(state, viewModel::onAction)
}
```

### Step 5: Use NavigationHost

```kotlin
// Before
NavHost(navController, startDestination) { ... }

// After
NavigationHost(navController, startDestination) { ... }
```

## Best Practices

1. **Keep actions semantic**: `OnShowSettingsClicked`, not `NavigateToSettings`
2. **Put business logic in ViewModel**: Validation, analytics, etc. before navigation
3. **Use type-safe routes**: Leverage Kotlin's type system
4. **Test navigation**: Navigation is business logic, test it!
5. **Don't expose NavController**: Keep it in NavigationHost only
6. **Single NavigationService instance**: Register as singleton in DI

## Conclusion

The NavigationService pattern provides:
- ✅ **Simplest API**: `nav.to(route)`
- ✅ **Clean separation**: ViewModels don't know about UI framework
- ✅ **Testable**: Easy to verify navigation logic
- ✅ **Centralized**: Single place for navigation concerns
- ✅ **Scalable**: Easy to add features (analytics, guards, etc.)
- ✅ **True MVI**: Unidirectional data flow maintained

This is the **production-ready** navigation architecture for Compose Multiplatform!
