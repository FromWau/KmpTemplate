package com.example.kmp_template.shared_client.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.window.core.layout.WindowSizeClass

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    ProvideDeviceType {
        MaterialTheme(
            colorScheme = getColorScheme(darkTheme, dynamicColor),
            typography = AppTypography,
        ) {
            content()
        }
    }
}


@Composable
private fun ProvideDeviceType(content: @Composable () -> Unit) {
    val sizeClass = currentWindowAdaptiveInfo().windowSizeClass

    val deviceType by remember(sizeClass) {
        mutableStateOf(getDeviceType(sizeClass))
    }

    CompositionLocalProvider(LocalDeviceType provides deviceType) {
        content()
    }
}

private fun getDeviceType(windowSizeClass: WindowSizeClass): DeviceType {
    // WIDTH CLASSES
    val isCompactWidth =
        !windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

    val isMediumWidth =
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND) &&
                !windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

    val isExpandedWidth =
        windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

    // HEIGHT CLASSES
    val isCompactHeight =
        !windowSizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND)

    val isMediumHeight =
        windowSizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND) &&
                !windowSizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_EXPANDED_LOWER_BOUND)

    val isExpandedHeight =
        windowSizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_EXPANDED_LOWER_BOUND)

    return when {
        // MOBILE PORTRAIT
        isCompactWidth && isMediumHeight -> DeviceType.MOBILE_PORTRAIT
        isCompactWidth && isExpandedHeight -> DeviceType.MOBILE_PORTRAIT

        // MOBILE LANDSCAPE
        isExpandedWidth && isCompactHeight -> DeviceType.MOBILE_LANDSCAPE

        // TABLET PORTRAIT
        isMediumWidth && isExpandedHeight -> DeviceType.TABLET_PORTRAIT

        // TABLET LANDSCAPE
        isExpandedWidth && isMediumHeight -> DeviceType.TABLET_LANDSCAPE

        // FALLBACK
        else -> DeviceType.DESKTOP
    }
}