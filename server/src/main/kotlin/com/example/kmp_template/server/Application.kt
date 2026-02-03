package com.example.kmp_template.server

import com.example.kmp_template.core.logger.Log
import com.example.kmp_template.core.SystemAppDirectories
import com.example.kmp_template.core.config.ConfigLoader
import com.example.kmp_template.server.config.ServerConfig
import com.example.kmp_template.server.plugins.configureKoin
import com.example.kmp_template.server.plugins.configureRouting
import com.example.kmp_template.server.plugins.configureSerialization
import com.example.kmp_template.server.plugins.configureTrailingSlashRedirect
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.rpc.krpc.ktor.server.Krpc
import com.example.kmp_template.core.result.Result

private const val TAG = "Application"

fun main() {
    val systemDirs = SystemAppDirectories()
    val configLoader = ConfigLoader(systemDirs)

    val config = when (val result = configLoader.load(ServerConfig())) {
        is Result.Success -> result.data
        is Result.Error -> {
            Log.tag(TAG).w { "No config file found, creating default at: ${systemDirs.configDir()}/server.toml" }
            val default = ServerConfig()

            configLoader.saveDefaults(default)
            when (val reloaded = configLoader.load(default)) {
                is Result.Success -> reloaded.data
                is Result.Error -> {
                    Log.tag(TAG).e { "Failed to create config file, using in-memory defaults" }
                    default
                }
            }
        }
    }

    Log.initialize(config.logging, systemDirs)
    Log.tag(TAG).i { "Server configuration loaded" }

    System.setProperty("io.ktor.development", config.server.developmentMode.toString())

    embeddedServer(
        Netty,
        host = config.server.host,
        port = config.server.port,
    ) {
        configureKoin(config)
        install(Krpc)
        configureSerialization()
        configureTrailingSlashRedirect()
        configureRouting()
    }.start(wait = true)
}
