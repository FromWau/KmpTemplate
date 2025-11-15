package com.example.kmp_template.shared_client

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

@Composable
actual fun RequestPermission(
    permission: Permission,
    onPermissionResult: (Boolean) -> Unit,
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = onPermissionResult
    )

    LaunchedEffect(Unit) {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(context, permission.toAndroidPermission()) -> {
                onPermissionResult(true)
            }

            else -> {
                launcher.launch(permission.toAndroidPermission())
            }
        }
    }
}

private fun Permission.toAndroidPermission(): String {
    return when (this) {
        Permission.NETWORK -> Manifest.permission.INTERNET
    }
}