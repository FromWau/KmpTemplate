package com.example.kmp_template.shared_client.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

@Composable
expect fun getColorScheme(darkTheme: Boolean, dynamicColor: Boolean): ColorScheme
