package com.example.kmp_template.core.config.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class LoggingConfig(
    val level: LogLevel = LogLevel.INFO,
    val format: LogFormat = LogFormat.TEXT,
    val logToFile: Boolean = true,
    val logDir: String? = null,
    val consoleEnabled: Boolean = true,
    val colorEnabled: Boolean = true,
) : Config {
    @Transient
    override val name: String = "logging"

    override fun toToml(): String {
        val possibleLogs = LogLevel.entries.joinToString { "\"${it.name}\"" }
        val possibleFormats = LogFormat.entries.joinToString { "\"${it.name}\"" }
        val logDirLine = if (logDir != null) {
            "logDir = \"$logDir\""
        } else {
            "#logDir = <LOG_DIRECTORY_PATH>"
        }

        return """
            |[$name]
            |# Possible log levels: $possibleLogs
            |level = "$level"
            |# Possible log formats: $possibleFormats
            |format = "$format"
            |# Enable or disable logging to file
            |logToFile = $logToFile
            |# Directory to store log files. If not set, the default application log directory is used.
            |$logDirLine
            |# Enable or disable console logging
            |consoleEnabled = $consoleEnabled
            |# Enable or disable colored output in console
            |colorEnabled = $colorEnabled
        """.trimMargin("|")
    }
}

@Serializable
enum class LogLevel {
    VERBOSE,
    DEBUG,
    INFO,
    WARN,
    ERROR
}

@Serializable
enum class LogFormat {
    TEXT,
    JSON,
}
