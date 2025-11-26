package com.example.kmp_template.shared_client.theme

import androidx.compose.runtime.compositionLocalOf

val LocalDeviceType = compositionLocalOf<DeviceType> {
    error("No DeviceType provided! Make sure AppTheme is wrapping your composables.")
}
