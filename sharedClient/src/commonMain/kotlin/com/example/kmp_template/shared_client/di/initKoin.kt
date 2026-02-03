package com.example.kmp_template.shared_client.di

import com.example.kmp_template.core.di.coreModule
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

fun initKoin(config: KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(coreModule, sharedModules, sharedPlatformModules, viewModelModules)
    }
}
