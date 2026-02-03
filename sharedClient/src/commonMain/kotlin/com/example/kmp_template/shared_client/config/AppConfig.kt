package com.example.kmp_template.shared_client.config

import com.example.kmp_template.core.config.model.Config
import com.example.kmp_template.core.config.model.LoggingConfig
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class AppConfig(
    @SerialName("logging")
    val logging: LoggingConfig = LoggingConfig(),
    @SerialName("server")
    val server: ServerConnectionConfig = ServerConnectionConfig(),
) : Config {
    @Transient
    override val name: String = "app"

    override fun toToml(): String {
        return """
            |${logging.toToml()}
            |
            |${server.toToml()}
        """.trimMargin("|")
    }
}