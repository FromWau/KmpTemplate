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
    @SerialName("setting")
    val settingConfig: SettingConfig = SettingConfig(),
    @SerialName("server")
    val server: ServerConnectionConfig = ServerConnectionConfig(),
) : Config {
    @Transient
    override val name: String = "app"

    override val defaultContent: String
        get() {
            return """
                |${logging.defaultContent}
                |
                |${settingConfig.defaultContent}
                |
                |${server.defaultContent}
                |
        """.trimMargin("|")
        }
}