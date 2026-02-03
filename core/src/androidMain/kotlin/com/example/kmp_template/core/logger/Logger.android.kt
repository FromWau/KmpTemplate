package com.example.kmp_template.core.logger

import com.example.kmp_template.core.config.model.LoggingConfig
import android.util.Log as AndroidLog

actual val Log: Logger = object : Logger() {
    override fun doLog(
        logEntry: LogEntry,
        config: LoggingConfig,
    ) {
        when (logEntry.logLevel) {
            LogLevel.VERBOSE -> AndroidLog.v(logEntry.tag, logEntry.message)
            LogLevel.DEBUG -> AndroidLog.d(logEntry.tag, logEntry.message)
            LogLevel.INFO -> AndroidLog.i(logEntry.tag, logEntry.message)
            LogLevel.WARN -> AndroidLog.w(logEntry.tag, logEntry.message)
            LogLevel.ERROR -> AndroidLog.e(logEntry.tag, logEntry.message)
        }
    }
}
