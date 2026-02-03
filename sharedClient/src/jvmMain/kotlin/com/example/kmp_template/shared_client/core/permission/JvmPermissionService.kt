package com.example.kmp_template.shared_client.core.permission

class JvmPermissionService : PermissionService {
    override suspend fun requestPermission(permission: Permission): Boolean {
        // On JVM/Desktop, permissions are always granted
        return true
    }

    override fun hasPermission(permission: Permission): Boolean {
        // On JVM/Desktop, permissions are always granted
        return true
    }
}
