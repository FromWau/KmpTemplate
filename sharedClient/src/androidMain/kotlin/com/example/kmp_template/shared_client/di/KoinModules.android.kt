package com.example.kmp_template.shared_client.di

import com.example.kmp_template.shared_client.core.permission.AndroidPermissionService
import com.example.kmp_template.shared_client.core.permission.PermissionService
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

actual val sharedPlatformModules: Module
    get() = module {
        singleOf(::AndroidPermissionService) bind PermissionService::class
    }