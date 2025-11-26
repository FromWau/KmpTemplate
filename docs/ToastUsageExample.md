# Toast System Usage

## Overview

The toast system uses SharedFlow for centralized, event-driven toast notifications throughout the app.

## Architecture

- **ToastMessage**: Data class defining toast content and styling
- **ToastService**: Singleton service with SharedFlow to emit toast events
- **ToastHost**: Composable that collects and displays toasts (placed in App.kt)

## Benefits

- ✅ Centralized toast management
- ✅ No duplicate toasts stacking
- ✅ Proper event consumption with SharedFlow
- ✅ Easy to use from ViewModels
- ✅ Automatic queuing of multiple toasts
- ✅ Customizable duration and styling per toast type

## Usage in ViewModels

### Inject ToastService

```kotlin
class MyViewModel(
    private val toastService: ToastService,
) : ViewModel() {
    // ...
}
```

### Show toasts

```kotlin
// Success toast
viewModelScope.launch {
    toastService.showSuccess("Operation completed successfully")
}

// Error toast
viewModelScope.launch {
    toastService.showError("Failed to complete operation")
}

// Warning toast
viewModelScope.launch {
    toastService.showWarning("Please check your input")
}

// Info toast
viewModelScope.launch {
    toastService.showInfo("New features available")
}

// Custom toast with duration
viewModelScope.launch {
    toastService.showToast(
        ToastMessage(
            message = "Custom message",
            duration = 5.seconds,
            type = ToastType.SUCCESS
        )
    )
}
```

## Toast Types

- **SUCCESS**: Green/primary color scheme
- **ERROR**: Red/error color scheme
- **WARNING**: Orange/tertiary color scheme
- **INFO**: Blue/secondary color scheme

## Example: SettingsViewModel

```kotlin
viewModelScope.launch {
    val result = settingsRepository.upsertSettings(settings)

    if (result) {
        toastService.showSuccess("Settings saved successfully")
    } else {
        toastService.showError("Failed to save settings")
    }
}
```

## Customization

### Using ToastDefaults

The toast system follows Material3 design patterns with a `ToastDefaults` object:

```kotlin
// Access default values
ToastDefaults.shape                // RoundedCornerShape(8.dp)
ToastDefaults.horizontalPadding    // 16.dp
ToastDefaults.verticalPadding      // 12.dp
ToastDefaults.margin               // 16.dp

// Get colors for specific types
val successColors = ToastDefaults.successColors()
val errorColors = ToastDefaults.errorColors()
val warningColors = ToastDefaults.warningColors()
val infoColors = ToastDefaults.infoColors()
```

### Custom Toast Colors

Override default colors by modifying `ToastDefaults`:

```kotlin
@Composable
fun customSuccessColors() = ToastDefaults.successColors(
    containerColor = Color.Green,
    contentColor = Color.White,
)
```

### Custom Shape

Change the toast shape globally by modifying `ToastDefaults.shape`:

```kotlin
object ToastDefaults {
    val shape: Shape = RoundedCornerShape(16.dp) // More rounded
    // or
    val shape: Shape = RoundedCornerShape(
        topStart = 16.dp,
        topEnd = 16.dp,
        bottomStart = 0.dp,
        bottomEnd = 0.dp
    )
    // ...
}
```

### Custom Padding/Margins

Adjust spacing in `ToastDefaults`:

```kotlin
object ToastDefaults {
    val horizontalPadding: Dp = 24.dp  // Wider
    val verticalPadding: Dp = 16.dp    // Taller
    val margin: Dp = 32.dp             // More space from edge
    // ...
}
```

### Animation Customization

To customize animations, edit `ToastHost.kt`:

```kotlin
AnimatedVisibility(
    visible = visible && currentToast != null,
    enter = fadeIn() + slideInVertically(initialOffsetY = { it }),  // Slide from bottom
    exit = fadeOut() + slideOutVertically(targetOffsetY = { it }),
) {
    // ...
}
```

### Position Customization

Change toast position in `ToastHost.kt`:

```kotlin
Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.TopCenter,  // Top of screen
    // or Alignment.Center, Alignment.TopStart, etc.
) {
    // ...
}
```

## Advanced: Creating Custom Toast Themes

You can create your own toast theme by extending the defaults:

```kotlin
// Create a custom toast theme
object MyAppToastDefaults {
    val shape = RoundedCornerShape(12.dp)
    val horizontalPadding = 20.dp
    val verticalPadding = 14.dp

    @Composable
    fun brandColors() = ToastColors(
        containerColor = Color(0xFF1E88E5),
        contentColor = Color.White,
    )
}

// Use in a custom toast composable
@Composable
fun BrandToast(message: String) {
    Box(
        modifier = Modifier
            .padding(ToastDefaults.margin)
            .background(
                color = MyAppToastDefaults.brandColors().containerColor,
                shape = MyAppToastDefaults.shape,
            )
            .padding(
                horizontal = MyAppToastDefaults.horizontalPadding,
                vertical = MyAppToastDefaults.verticalPadding,
            ),
    ) {
        Text(
            text = message,
            color = MyAppToastDefaults.brandColors().contentColor,
        )
    }
}
```

## Implementation Details

- SharedFlow has `extraBufferCapacity = 1` to buffer one toast if another is displaying
- Toasts auto-dismiss after their duration
- Animation duration is 300ms
- Only one toast shows at a time; additional toasts are queued
- `ToastColors` is an `@Immutable` data class for performance
- All defaults follow Material3 design guidelines
