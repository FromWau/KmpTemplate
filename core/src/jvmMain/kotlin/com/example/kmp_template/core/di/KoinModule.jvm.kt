package com.example.kmp_template.core.di

import com.example.kmp_template.core.SystemAppDirectories
import com.example.kmp_template.core.database.DatabaseFactory
import com.example.kmp_template.core.network.HttpClientFactory
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.core.module.Module
import org.koin.dsl.module

actual val coreModule: Module
    get() = module {
        single<HttpClientEngine> { OkHttp.create() }
        single<HttpClient> { HttpClientFactory.create(get()) }
        single<SystemAppDirectories> { SystemAppDirectories() }
        single<DatabaseFactory> { DatabaseFactory(get()) }
    }
