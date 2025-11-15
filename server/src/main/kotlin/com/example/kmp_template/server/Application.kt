package com.example.kmp_template.server

import com.example.kmp_template.server.plugins.configureKoin
import com.example.kmp_template.server.plugins.configureRouting
import com.example.kmp_template.server.plugins.configureSerialization
import com.example.kmp_template.server.plugins.configureTrailingSlashRedirect
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.netty.EngineMain
import kotlinx.rpc.krpc.ktor.server.Krpc

fun main(args: Array<String>) = EngineMain.main(args)


fun Application.module() {
    configureKoin()
    install(Krpc)
    configureSerialization()
    configureTrailingSlashRedirect()
    configureRouting()
}
