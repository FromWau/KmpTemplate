package com.example.kmp_template.core.logger

import com.example.kmp_template.core.config.model.LogFormat
import com.example.kmp_template.core.config.model.LoggingConfig
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSLog

@OptIn(ExperimentalForeignApi::class)
actual val Log: Logger = object : Logger() {
    override fun doLog(
        logEntry: LogEntry,
        config: LoggingConfig,
    ) {
        val output = when (config.format) {
            LogFormat.JSON -> logEntry.toJsonString()
            LogFormat.TEXT -> logEntry.toTextString()
        }

        NSLog(output)
    }
}