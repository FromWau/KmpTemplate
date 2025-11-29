package com.example.kmp_template.shared_client.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable

@Composable
@ReadOnlyComposable
actual fun getColorScheme(
    darkTheme: Boolean,
    dynamicColor: Boolean,
): ColorScheme = when {
    // TODO: Implement dynamic color for desktop
    darkTheme -> darkScheme
    else -> lightScheme
}