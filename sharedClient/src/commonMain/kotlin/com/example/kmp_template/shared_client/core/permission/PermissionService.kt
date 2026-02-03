package com.example.kmp_template.shared_client.core.permission

interface PermissionService {
    /**
     * Request a permission and suspend until the result is available.
     * @return true if permission was granted, false if denied
     */
    suspend fun requestPermission(permission: Permission): Boolean

    /**
     * Check if a permission is currently granted without requesting it.
     * @return true if permission is granted, false otherwise
     */
    fun hasPermission(permission: Permission): Boolean
}
