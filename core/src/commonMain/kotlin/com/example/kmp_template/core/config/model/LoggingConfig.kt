package com.example.kmp_template.core.config.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class LoggingConfig(
    val level: LogLevel = LogLevel.VERBOSE,
    val format: LogFormat = LogFormat.TEXT,
    val logToFile: Boolean = true,
    val logDir: String? = null,
    val consoleEnabled: Boolean = true,
    val colorEnabled: Boolean = true,
) : Config {
    @Transient
    override val name: String = "logging"

    override val defaultContent: String
        get() {
            val possibleLogs = LogLevel.entries.fold("") { acc, lvl -> "$acc\"${lvl.name}\", " }
            val possibleFormats = LogFormat.entries.fold("") { acc, fmt -> "$acc\"${fmt.name}\", " }

            return """
            |[${name}]
            |# Possible log levels: $possibleLogs
            |level = "INFO"
            |# Possible log formats: $possibleFormats
            |format = "TEXT"
            |# Enable or disable logging to file
            |logToFile = true
            |# Directory to store log files. If not set, the default application log directory is used.
            |#logDir = <LOG_DIRECTORY_PATH>
            |# Enable or disable console logging
            |consoleEnabled = true
            |# Enable or disable colored output in console
            |colorEnabled = true
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
