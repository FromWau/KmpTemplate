package com.example.kmp_template.shared_client

import androidx.compose.runtime.Composable

@Composable
actual fun RequestPermission(
    permission: Permission,
    onPermissionResult: (Boolean) -> Unit,
) {
    // No-op on native platforms
    onPermissionResult(true)
}