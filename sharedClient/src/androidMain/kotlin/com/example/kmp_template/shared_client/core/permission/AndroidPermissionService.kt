package com.example.kmp_template.shared_client.core.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class AndroidPermissionService(private val context: Context) : PermissionService {
    private val _pendingRequest = MutableStateFlow<PendingPermissionRequest?>(null)
    val pendingRequest: StateFlow<PendingPermissionRequest?> = _pendingRequest.asStateFlow()

    override suspend fun requestPermission(permission: Permission): Boolean {
        // Check if already granted
        if (hasPermission(permission)) {
            return true
        }

        // Request permission and suspend until result is available
        return suspendCancellableCoroutine { continuation ->
            _pendingRequest.value = PendingPermissionRequest(
                permission = permission,
                onResult = { granted ->
                    _pendingRequest.value = null
                    continuation.resume(granted)
                }
            )

            continuation.invokeOnCancellation {
                _pendingRequest.value = null
            }
        }
    }

    override fun hasPermission(permission: Permission): Boolean {
        val androidPermissions = permission.toAndroidPermissions()
        return androidPermissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
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
}

data class PendingPermissionRequest(
    val permission: Permission,
    val onResult: (Boolean) -> Unit
)
