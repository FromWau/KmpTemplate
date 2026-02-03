package com.example.kmp_template.server.config

import com.example.kmp_template.core.config.model.Config
import com.example.kmp_template.core.config.model.LoggingConfig
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ServerConfig(
    val logging: LoggingConfig = LoggingConfig(),
    val server: ServerNetworkConfig = ServerNetworkConfig(),
) : Config {
    @Transient
    override val name: String = "server"

    override val defaultContent: String
        get() {
            return """
                |${logging.defaultContent}
                |
                |[server]
                |developmentMode = ${server.developmentMode}
                |host = "${server.host}"
                |port = ${server.port}
                |maxConnections = ${server.maxConnections}
                |requestTimeoutSeconds = ${server.requestTimeoutSeconds}
            """.trimMargin("|")
        }
}

@Serializable
data class ServerNetworkConfig(
    val developmentMode: Boolean = false,
    val host: String = "0.0.0.0",
    val port: Int = 8080,
    val maxConnections: Int = 100,
    val requestTimeoutSeconds: Int = 30,
)
