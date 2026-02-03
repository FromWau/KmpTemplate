package com.example.kmp_template.core.logger

import com.example.kmp_template.core.config.model.LogFormat
import com.example.kmp_template.core.config.model.LoggingConfig

// ANSI color codes
private const val RESET = "\u001B[0m"
private const val RED = "\u001B[31m"
private const val YELLOW = "\u001B[33m"
private const val BLUE = "\u001B[34m"
private const val WHITE = "\u001B[37m"
private const val GRAY = "\u001B[90m"

actual val Log: Logger = object : Logger() {
    override fun doLog(
        logEntry: LogEntry,
        config: LoggingConfig,
    ) {
        val rawOutput = when (config.format) {
            LogFormat.JSON -> logEntry.toJsonString()
            LogFormat.TEXT -> logEntry.toTextString()
        }

        val output = if (config.colorEnabled) {
            colorizeLog(
                level = logEntry.logLevel,
                logString = rawOutput
            )
        } else {
            rawOutput
        }

        println(output)
    }

    private fun colorizeLog(
        level: LogLevel,
        logString: String,
    ): String {
        val color = when (level) {
            LogLevel.VERBOSE -> GRAY
            LogLevel.DEBUG -> BLUE
            LogLevel.INFO -> WHITE
            LogLevel.WARN -> YELLOW
            LogLevel.ERROR -> RED
        }

        return "$color${logString}$RESET"
    }
}