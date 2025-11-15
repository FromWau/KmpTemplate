package com.example.kmp_template.server.plugins

import com.example.kmp_template.core.logger.Log
import com.example.kmp_template.server.feature.routing.featureRoutes
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing


fun Application.configureRouting() {
    routing {
        get("/") {
            Log.tag("routing").i { "Health check endpoint triggered" }
            call.respond(HttpStatusCode.OK, "OK")
        }

        featureRoutes()
    }
}