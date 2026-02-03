package com.example.kmp_template.shared_client.core.permission

class NativePermissionService : PermissionService {
    override suspend fun requestPermission(permission: Permission): Boolean {
        // On native platforms, permissions are always granted
        return true
    }

    override fun hasPermission(permission: Permission): Boolean {
        // On native platforms, permissions are always granted
        return true
    }
}
