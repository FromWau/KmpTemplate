package com.example.kmp_template.server.plugins

import io.ktor.server.application.Application
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.application.install
import io.ktor.server.request.path
import io.ktor.server.request.queryString
import io.ktor.server.response.respondRedirect

fun Application.configureTrailingSlashRedirect() {
    install(TrailingSlashRedirect)
}

val TrailingSlashRedirect = createApplicationPlugin(name = "TrailingSlashRedirect") {
    onCall { call ->
        val path = call.request.path()
        if (path.length > 1 && path.endsWith("/")) {
            val noSlash = path.removeSuffix("/")
            val query = call.request.queryString()
            val redirectUrl = if (query.isEmpty()) noSlash else "$noSlash?$query"
            call.respondRedirect(redirectUrl, permanent = true)
            return@onCall
        }
    }
}