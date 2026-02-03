package com.example.kmp_template.shared_client.config

import com.example.kmp_template.core.config.model.Config
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class SettingConfig(
    val settings: Map<String, String> = emptyMap(),
) : Config {
    @Transient
    override val name: String = "setting"

    override val defaultContent: String
        get() {
            return """
                |# [$name]
                |# Application settings
                |# Key-value pairs for application configuration
                |# Example:
                |# theme=dark
        """.trimMargin("|")
        }
}