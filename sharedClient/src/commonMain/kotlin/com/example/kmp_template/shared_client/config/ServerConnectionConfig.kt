package com.example.kmp_template.shared_client.config

import com.example.kmp_template.core.config.model.Config
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ServerConnectionConfig(
    val host: String = "localhost",
    val port: Int = 8080,
) : Config {
    @Transient
    override val name: String = "server"

    override val defaultContent: String
        get() {
            return """
                |[${name}]
                |host = "$host"
                |port = $port
            """.trimMargin("|")
        }
}
