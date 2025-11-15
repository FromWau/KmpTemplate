package com.example.kmp_template.shared_client

import androidx.compose.runtime.Composable

enum class Permission {
    NETWORK,
}

@Composable
expect fun RequestPermission(permission: Permission, onPermissionResult: (Boolean) -> Unit)