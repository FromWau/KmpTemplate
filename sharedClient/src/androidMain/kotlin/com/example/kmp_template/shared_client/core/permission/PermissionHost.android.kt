package com.example.kmp_template.shared_client.core.permission

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.koinInject

@Composable
actual fun PermissionHost() {
    val permissionService = koinInject<AndroidPermissionService>()
    val context = LocalContext.current
    val pendingRequest by permissionService.pendingRequest.collectAsStateWithLifecycle()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissionsMap ->
            val currentRequest = pendingRequest
            if (currentRequest != null) {
                val allGranted = permissionsMap.values.all { it }
                currentRequest.onResult(allGranted)
            }
        }
    )

    LaunchedEffect(pendingRequest) {
        val request = pendingRequest ?: return@LaunchedEffect

        val permissions = request.permission.toAndroidPermissions()

        // Check if already granted
        val allGranted = permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

        if (allGranted) {
            request.onResult(true)
        } else {
            launcher.launch(permissions)
        }
    }
}

private fun Permission.toAndroidPermissions(): Array<String> {
    return when (this) {
        Permission.NETWORK -> arrayOf(Manifest.permission.INTERNET)
        Permission.READ_MEDIA -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayOf(
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_VIDEO
                )
            } else {
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }
}
